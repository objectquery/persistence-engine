package org.objectquery.persistence.engine.impl;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.mockito.Mockito;
import org.objectquery.persistence.engine.PersistenceKeeper;

public class TransactionImplTest {

	@Test
	public void simpleAddGetChanges() {
		TransactionImpl impl = new TransactionImpl();
		PersistenceKeeper keeper = Mockito.mock(PersistenceKeeper.class);
		MetaField metafield = Mockito.mock(MetaField.class);
		Object oldValue = new Object();
		Object newValue = new Object();
		impl.addChange(keeper, metafield, oldValue, newValue);

		Object savedNewValue = impl.getChangeValue(keeper, metafield);
		assertSame(newValue, savedNewValue);
	}

	@Test
	public void addChangesAndCommit() {
		TransactionImpl impl = new TransactionImpl();
		PersistenceKeeper keeper = Mockito.mock(PersistenceKeeper.class);
		MetaField metafield = Mockito.mock(MetaField.class);
		Object oldValue = new Object();
		Object newValue = new Object();
		impl.addChange(keeper, metafield, oldValue, newValue);
		impl.commit();
		Mockito.verify(keeper, Mockito.atLeastOnce()).storeChangedValue(metafield, oldValue, newValue);
	}

}
