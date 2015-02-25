package org.objectquery.persistence.engine.impl;

public interface TransactionChange {

	MetaField getField();

	Object getValue();

	Object getOldValue();

}
