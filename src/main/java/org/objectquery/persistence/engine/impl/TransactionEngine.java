package org.objectquery.persistence.engine.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.objectquery.persistence.engine.PersistenceEngine;

public class TransactionEngine {

	private static final ThreadLocal<Map<PersistenceEngine, Stack<Transaction>>> transactions = ThreadLocal
			.withInitial(() -> new HashMap<PersistenceEngine, Stack<Transaction>>());

	private static final ThreadLocal<Map<PersistenceEngine, Stack<Transaction>>> suspended = ThreadLocal
			.withInitial(() -> new HashMap<PersistenceEngine, Stack<Transaction>>());

	public void begin(PersistenceEngineInternal persistenceEngine) {
		getEngineTransactions(persistenceEngine).push(persistenceEngine.newTransaction());
	}

	private Stack<Transaction> getEngineTransactions(PersistenceEngineInternal persistenceEngine) {
		Map<PersistenceEngine, Stack<Transaction>> allTransactions = transactions.get();
		Stack<Transaction> engineTransaction = allTransactions.get(persistenceEngine);
		if (engineTransaction == null) {
			engineTransaction = new Stack<Transaction>();
			allTransactions.put(persistenceEngine, engineTransaction);
		}
		return engineTransaction;
	}

	public void commit(PersistenceEngineInternal persistenceEngine) {
		Transaction transaction = getEngineTransactions(persistenceEngine).pop();
		transaction.commit();
	}

	public void restore(PersistenceEngineInternal persistenceEngine) {
		Transaction transaction = getEngineSuspended(persistenceEngine).pop();
		getEngineTransactions(persistenceEngine).push(transaction);
	}

	private Stack<Transaction> getEngineSuspended(PersistenceEngineInternal persistenceEngine) {
		Map<PersistenceEngine, Stack<Transaction>> allSuspended = suspended.get();
		Stack<Transaction> engineSuspended = allSuspended.get(persistenceEngine);
		if (engineSuspended == null) {
			engineSuspended = new Stack<Transaction>();
			allSuspended.put(persistenceEngine, engineSuspended);
		}
		return engineSuspended;
	}

	public void suspend(PersistenceEngineInternal persistenceEngine) {
		Transaction transaction = getEngineTransactions(persistenceEngine).pop();
		getEngineSuspended(persistenceEngine).push(transaction);
	}

	public void rollback(PersistenceEngineInternal persistenceEngine) {
		Transaction transaction = getEngineTransactions(persistenceEngine).pop();
		transaction.rollback();
	}

	public Transaction current(PersistenceEngineInternal persistenceEngine) {
		Stack<Transaction> engineTransactions = getEngineTransactions(persistenceEngine);
		if (engineTransactions == null || engineTransactions.isEmpty())
			return null;
		return engineTransactions.peek();
	}
}
