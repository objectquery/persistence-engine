package org.objectquery.persistence.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.objectquery.persistence.engine.PersistenceKeeper;

public class TransactionImpl implements Transaction {

	private Map<PersistenceKeeper, Map<MetaField, TransactionChange>> changes = new HashMap<>();

	@Override
	public void addChange(PersistenceKeeper persistenceKeeper, MetaField field, Object prev, Object newValue) {
		Map<MetaField, TransactionChange> objectChanges = changes.get(persistenceKeeper);
		if (objectChanges == null) {
			objectChanges = new HashMap<>();
			changes.put(persistenceKeeper, objectChanges);
		}
		objectChanges.put(field, new FieldTransactionChange(field, newValue, prev));
	}

	@Override
	public Object getChangeValue(PersistenceKeeper persistenceKeeper, MetaField field) {
		Map<MetaField, TransactionChange> objectChanges = changes.get(persistenceKeeper);
		if (objectChanges == null)
			return null;
		TransactionChange change = objectChanges.get(field);
		if (change == null)
			return null;

		return change.getValue();
	}

	@Override
	public void commit() {
		startNativeTransaction();
		changes.forEach((keeper, objectChanges) -> {
			objectChanges.forEach((field, change) -> keeper.storeChangedValue(field, change.getOldValue(), change.getValue()));
			keeper.forceLoad();
		});
		commitNativeTransaction();
	}

	@Override
	public void rollback() {
		changes.clear();
		rollbackNativeTransaction();
	}

	protected void startNativeTransaction() {

	}

	protected void commitNativeTransaction() {

	}

	protected void rollbackNativeTransaction() {

	}
}
