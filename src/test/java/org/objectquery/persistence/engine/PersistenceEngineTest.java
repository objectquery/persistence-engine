package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class PersistenceEngineTest {

	@Test
	public void createEngineTest() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		assertNotNull(engine);
		engine.close();
		factory.close();
	}

	@Test
	public void createEntityTest() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		Person instance = engine.newInstance(Person.class);
		assertNotNull(instance);
		Person instance1 = engine.newInstance(Person.class, 1);
		assertNotNull(instance1);
		engine.close();
		factory.close();
	}

	@Test
	public void createEndGetTest() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		Person instance1 = engine.newInstance(Person.class, 1);
		assertNotNull(instance1);
		Person instance2 = engine.get(Person.class, 1);
		assertNotNull(instance2);
		assertSame(instance1, instance2);
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

}
