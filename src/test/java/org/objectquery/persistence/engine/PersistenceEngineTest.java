package org.objectquery.persistence.engine;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.objectquery.persistence.engine.domain.Employee;
import org.objectquery.persistence.engine.domain.Organization;
import org.objectquery.persistence.engine.domain.Person;

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
	public void createMultipleEntityTest() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		Person instance = engine.newInstance(Person.class);
		assertNotNull(instance);
		Person instance1 = engine.newInstance(Person.class, 1);
		assertNotNull(instance1);

		Organization instance2 = engine.newInstance(Organization.class);
		assertNotNull(instance2);
		Organization instance3 = engine.newInstance(Organization.class, 1);
		assertNotNull(instance3);

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
	public void checkSelfLoaderInstance() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		Employee instance = engine.newInstance(Employee.class);
		assertTrue(instance instanceof PersistentObject);

		((PersistentObject) instance).load();
		assertTrue(((PersistentObject) instance).getKeeper() instanceof PersistenceKeeper);
	}

}
