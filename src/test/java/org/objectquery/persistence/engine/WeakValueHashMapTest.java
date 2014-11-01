package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class WeakValueHashMapTest {

	@Test
	public void testAddGet() {
		WeakValueHashMap values = new WeakValueHashMap();
		Object o = new Object();
		Object ko = new Object();
		values.put(ko, o);
		Object o1 = values.get(ko);
		assertNotNull(o1);
		assertEquals(o, o1);
	}

	@Test
	public void testAddGetReplace() {
		WeakValueHashMap values = new WeakValueHashMap();
		Object o = new Object();
		Object ko = new Object();
		values.put(ko, o);
		Object o1 = values.get(ko);
		assertNotNull(o1);
		assertEquals(o, o1);
		Object o2 = new Object();
		values.put(ko, o2);
		Object o3 = values.get(ko);
		assertNotNull(o3);
		assertEquals(o2, o3);

	}

	@Test
	public void testAddGetAutoRemove() {
		WeakValueHashMap values = new WeakValueHashMap();
		Object o = new Object();
		Object ko = new Object();
		values.put(ko, o);
		Object o1 = values.get(ko);
		assertNotNull(o1);
		assertEquals(o, o1);
		o = null;
		o1 = null;
		System.gc();
		o1 = values.get(ko);
		assertNull(o1);
	}
	
	@Test
	public void testAddGetKeepReferred() {
		WeakValueHashMap values = new WeakValueHashMap();
		Object o = new Object();
		Object ko = new Object();
		values.put(ko, o);
		Object o1 = values.get(ko);
		assertNotNull(o1);
		assertEquals(o, o1);
		System.gc();
		o1 = values.get(ko);
		assertNotNull(o1);
	}
}
