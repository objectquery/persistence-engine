package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.persistence.engine.domain.Book;
import org.objectquery.persistence.engine.domain.Employee;
import org.objectquery.persistence.engine.domain.Person;
import org.objectquery.persistence.engine.map.MapPersistenceEngine;
import org.objectquery.persistence.engine.map.MapTestDb;

public class PersistenceKeepTest {

	private PersistenceEngine engine;

	@Before
	public void before() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		engine = factory.createEngine();
	}

	@After
	public void after() {
		engine.close();
	}

	public MapTestDb getDb() {
		return ((MapPersistenceEngine) engine).getDb();
	}

	@Test
	public void createSetGetValue() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		Person instance = engine.newInstance(Person.class);
		assertNotNull(instance);
		instance.setName("name");
		assertEquals("name", instance.getName());
	}

	@Test
	public void createSetGetValueHierarchy() {
		Employee instance = engine.newInstance(Employee.class);
		assertNotNull(instance);
		instance.setName("name");
		assertEquals("name", instance.getName());
		instance.setPassId("id");
		assertEquals("id", instance.getPassId());
	}

	@Test
	public void testKeepPresistence() {
		Person p = engine.newInstance(Person.class, "10");
		p.setName("should save it");
		p = null;
		// assure no staff is in cache.
		System.gc();
		p = engine.get(Person.class, "10");
		assertEquals("should save it", p.getName());
	}

	@Test
	public void tesObjectRef() {
		Person p = engine.newInstance(Person.class, "10");
		p.setName("should save it");
		Book book = engine.newInstance(Book.class, "12");
		book.setTitle("life is life");
		book.setOwner(p);
		int phash = System.identityHashCode(p);
		int bhash = System.identityHashCode(book);
		p = null;
		book = null;
		System.gc();

		MapTestDb db = getDb();
		Map<String, Object> values = db.getById("12");

		assertEquals("life is life", values.get("title"));
		assertEquals("10", values.get("owner"));

		book = engine.get(Book.class, "12");
		assertEquals("life is life", book.getTitle());
		assertEquals("should save it", book.getOwner().getName());

		assertNotEquals(bhash, System.identityHashCode(book));
		assertNotEquals(phash, System.identityHashCode(book.getOwner()));
		assertSame(book.getOwner(), engine.get(Person.class, "10"));

	}

	@Test
	public void testCollectionOperation() {

		Book book = engine.newInstance(Book.class, "13");
		Book book1 = engine.newInstance(Book.class, "14");

		assertEquals(0, book.countRefs());
		book.addToRefs(book1);
		assertTrue(book.hasInRefs(book1));
		assertEquals(1, book.countRefs());
		assertTrue(book.removeFromRefs(book1));
		assertFalse(book.hasInRefs(book1));
		assertEquals(0, book.countRefs());

	}
}
