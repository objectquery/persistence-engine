package org.objectquery.persistence.engine.impl;

public class FieldTransactionChange implements TransactionChange {

	public MetaField field;
	public Object value;
	public Object oldValue;

	public FieldTransactionChange(MetaField field, Object value, Object oldValue) {
		super();
		this.field = field;
		this.value = value;
		this.oldValue = oldValue;
	}

	@Override
	public MetaField getField() {
		return field;
	}

	@Override
	public Object getOldValue() {
		return oldValue;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
