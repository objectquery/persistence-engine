package org.objectquery.persistence.engine.impl;

import java.util.Collection;
import java.util.HashSet;

import org.objectquery.persistence.engine.PersistenceEngine;
import org.objectquery.persistence.engine.PersistenceKeeper;
import org.objectquery.persistence.engine.PersistentObject;

public abstract class PersistenceKeeperAbstract implements PersistenceKeeper {

	private Object id;
	private MetaClass metaClass;
	private PersistentObject instance;
	private PersistenceEngine engine;
	private boolean initied;

	public PersistenceKeeperAbstract(PersistenceEngine engine, MetaClass metaClass, Object id) {
		this.engine = engine;
		this.metaClass = metaClass;
		this.id = id;
	}

	@Override
	public void setInstance(Object instance) {
		this.instance = (PersistentObject) instance;
	}

	@Override
	public Object getId() {
		return id;
	}

	public void checkLoad() {
		if (!initied) {
			instance.load();
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
	public Object loadField(String fieldName, int fieldId) {
		Object fieldvalue = loadField(fieldName);
		MetaField field = metaClass.getFieldById(fieldId);
		if (fieldvalue == null) {
			if (field.isCollection())
				return new HashSet<Object>();
			else
				return null;
		}
		if (field.isPrimitive())
			return fieldvalue;
		else
			return engine.get(field.getDeclaration().getType().getType(), fieldvalue);
	}

	protected abstract Object loadField(String field);

	protected abstract void storeField(String filed, Object prev, Object newValue);

	@Override
	public Object onFieldWrite(String fieldName, int fieldId, Object prev, Object newValue) {
		MetaField field = metaClass.getFieldById(fieldId);
		if (field.isPrimitive())
			storeField(fieldName, prev, newValue);
		else {
			final Object prevId;
			if (prev != null)
				prevId = ((PersistentObject) prev).getKeeper().getId();
			else
				prevId = null;
			final Object newValueId;
			if (newValue != null)
				newValueId = ((PersistentObject) newValue).getKeeper().getId();
			else
				newValueId = null;

			storeField(fieldName, prevId, newValueId);
		}

		return newValue;
	}

	@Override
	public void onAddTo(String fieldName, int fieldId, Collection<?> values, Object value) {
		((Collection<Object>) values).add(value);
	}

	@Override
	public int onCount(String fieldName, int fieldId, Collection<?> values) {
		return values.size();
	}

	@Override
	public boolean onHasIn(String fieldName, int fieldId, Collection<?> values, Object value) {
		return values.contains(value);
	}

	@Override
	public boolean onRemoveFrom(String fieldName, int fieldId, Collection<?> values, Object value) {
		return values.remove(value);
	}
}
