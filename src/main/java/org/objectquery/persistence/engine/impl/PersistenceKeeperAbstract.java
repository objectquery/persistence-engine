package org.objectquery.persistence.engine.impl;

import org.objectquery.persistence.engine.PersistenceKeeper;
import org.objectquery.persistence.engine.SelfLoader;

public abstract class PersistenceKeeperAbstract implements PersistenceKeeper {

	private Object instance;
	private boolean initied;

	@Override
	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public void checkLoad() {
		if (!initied) {
			((SelfLoader) instance).load();
			initied = true;
		}
	}

	@Override
	public Object onFieldRead(String fieldName, int fieldId, Object prev) {
		return prev;
	}

	protected void load() {
	}

	@Override
	public Object loadField(String fieldName, int filedId) {
		return loadField(fieldName);
	}

	protected abstract Object loadField(String field);

	protected abstract void storeField(String filed, Object prev, Object newValue);

	@Override
	public Object onFieldWrite(String fieldName, int fieldId, Object prev, Object newValue) {
		storeField(fieldName, prev, newValue);
		return newValue;
	}
}
