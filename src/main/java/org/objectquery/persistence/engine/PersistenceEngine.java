package org.objectquery.persistence.engine;

public interface PersistenceEngine {

	void close();

	<T> T newInstance(Class<T> class1);

	<T> T newInstance(Class<T> class1, Object id);

	<T> T get(Class<T> class1, Object id);

	void begin();

	void commit();

	void rollback();

	void suspend();

	void restore();

}
