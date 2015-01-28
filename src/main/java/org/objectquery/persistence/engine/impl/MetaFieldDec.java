package org.objectquery.persistence.engine.impl;

import javassist.CtMethod;

public class MetaFieldDec {
	private MetaClass owner;
	private String name;
	private MetaClass type;
	private CtMethod getter;
	private CtMethod setter;
	private CtMethod addTo;
	private CtMethod removeFrom;
	private CtMethod count;
	private CtMethod hasIn;
	private boolean collection;

	public MetaFieldDec(MetaClass owner, String fieldName, MetaClass type) {
		this.owner = owner;
		this.name = fieldName;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public MetaClass getType() {
		return type;
	}

	public MetaClass getOwner() {
		return owner;
	}

	public void setGetter(CtMethod getter) {
		this.getter = getter;
	}

	public CtMethod getGetter() {
		return getter;
	}

	public void setSetter(CtMethod setter) {
		this.setter = setter;
	}

	public CtMethod getSetter() {
		return setter;
	}

	public boolean isPrimitive() {
		return type.isPrimitive();
	}

	public CtMethod getAddTo() {
		return addTo;
	}

	public void setAddTo(CtMethod addTo) {
		this.addTo = addTo;
	}

	public CtMethod getRemoveFrom() {
		return removeFrom;
	}

	public void setRemoveFrom(CtMethod removeFrom) {
		this.removeFrom = removeFrom;
	}

	public CtMethod getCount() {
		return count;
	}

	public void setCount(CtMethod count) {
		this.count = count;
	}

	public CtMethod getHasIn() {
		return hasIn;
	}

	public void setHasIn(CtMethod hasIn) {
		this.hasIn = hasIn;
	}

	public void setCollection(boolean collection) {
		this.collection = collection;
	}

	public boolean isCollection() {
		return collection;
	}

}
