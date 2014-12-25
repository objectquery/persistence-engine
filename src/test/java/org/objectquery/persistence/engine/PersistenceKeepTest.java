package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.persistence.engine.domain.Person;

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
}
