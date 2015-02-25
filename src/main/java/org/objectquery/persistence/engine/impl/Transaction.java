package org.objectquery.persistence.engine.impl;

import org.objectquery.persistence.engine.PersistenceKeeper;

public interface Transaction {

	void addChange(PersistenceKeeper persistenceKeeper, MetaField field, Object prev, Object newValue);

	Object getChangeValue(PersistenceKeeper persistenceKeeper, MetaField field);

	void commit();

	void rollback();

}
