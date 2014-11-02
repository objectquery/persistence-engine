package org.objectquery.persistence.engine;

public interface PersistenceKeeper {

	Object onFieldRead(String fieldName, Object prev);

	Object onFieldWrite(String fieldName, Object prev, Object newValue);

}
