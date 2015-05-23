package org.objectquery.persistence.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.LocalDate;

import org.junit.Test;
import org.objectquery.persistence.engine.domain.Employee;
import org.objectquery.persistence.engine.domain.FullTypeClass;
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

	@Test
	public void allTypesSetAndGet() {
		PersistenceEngineFactory factory = new PersistenceEngineFactory();
		PersistenceEngine engine = factory.createEngine();
		FullTypeClass instance = engine.newInstance(FullTypeClass.class);
		instance.setBirthDate(LocalDate.now());
		instance.setDetailedVote(113.4D);
		instance.setVote(113.4F);
		instance.setFlags((byte) 10);
		instance.setInitial('A');
		instance.setSoLong(20L);
		instance.setWeight(11);
		instance.setYesShort((short) 2);
		instance.setName("Abla");
		// instance.setReallyRawData(new byte[] { 1, 2, 3 });

		assertEquals(instance.getBirthDate(), LocalDate.now());
		assertEquals(instance.getDetailedVote(), 113.4D, 0D);
		assertEquals(instance.getVote(), 113.4F, 0F);
		assertEquals(instance.getFlags(), (byte) 10);
		assertEquals(instance.getInitial(), 'A');
		assertEquals(instance.getSoLong(), 20L);
		assertEquals(instance.getWeight(), 11);
		assertEquals(instance.getYesShort(), (short) 2);
		assertEquals(instance.getName(), "Abla");
		// assertEquals(instance.getReallyRawData(), new byte[] { 1, 2, 3 });
	}

}
