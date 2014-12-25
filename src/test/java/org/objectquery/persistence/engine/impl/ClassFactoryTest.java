package org.objectquery.persistence.engine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.objectquery.persistence.engine.ClassFactory;
import org.objectquery.persistence.engine.SelfLoader;
import org.objectquery.persistence.engine.domain.Company;
import org.objectquery.persistence.engine.domain.Employee;
import org.objectquery.persistence.engine.domain.EmployeeMetadata;
import org.objectquery.persistence.engine.domain.Organization;
import org.objectquery.persistence.engine.domain.Person;
import org.objectquery.persistence.engine.domain.SelfEmployed;
import org.objectquery.persistence.engine.impl.JavassistClassFactory;
import org.objectquery.persistence.engine.impl.MetaClass;
import org.objectquery.persistence.engine.impl.MetaFieldDec;

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
		Set<MetaClass> supers = meta.getSupers();
		assertEquals(1, supers.size());
		MetaClass superMata = supers.iterator().next();
		assertEquals(factory.getRealClass(Person.class), superMata.getRealClass());
		assertNotNull(superMata.getDeclaredField("name"));
		assertEquals("name", superMata.getDeclaredField("name").getName());
		assertEquals("java.lang.String", superMata.getDeclaredField("name").getType().getName());
		Collection<MetaFieldDec> fields = meta.getDeclaredFields();
		assertEquals(1, fields.size());
		assertNotNull(meta.getDeclaredField("passId"));
		assertEquals("passId", meta.getDeclaredField("passId").getName());
		assertEquals("java.lang.String", meta.getDeclaredField("passId").getType().getName());
	}

	@Test
	public void createMetaClassFistCreationTest() throws InstantiationException, IllegalAccessException {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(EmployeeMetadata.class);
		MetaClass meta = factory.getClassMetadata(EmployeeMetadata.class);
		assertEquals("org.objectquery.persistence.engine.domain.EmployeeMetadata", meta.getName());
		assertEquals(clazz, meta.getRealClass());
		Set<MetaClass> supers = meta.getSupers();
		assertEquals(1, supers.size());
		MetaClass superMata = supers.iterator().next();
		assertEquals(factory.getRealClass(Person.class), superMata.getRealClass());
		assertNotNull(superMata.getDeclaredField("name"));
		assertEquals("name", superMata.getDeclaredField("name").getName());
		assertEquals("java.lang.String", superMata.getDeclaredField("name").getType().getName());
		Collection<MetaFieldDec> fields = meta.getDeclaredFields();
		assertEquals(1, fields.size());
		assertNotNull(meta.getDeclaredField("passId"));
		assertEquals("passId", meta.getDeclaredField("passId").getName());
		assertEquals("java.lang.String", meta.getDeclaredField("passId").getType().getName());
	}

	@Test
	public void createMetaClassDoubleParentsTest() throws InstantiationException, IllegalAccessException {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(SelfEmployed.class);
		MetaClass meta = factory.getClassMetadata(SelfEmployed.class);
		assertEquals("org.objectquery.persistence.engine.domain.SelfEmployed", meta.getName());
		assertEquals(clazz, meta.getRealClass());
		Set<MetaClass> supers = meta.getSupers();
		assertEquals(2, supers.size());
		for (MetaClass metaClass : supers) {
			if (metaClass.getName().equals(Company.class.getName())) {
				assertEquals(factory.getRealClass(Company.class), metaClass.getRealClass());
				assertNotNull(metaClass.getDeclaredField("legalName"));
				assertEquals("legalName", metaClass.getDeclaredField("legalName").getName());
				assertEquals("java.lang.String", metaClass.getDeclaredField("legalName").getType().getName());
			} else {
				assertEquals(factory.getRealClass(Person.class), metaClass.getRealClass());
				assertNotNull(metaClass.getDeclaredField("name"));
				assertEquals("name", metaClass.getDeclaredField("name").getName());
				assertEquals("java.lang.String", metaClass.getDeclaredField("name").getType().getName());
			}
		}

	}

	@Test
	public void testDeclaredClassIsSelfLoader() {
		ClassFactory factory = new JavassistClassFactory();
		Class<?> clazz = factory.getRealClass(Person.class);
		assertTrue(SelfLoader.class.isAssignableFrom(clazz));
	}

}
