package org.objectquery.persistence.engine;

import static org.junit.Assert.*;

import java.lang.reflect.Modifier;

import org.junit.Test;
import org.objectquery.persistence.engine.domain.Employee;
import org.objectquery.persistence.engine.domain.Person;

public class ClassFactoryTest {

	@Test
	public void createClassTest() {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(Person.class);
		assertTrue(Person.class.isAssignableFrom(clazz));
		assertFalse(clazz.isInterface());
		assertFalse((clazz.getModifiers() & Modifier.ABSTRACT) != 0);
	}

	@Test
	public void createHierarchyClassTest() throws InstantiationException, IllegalAccessException {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(Employee.class);
		assertTrue(Person.class.isAssignableFrom(clazz));
		assertTrue(Employee.class.isAssignableFrom(clazz));
		assertFalse(clazz.isInterface());
		assertFalse((clazz.getModifiers() & Modifier.ABSTRACT) != 0);
	}

}
