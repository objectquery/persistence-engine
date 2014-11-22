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
			CtClass newClass = classPoll.getOrNull(interName + "$Impl");
			if (newClass == null) {
				CtClass inter = classPoll.get(interName);
				MetaClass newMetaClass = getOrCreateMetaClass(interName);
				newClass = classPoll.makeClass(interName + "$Impl");
				CtClass persKeeper = classPoll.get(PersistenceKeeper.class.getName());
				CtField persistenceField = new CtField(persKeeper, "__$persistence", newClass);
				newClass.addField(persistenceField);
				CtConstructor cons = CtNewConstructor.make(new CtClass[] { persKeeper }, null, "__$persistence = $1;", newClass);
				newClass.addConstructor(cons);
				implementInterface(inter, newClass);
				realClass = newClass.toClass();
				checkAndCreateMetadata(interName, realClass);
				newMetaClass.setRealClass(realClass);
			} else {
				realClass = classPoll.getClassLoader().loadClass(interName + "$Impl");
				checkAndCreateMetadata(interName, realClass);
			}
			return realClass;

		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	private void checkAndCreateMetadata(String name, Class<?> newClass) throws NotFoundException {
		MetaClass metaClass = getOrCreateMetaClass(name);
		if (metaClass.getRealClass() == null) {
			implementInterfaceMetadata(classPoll.get(name), metaClass);
			metaClass.setRealClass(newClass);
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
				if (method.getName().startsWith("get"))
					fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
				else
					fieldName = Character.toLowerCase(method.getName().charAt(2)) + method.getName().substring(3);
				if (!metaClass.hasField(fieldName))
					metaClass.addField(fieldName, getOrCreateMetaClass(method.getReturnType().getName()));
			}
		}
	}

	private void implementInterface(CtClass inter, CtClass newClass) throws CannotCompileException, NotFoundException {
		newClass.addInterface(inter);

		for (CtClass superInt : inter.getInterfaces()) {
			implementInterface(superInt, newClass);
		}
		for (CtMethod method : inter.getDeclaredMethods()) {
			if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
				try {
					newClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
					continue;
				} catch (NotFoundException nf) {

				}
				String fieldName;
				if (method.getName().startsWith("get"))
					fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
				else
					fieldName = Character.toLowerCase(method.getName().charAt(2)) + method.getName().substring(3);
				CtField field = getOrCreate(method.getReturnType(), fieldName, newClass);
				newClass.addMethod(createGetter(field, method));
			} else if (method.getName().startsWith("set")) {
				try {
					newClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
					continue;
				} catch (NotFoundException nf) {

				}

				String fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
				CtField field = getOrCreate(method.getParameterTypes()[0], fieldName, newClass);
				newClass.addMethod(createSetter(field, method));
			}
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
