package org.objectquery.persistence.engine;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;

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
						CtField field = new CtField(method.getReturnType(), fieldName, newClass);
						newClass.addField(field);
						newClass.addMethod(CtNewMethod.getter(method.getName(), field));
						newClass.addMethod(CtNewMethod.setter("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), field));
					}
					// newClass.
				}
				newClass.toClass();
			}
			return classPoll.getClassLoader().loadClass(class1.getName() + "$Impl");
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}
}
