package org.objectquery.persistence.engine;

import java.util.Collection;

import org.objectquery.persistence.engine.impl.MetaField;

public interface PersistenceKeeper {

	void setInstance(Object instance);

	Object onFieldRead(String fieldName, int fieldId, Object prev);

	Object onFieldWrite(String fieldName, int fieldId, Object prev, Object newValue);

	Object loadField(String fieldName, int fieldId);

	Object getId();

	void onAddTo(String fieldName, int fieldId, Collection<?> values, Object value);

	boolean onRemoveFrom(String fieldName, int fieldId, Collection<?> values, Object value);

	boolean onHasIn(String fieldName, int fieldId, Collection<?> values, Object value);

	int onCount(String fieldName, int fieldId, Collection<?> values);

	void storeChangedValue(MetaField field, Object oldValue, Object newValue);

	void checkLoad();
	
	void forceLoad();
}
