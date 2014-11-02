package org.objectquery.persistence.engine;

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

	public JavassistClassFactory() {
		classPoll = ClassPool.getDefault();
	}

	public Class<?> getRealClass(Class<?> class1) {
		try {
			CtClass inter = classPoll.get(class1.getName());
			if (!inter.isInterface())
				throw new PersistenceException(class1 + " is not an interfaces");

			CtClass newClass = classPoll.getOrNull(class1.getName() + "$Impl");
			if (newClass == null) {
				newClass = classPoll.makeClass(class1.getName() + "$Impl");
				CtClass persKeeper = classPoll.get(PersistenceKeeper.class.getName());
				CtField persistenceField = new CtField(persKeeper, "__$persistence", newClass);
				newClass.addField(persistenceField);
				CtConstructor cons = CtNewConstructor.make(new CtClass[] { persKeeper }, null, "__$persistence = $1;", newClass);
				newClass.addConstructor(cons);
				newClass.addInterface(inter);

				for (CtMethod method : inter.getDeclaredMethods()) {
					if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
						String fieldName;
						if (method.getName().startsWith("get"))
							fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
						else
							fieldName = Character.toLowerCase(method.getName().charAt(2)) + method.getName().substring(3);
						CtField field = getOrCreate(method.getReturnType(), fieldName, newClass);
						newClass.addMethod(createGetter(field, method));
					} else if (method.getName().startsWith("set")) {
						String fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
						CtField field = getOrCreate(method.getParameterTypes()[0], fieldName, newClass);
						newClass.addMethod(createSetter(field, method));
					}
				}
				newClass.toClass();
			}
			return classPoll.getClassLoader().loadClass(class1.getName() + "$Impl");
		} catch (Exception e) {
			throw new PersistenceException(e);
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
}
