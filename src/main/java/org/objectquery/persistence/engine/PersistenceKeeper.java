package org.objectquery.persistence.engine;

public interface PersistenceKeeper {

	void setInstance(Object instance);

	Object onFieldRead(String fieldName, int fieldId, Object prev);

	Object onFieldWrite(String fieldName, int fieldId, Object prev, Object newValue);

	Object loadField(String fieldName, int filedId);

	void checkLoad();
}
