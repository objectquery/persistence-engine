package org.objectquery.persistence.engine;


public abstract class PersistenceKeeperAbstract implements PersistenceKeeper {

	private Object instance;

	@Override
	public void setInstance(Object instance) {
		this.instance = instance;
	}

	@Override
	public Object onFieldRead(String fieldName, Object prev) {
		return prev;
	}

	protected void load() {
	}


	protected abstract Object loadField(String field);

	protected abstract void storeField(String filed, Object prev, Object newValue);

	@Override
	public Object onFieldWrite(String fieldName, Object prev, Object newValue) {
		return newValue;
	}
}
