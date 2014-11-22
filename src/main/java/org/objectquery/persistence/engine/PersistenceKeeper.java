package org.objectquery.persistence.engine;

public interface PersistenceKeeper {

	void setInstance(Object instance);

	Object onFieldRead(String fieldName, Object prev);

	Object onFieldWrite(String fieldName, Object prev, Object newValue);

}
