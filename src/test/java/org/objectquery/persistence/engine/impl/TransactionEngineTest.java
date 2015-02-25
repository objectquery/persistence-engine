package org.objectquery.persistence.engine.impl;

import org.junit.Test;
import org.mockito.Mockito;

public class TransactionEngineTest {

	@Test
	public void testCreateCommit() {

		TransactionEngine engine = new TransactionEngine();
		PersistenceEngineInternal persEng = Mockito.mock(PersistenceEngineInternal.class);
		Transaction transaction = Mockito.mock(Transaction.class);
		Mockito.when(persEng.newTransaction()).thenReturn(transaction);
		engine.begin(persEng);
		engine.commit(persEng);
		Mockito.verify(transaction, Mockito.atLeastOnce()).commit();
	}

	@Test
	public void testCreateRollback() {

		TransactionEngine engine = new TransactionEngine();
		PersistenceEngineInternal persEng = Mockito.mock(PersistenceEngineInternal.class);
		Transaction transaction = Mockito.mock(Transaction.class);
		Mockito.when(persEng.newTransaction()).thenReturn(transaction);
		engine.begin(persEng);
		engine.rollback(persEng);
		Mockito.verify(transaction, Mockito.atLeastOnce()).rollback();
	}

}
