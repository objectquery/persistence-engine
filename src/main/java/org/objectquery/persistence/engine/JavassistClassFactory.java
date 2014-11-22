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
		try {
			CtClass newClass = classPoll.getOrNull(interfa.getName() + "$Impl");
			if (newClass == null) {
				CtClass inter = classPoll.get(interfa.getName());
				MetaClass newMetaClass = getOrCreateMetaClass(interfa.getName());
				newClass = classPoll.makeClass(interfa.getName() + "$Impl");
				CtClass persKeeper = classPoll.get(PersistenceKeeper.class.getName());
				CtField persistenceField = new CtField(persKeeper, "__$persistence", newClass);
				newClass.addField(persistenceField);
				CtConstructor cons = CtNewConstructor.make(new CtClass[] { persKeeper }, null, "__$persistence = $1;", newClass);
				newClass.addConstructor(cons);

				implementInterface(inter, newClass, newMetaClass);
				Class<?> res = newClass.toClass();
				newMetaClass.setRealClass(res);
			} else {
				checkAndCreateMetadata(interfa, classPoll.getClassLoader().loadClass(interfa.getName() + "$Impl"));
			}
			return classPoll.getClassLoader().loadClass(interfa.getName() + "$Impl");
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	private void checkAndCreateMetadata(Class<?> interfa, Class<?> newClass) throws NotFoundException {
		MetaClass metaClass = metadata.get(interfa.getName());
		if (metaClass == null) {
			metaClass = new MetaClass(interfa.getName());
		}
		if (metaClass.getRealClass() == null) {
			this.metadata.put(interfa.getName(), metaClass);
			implementInterfaceMetadata(classPoll.get(interfa.getName()), newClass, metaClass);
			metaClass.setRealClass(newClass);
		}
	}

	private void implementInterfaceMetadata(CtClass inter, Class<?> newClass, MetaClass metaClass) throws NotFoundException {
		for (CtClass superInt : inter.getInterfaces())
			implementInterfaceMetadata(superInt, newClass, metaClass);
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

	private void implementInterface(CtClass inter, CtClass newClass, MetaClass newMetaClass) throws CannotCompileException, NotFoundException {
		newClass.addInterface(inter);

		for (CtClass superInt : inter.getInterfaces())
			implementInterface(superInt, newClass, newMetaClass);
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
				CtField field = getOrCreate(method.getReturnType(), fieldName, newClass, newMetaClass);
				newClass.addMethod(createGetter(field, method));
			} else if (method.getName().startsWith("set")) {
				try {
					newClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
					continue;
				} catch (NotFoundException nf) {

				}

				String fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
				CtField field = getOrCreate(method.getParameterTypes()[0], fieldName, newClass, newMetaClass);
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

	private CtField getOrCreate(CtClass filedType, String name, CtClass owner, MetaClass newMetaClass) throws CannotCompileException {
		CtField field = null;
		try {
			field = owner.getField(name);
		} catch (NotFoundException e) {

		}
		if (field == null) {
			newMetaClass.addField(name, getOrCreateMetaClass(filedType.getName()));
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
