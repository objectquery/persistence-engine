package org.objectquery.persistence.engine;

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
				CtClass persKeeper = classPoll.get(PersistenceKeeper.class.getName());
				CtField persistenceField = new CtField(persKeeper, "__$persistence", newClass);
				newClass.addField(persistenceField);
				CtConstructor cons = CtNewConstructor.make(new CtClass[] { persKeeper }, null, "__$persistence = $1;", newClass);
				newClass.addConstructor(cons);
				implementInterface(inter, newMetaClass, newClass);
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
				MetaField field = metaClass.addField(fieldName, getOrCreateMetaClass(method.getReturnType().getName()));
				field.setGetter(method);
				CtMethod setter = inter.getDeclaredMethod("set" + caseName);
				field.setSetter(setter);
			}
		}
	}

	private void implementInterface(CtClass inter, MetaClass metaClass, CtClass newClass) throws CannotCompileException, NotFoundException {
		newClass.addInterface(inter);

		for (MetaField metaField : metaClass.getFieldHierarchy()) {
			CtField field = getOrCreate(classPoll.get(metaField.getType().getName()), metaField.getName(), newClass);
			newClass.addMethod(createGetter(field, metaField.getGetter()));
			newClass.addMethod(createSetter(field, metaField.getSetter()));
		}
	}

	private CtMethod createSetter(CtField field, CtMethod method) throws CannotCompileException, NotFoundException {
		String body = field.getName() + "= (" + field.getType().getName() + ")__$persistence.onFieldWrite(\"" + field.getName() + "\"," + field.getName()
				+ ",$1);";
		return CtNewMethod.make(method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body,
				field.getDeclaringClass());
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

	private CtMethod createGetter(CtField field, CtMethod method) throws CannotCompileException, NotFoundException {
		String body = " return ($r)__$persistence.onFieldRead(\"" + field.getName() + "\"," + field.getName() + ");";
		return CtNewMethod.make(field.getType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes(), body, field.getDeclaringClass());
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
