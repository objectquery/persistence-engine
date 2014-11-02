package org.objectquery.persistence.engine;

public class PersistenceKeeperImpl implements PersistenceKeeper {

	public PersistenceKeeperImpl(Object id) {
	}

	@Override
	public Object onFieldRead(String fieldName, Object prev) {
		return prev;
	}

	@Override
	public Object onFieldWrite(String fieldName, Object prev, Object newValue) {
		return newValue;
	}

}
