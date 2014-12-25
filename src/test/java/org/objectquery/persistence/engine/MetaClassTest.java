package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

public class MetaClassTest {

	@Test
	public void listFieldTest() {
		MetaClass meta = new MetaClass("test");
		meta.addField("test", new MetaClass("java.lang.String"));
		MetaClass superClass = new MetaClass("test");
		superClass.addField("test", new MetaClass("java.lang.Long"));
		meta.addSuper(superClass);

		Collection<MetaField> fields = meta.getFieldHierarchy();
		assertEquals(1, fields.size());
		assertEquals("test", fields.iterator().next().getName());
		assertEquals("java.lang.String", fields.iterator().next().getType().getName());

	}

	@Test
	public void listTwoFieldTest() {
		MetaClass meta = new MetaClass("test");
		meta.addField("testb", new MetaClass("java.lang.String"));
		MetaClass superClass = new MetaClass("test");
		superClass.addField("test", new MetaClass("java.lang.Long"));
		meta.addSuper(superClass);

		Collection<MetaField> fields = meta.getFieldHierarchy();
		assertEquals(2, fields.size());
		Iterator<MetaField> it = fields.iterator();
		MetaField field = it.next();
		assertEquals("testb", field.getName());
		assertEquals("java.lang.String", field.getType().getName());
		field = it.next();
		assertEquals("test", field.getName());
		assertEquals("java.lang.Long", field.getType().getName());

	}
}
