package org.objectquery.persistence.engine.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.objectquery.persistence.engine.impl.MetaClass;
import org.objectquery.persistence.engine.impl.MetaField;

public class MetaClassTest {

	@Test
	public void listFieldTest() {
		MetaClass meta = new MetaClass("test");
		meta.addField("test", new MetaClass("java.lang.String"));
		MetaClass superClass = new MetaClass("test");
		superClass.addField("test", new MetaClass("java.lang.Long"));
		meta.addSuper(superClass);
		meta.initStructures();

		Collection<MetaField> fields = meta.getFields();
		assertEquals(1, fields.size());
		assertEquals("test", fields.iterator().next().getDeclaration().getName());
		assertEquals("java.lang.String", fields.iterator().next().getDeclaration().getType().getName());

	}

	@Test
	public void listTwoFieldTest() {
		MetaClass meta = new MetaClass("test");
		meta.addField("testb", new MetaClass("java.lang.String"));
		MetaClass superClass = new MetaClass("test");
		superClass.addField("test", new MetaClass("java.lang.Long"));
		meta.addSuper(superClass);
		meta.initStructures();

		Collection<MetaField> fields = meta.getFields();
		assertEquals(2, fields.size());
		Iterator<MetaField> it = fields.iterator();
		MetaField field = it.next();
		assertEquals("testb", field.getDeclaration().getName());
		assertEquals("java.lang.String", field.getDeclaration().getType().getName());
		field = it.next();
		assertEquals("test", field.getDeclaration().getName());
		assertEquals("java.lang.Long", field.getDeclaration().getType().getName());

	}
}
