package org.objectquery.persistence.engine;

import static org.junit.Assert.*;

import java.lang.reflect.Modifier;
import java.util.Collection;

import org.junit.Test;
import org.objectquery.persistence.engine.domain.Company;
import org.objectquery.persistence.engine.domain.Employee;
import org.objectquery.persistence.engine.domain.EmployeeMetadata;
import org.objectquery.persistence.engine.domain.Organization;
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

	@Test
	public void createHierarchyClassDobuleDeclaredFieldTest() throws InstantiationException, IllegalAccessException {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(Company.class);
		assertTrue(Organization.class.isAssignableFrom(clazz));
		assertTrue(Company.class.isAssignableFrom(clazz));
		assertFalse(clazz.isInterface());
		assertFalse((clazz.getModifiers() & Modifier.ABSTRACT) != 0);
	}

	@Test
	public void createMetaClassTest() throws InstantiationException, IllegalAccessException {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(Employee.class);
		MetaClass meta = factory.getClassMetadata(Employee.class);
		assertEquals("org.objectquery.persistence.engine.domain.Employee", meta.getName());
		assertEquals(clazz, meta.getRealClass());
	}

	@Test
	public void createMetaClassFistCreationTest() throws InstantiationException, IllegalAccessException {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(EmployeeMetadata.class);
		MetaClass meta = factory.getClassMetadata(EmployeeMetadata.class);
		assertEquals("org.objectquery.persistence.engine.domain.EmployeeMetadata", meta.getName());
		assertEquals(clazz, meta.getRealClass());
		Collection<MetaField> fields = meta.getFields();
		assertEquals(2, fields.size());
		assertNotNull(meta.getField("name"));
		assertEquals("name", meta.getField("name").getName());
		assertEquals("java.lang.String", meta.getField("name").getType().getName());
		assertNotNull(meta.getField("passId"));
		assertEquals("passId", meta.getField("passId").getName());
		assertEquals("java.lang.String", meta.getField("passId").getType().getName());
	}

}
