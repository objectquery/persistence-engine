package org.objectquery.persistence.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.PersistenceException;
import org.objectquery.persistence.engine.PersistenceKeeper;
import org.objectquery.persistence.engine.SelfLoader;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class JavassistClassFactory implements ClassFactory {

	private static final String IMPL_CLASS_SUFFIX = "$Impl";
	private ClassPool classPoll;
	private Map<String, MetaClass> metadata = new HashMap<String, MetaClass>();

	public JavassistClassFactory() {
		classPoll = ClassPool.getDefault();
	}

	public Class<?> getRealClass(Class<?> interfa) {
		if (!interfa.isInterface())
			throw new PersistenceException(interfa + " is not an interfaces");
		return getRealClass(interfa.getName());
	}

	private Class<?> getRealClass(String interName) {
		try {
			Class<?> realClass;
			CtClass newClass = classPoll.getOrNull(interName + IMPL_CLASS_SUFFIX);
			if (newClass == null) {
				CtClass inter = classPoll.get(interName);
				MetaClass newMetaClass = getOrCreateMetaClass(interName);
				implementInterfaceMetadata(inter, newMetaClass);
				newClass = classPoll.makeClass(interName + IMPL_CLASS_SUFFIX);
				makeRealClass(inter, newMetaClass, newClass);
				realClass = newClass.toClass();
				newMetaClass.setRealClass(realClass);
			} else {
				realClass = classPoll.getClassLoader().loadClass(interName + IMPL_CLASS_SUFFIX);
				MetaClass metaClass = getOrCreateMetaClass(interName);
				if (metaClass.getRealClass() == null) {
					implementInterfaceMetadata(classPoll.get(interName), metaClass);
					metaClass.setRealClass(realClass);
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
				CtMethod setter = inter.getDeclaredMethod("set" + caseName);
				field.setSetter(setter);
			}
		}
		metaClass.initStructures();
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
			CtField field = getOrCreate(classPoll.get(metaField.getDeclaration().getType().getName()), metaField.getDeclaration().getName(), newClass);
			newClass.addMethod(createGetter(field, metaField));
			newClass.addMethod(createSetter(field, metaField));
		}

		// Add self loader and relative load method;
		CtClass loader = classPoll.get(SelfLoader.class.getName());
		newClass.addInterface(loader);
		CtMethod loadMethod = loader.getDeclaredMethod("load");
		StringBuilder body = new StringBuilder("{");
		for (MetaField metaField : metaClass.getFields()) {
			MetaFieldDec dec = metaField.getDeclaration();
			body.append(dec.getName()).append("= (").append(dec.getType().getName()).append(")__$persistence.loadField(\"").append(dec.getName());
			body.append("\",").append(metaField.getId()).append(");\n");
		}
		body.append("}");
		CtMethod loadMethodImpl = CtNewMethod.make(loadMethod.getReturnType(), loadMethod.getName(), loadMethod.getParameterTypes(),
				loadMethod.getExceptionTypes(), body.toString(), newClass);
		newClass.addMethod(loadMethodImpl);

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
		String body = "{__$persistence.checkLoad(); return ($r)__$persistence.onFieldRead(\"" + field.getName() + "\"," + metaField.getId() + ","
				+ field.getName() + ");}";
		CtMethod method = metaField.getDeclaration().getGetter();
		return CtNewMethod.make(field.getType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body, field.getDeclaringClass());
	}

	private CtMethod createSetter(CtField field, MetaField metaField) throws CannotCompileException, NotFoundException {
		String body = "{__$persistence.checkLoad(); " + field.getName() + "= (" + field.getType().getName() + ")__$persistence.onFieldWrite(\""
				+ field.getName() + "\"," + metaField.getId() + "," + field.getName() + ",$1);}";
		CtMethod method = metaField.getDeclaration().getSetter();
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
	}

	public MetaClass getClassMetadata(Class<?> clazz) {
		return metadata.get(clazz.getName());
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
