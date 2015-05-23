package org.objectquery.persistence.engine.impl;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.PersistenceException;
import org.objectquery.persistence.engine.PersistenceKeeper;
import org.objectquery.persistence.engine.PersistentObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavassistClassFactory implements ClassFactory {

	private static final Logger logger = LoggerFactory.getLogger(JavassistClassFactory.class);
	private static final String IMPL_CLASS_SUFFIX = "$Impl";
	private ClassPool classPoll;
	private ClassLoader classLoader;
	private Map<String, MetaClass> metadata = new HashMap<String, MetaClass>();

	public JavassistClassFactory() {
		this(null);
	}

	public JavassistClassFactory(ClassLoader classLoader) {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		this.classLoader = classLoader;
		classPoll = new ClassPool();
		classPoll.appendClassPath(new LoaderClassPath(classLoader));
		addMetadata(new MetaClass("java.lang.Byte", true, Byte.class));
		addMetadata(new MetaClass("java.lang.Character", true, Character.class));
		addMetadata(new MetaClass("java.lang.Boolean", true, Boolean.class));
		addMetadata(new MetaClass("java.lang.Short", true, Short.class));
		addMetadata(new MetaClass("java.lang.Integer", true, Integer.class));
		addMetadata(new MetaClass("java.lang.Long", true, Long.class));
		addMetadata(new MetaClass("java.lang.Float", true, Float.class));
		addMetadata(new MetaClass("java.lang.Double", true, Double.class));
		addMetadata(new MetaClass("java.util.Date", true, null));
		addMetadata(new MetaClass("java.time.LocalDate", true, null));
		addMetadata(new MetaClass("java.lang.String", true, null));
		addMetadata(new MetaClass(Byte.TYPE.getName(), true, Byte.class));
		addMetadata(new MetaClass(Character.TYPE.getName(), true, Character.class));
		addMetadata(new MetaClass(Boolean.TYPE.getName(), true, Boolean.class));
		addMetadata(new MetaClass(Short.TYPE.getName(), true, Short.class));
		addMetadata(new MetaClass(Integer.TYPE.getName(), true, Integer.class));
		addMetadata(new MetaClass(Long.TYPE.getName(), true, Long.class));
		addMetadata(new MetaClass(Float.TYPE.getName(), true, Float.class));
		addMetadata(new MetaClass(Double.TYPE.getName(), true, Double.class));
	}

	public Class<?> getRealClass(Class<?> interfa) {
		if (!interfa.isInterface())
			throw new PersistenceException(interfa + " is not an interfaces");
		return getRealClass(interfa.getName());
	}

	private Class<?> getClassOrNull(String name) {
		try {
			return classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private Class<?> getRealClass(String interName) {
		try {
			Class<?> realClass = getClassOrNull(interName + IMPL_CLASS_SUFFIX);

			if (realClass == null) {
				CtClass newClass = classPoll.getOrNull(interName + IMPL_CLASS_SUFFIX);
				CtClass inter = classPoll.get(interName);
				MetaClass newMetaClass = getOrCreateMetaClass(interName);
				implementInterfaceMetadata(inter, newMetaClass);
				newClass = classPoll.makeClass(interName + IMPL_CLASS_SUFFIX);
				makeRealClass(inter, newMetaClass, newClass);
				realClass = newClass.toClass(classLoader, null);
				newMetaClass.setRealClass(realClass);
				FileOutputStream out = new FileOutputStream("ToInspect.class");
				newClass.toBytecode(new DataOutputStream(out));
				out.close();
				newMetaClass.setType(classLoader.loadClass(interName));
			} else {
				MetaClass metaClass = getOrCreateMetaClass(interName);
				if (metaClass.getRealClass() == null) {
					implementInterfaceMetadata(classPoll.get(interName), metaClass);
					metaClass.setRealClass(realClass);
					metaClass.setType(classLoader.loadClass(interName));
				}
			}
			return realClass;

		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	private void implementInterfaceMetadata(CtClass inter, MetaClass metaClass) throws NotFoundException {
		for (CtClass superInt : inter.getInterfaces()) {
			MetaClass superMeta = getOrCreateMetaClass(superInt.getName());
			if (metaClass.getRealClass() == null)
				getRealClass(superInt.getName());
			metaClass.addSuper(superMeta);
		}
		for (CtMethod method : inter.getDeclaredMethods()) {
			if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
				String fieldName;
				String caseName;
				if (method.getName().startsWith("get")) {
					fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
					caseName = method.getName().substring(3);
				} else {
					fieldName = Character.toLowerCase(method.getName().charAt(2)) + method.getName().substring(3);
					caseName = method.getName().substring(2);
				}
				MetaFieldDec field = metaClass.addField(fieldName, getOrCreateMetaClass(method.getReturnType().getName()));
				field.setGetter(method);
				CtMethod setter;
				setter = getMethodOrNull(inter, "set" + caseName);
				field.setSetter(setter);

				CtClass iterab = classPoll.get(Iterable.class.getName());
				if (method.getReturnType().subclassOf(iterab)) {
					field.setCollection(true);
					field.setAddTo(getMethodOrNull(inter, "addTo" + caseName));
					field.setRemoveFrom(getMethodOrNull(inter, "removeFrom" + caseName));
					field.setCount(getMethodOrNull(inter, "count" + caseName));
					field.setHasIn(getMethodOrNull(inter, "hasIn" + caseName));
				}
			}
		}
		metaClass.initStructures();
	}

	private CtMethod getMethodOrNull(CtClass inter, String methodName) {
		try {
			return inter.getDeclaredMethod(methodName);
		} catch (NotFoundException e) {
			return null;
		}
	}

	private void makeRealClass(CtClass inter, MetaClass metaClass, CtClass newClass) throws CannotCompileException, NotFoundException {
		// Add Persistence Field and relative constructor
		CtClass persKeeper = classPoll.get(PersistenceKeeper.class.getName());
		CtField persistenceField = new CtField(persKeeper, "__$persistence", newClass);
		newClass.addField(persistenceField);
		CtConstructor cons = CtNewConstructor.make(new CtClass[] { persKeeper }, null, "__$persistence = $1;", newClass);
		newClass.addConstructor(cons);

		// Add user defined interface and relative fields.
		newClass.addInterface(inter);
		for (MetaField metaField : metaClass.getFields()) {

			CtClass filedType;
			MetaFieldDec declaration = metaField.getDeclaration();
			if (metaField.isCollection())
				filedType = classPoll.get(Collection.class.getName());
			else
				filedType = classPoll.get(declaration.getType().getName());
			CtField field = getOrCreate(filedType, declaration.getName(), newClass);
			newClass.addMethod(createGetter(field, metaField));
			if (declaration.getSetter() != null)
				newClass.addMethod(createSetter(field, metaField));
			if (metaField.isCollection()) {
				if (declaration.getAddTo() != null)
					newClass.addMethod(createAddTo(field, metaField));
				if (declaration.getRemoveFrom() != null)
					newClass.addMethod(createRemoveFrom(field, metaField));
				if (declaration.getHasIn() != null)
					newClass.addMethod(createHasIn(field, metaField));
				if (declaration.getCount() != null)
					newClass.addMethod(createCount(field, metaField));
			}
		}

		// Add self loader and relative load method;
		CtClass pers = classPoll.get(PersistentObject.class.getName());
		newClass.addInterface(pers);
		CtMethod loadMethod = pers.getDeclaredMethod("load");
		StringBuilder body = new StringBuilder("{");
		for (MetaField metaField : metaClass.getFields()) {
			MetaFieldDec dec = metaField.getDeclaration();

			final String fieldName = dec.getName();
			final String fieldType = dec.getType().getName();
			if (metaField.isPrimitive() && metaField.getDeclaration().getType().getPrimitiveCastClass() != null) {
				String boxType = metaField.getDeclaration().getType().getPrimitiveCastClass().getName();

				body.append("{").append(boxType).append(" var1 = (").append(boxType);
				body.append(")__$persistence.loadField(\"").append(fieldName);
				body.append("\",").append(metaField.getId()).append(");");
				body.append("if (var1 != null) this.").append(fieldName).append("= var1.");
				body.append(fieldType).append("Value();}\n");

			} else {
				body.append(fieldName).append("= (").append(fieldType).append(")__$persistence.loadField(\"").append(fieldName);
				body.append("\",").append(metaField.getId()).append(");\n");
			}
		}
		body.append("}");
		CtMethod loadMethodImpl = CtNewMethod.make(loadMethod.getReturnType(), loadMethod.getName(), loadMethod.getParameterTypes(),
				loadMethod.getExceptionTypes(), body.toString(), newClass);
		newClass.addMethod(loadMethodImpl);
		CtMethod getKeeperMethod = pers.getDeclaredMethod("getKeeper");
		// add getKeeper method.
		CtMethod getKeeperMethodImpl = CtNewMethod.make(getKeeperMethod.getReturnType(), getKeeperMethod.getName(), getKeeperMethod.getParameterTypes(),
				getKeeperMethod.getExceptionTypes(), "return __$persistence;", newClass);
		newClass.addMethod(getKeeperMethodImpl);
		if (logger.isDebugEnabled())
			logger.debug("complete class{}", newClass.toString());

	}

	private CtField getOrCreate(CtClass filedType, String name, CtClass owner) throws CannotCompileException {
		CtField field = null;
		try {
			field = owner.getField(name);
		} catch (NotFoundException e) {

		}
		if (field == null) {
			field = new CtField(filedType, name, owner);
			owner.addField(field);
		}
		return field;
	}

	private CtMethod createGetter(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		StringBuilder body = new StringBuilder("{__$persistence.checkLoad(); return ($r)__$persistence.onFieldRead(\"");
		body.append(field.getName()).append("\",");
		body.append(metaField.getId()).append(",");
		if (metaField.isPrimitive() && metaField.getDeclaration().getType().getPrimitiveCastClass() != null) {
			final String name = metaField.getDeclaration().getType().getPrimitiveCastClass().getName();
			body.append("").append(name).append(".valueOf(");
			body.append(field.getName()).append("));}");
		} else
			body.append(field.getName()).append(");}");
		if (logger.isDebugEnabled())
			logger.debug("create getter {}", body.toString());
		CtMethod method = metaField.getDeclaration().getGetter();
		return CtNewMethod.make(field.getType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body.toString(),
				field.getDeclaringClass());
	}

	private CtMethod createSetter(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		final String body;
		if (metaField.isPrimitive() && metaField.getDeclaration().getType().getPrimitiveCastClass() != null)
			body = codeForLiteralSetter(field, metaField);
		else
			body = codeForSetter(field, metaField);

		if (logger.isDebugEnabled())
			logger.debug("create setter {}", body);
		
		CtMethod method = metaField.getDeclaration().getSetter();
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
	}

	private String codeForSetter(CtField field, MetaField metaField) throws NotFoundException {
		StringBuilder body = new StringBuilder("{__$persistence.checkLoad(); ");
		body.append("this.").append(field.getName()).append("= (");
		body.append(field.getType().getName());
		body.append(")__$persistence.onFieldWrite(\"");
		body.append(field.getName()).append("\",");
		body.append(metaField.getId()).append(",");
		body.append(field.getName());
		body.append(",$1);}");
		return body.toString();
	}

	private String codeForLiteralSetter(CtField field, MetaField metaField) throws NotFoundException {
		final StringBuilder body = new StringBuilder("{__$persistence.checkLoad(); ");
		final String boxTypeName = metaField.getDeclaration().getType().getPrimitiveCastClass().getName();
		final String typeName = field.getType().getName();
		final String fieldName = field.getName();
		body.append(boxTypeName);
		body.append(" var1 = (");
		body.append(boxTypeName);
		body.append(")__$persistence.onFieldWrite(\"");
		body.append(fieldName).append("\",");
		body.append(metaField.getId()).append(",");
		body.append("").append(boxTypeName).append(".valueOf(");
		body.append(fieldName).append(")");
		body.append(", ").append(boxTypeName).append(".valueOf($1));");
		body.append(" if ( var1 != null ){ ");
		body.append(typeName);
		body.append(" var2 = ");
		body.append("var1.").append(typeName).append("Value();");
		body.append("this.").append(fieldName).append("=").append("var2;}}");
		return body.toString();
	}

	private CtMethod createAddTo(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		String body = "{__$persistence.checkLoad();return __$persistence.onAddTo(\"" + field.getName() + "\"," + metaField.getId() + "," + field.getName()
				+ ",$1);}";
		CtMethod method = metaField.getDeclaration().getAddTo();
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
	}

	private CtMethod createRemoveFrom(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		String body = "{__$persistence.checkLoad(); return __$persistence.onRemoveFrom(\"" + field.getName() + "\"," + metaField.getId() + ","
				+ field.getName() + ",$1);}";
		CtMethod method = metaField.getDeclaration().getRemoveFrom();
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
	}

	private CtMethod createHasIn(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		String body = "{__$persistence.checkLoad(); return __$persistence.onHasIn(\"" + field.getName() + "\"," + metaField.getId() + "," + field.getName()
				+ ",$1);}";
		CtMethod method = metaField.getDeclaration().getHasIn();
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
	}

	private CtMethod createCount(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		String body = "{__$persistence.checkLoad(); return __$persistence.onCount(\"" + field.getName() + "\"," + metaField.getId() + "," + field.getName()
				+ ");}";
		CtMethod method = metaField.getDeclaration().getCount();
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
	}

	public MetaClass getClassMetadata(Class<?> clazz) {
		return metadata.get(clazz.getName());
	}

	public void addMetadata(MetaClass meta) {
		metadata.put(meta.getName(), meta);
	}

	private MetaClass getOrCreateMetaClass(String name) {
		MetaClass metaClass = metadata.get(name);
		if (metaClass == null) {
			metaClass = new MetaClass(name);
			this.metadata.put(name, metaClass);
		}
		return metaClass;
	}

}
