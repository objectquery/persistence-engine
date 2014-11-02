package org.objectquery.persistence.engine;

public interface PersistenceKeeper {

	<T> T onFieldRead(String fieldName, T prev);

	<T> T onFieldWrite(String fieldName, T prev, T newValue);

}
