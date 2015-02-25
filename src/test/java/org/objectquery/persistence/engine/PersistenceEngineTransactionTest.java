package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.persistence.engine.domain.Employee;

public class PersistenceEngineTransactionTest {

	private PersistenceEngine engine;

	@Before
	public void before() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		engine = factory.createEngine();
	}

	@After
	public void aflter() {
		engine.close();
		engine = null;
	}

	@Test
	public void startDoEndTransaction() {
		engine.begin();
		Employee instance = engine.newInstance(Employee.class);
		instance.setName("name");
		assertEquals("name", instance.getName());
		engine.commit();
		assertEquals("name", instance.getName());
	}

	@Test
	public void transactionVisibility() {
		engine.begin();
		Employee instance = engine.newInstance(Employee.class);
		instance.setName("name");
		assertEquals("name", instance.getName());
		engine.suspend();
		assertNotEquals("name", instance.getName());
		engine.restore();
		assertEquals("name", instance.getName());
		engine.commit();
		assertEquals("name", instance.getName());
	}

	@Test
	public void transactionRollback() {
		engine.begin();
		Employee instance = engine.newInstance(Employee.class);
		instance.setName("name");
		assertEquals("name", instance.getName());
		engine.rollback();
		assertNotEquals("name", instance.getName());
	}

}
