package org.objectquery.persistence.engine.impl;

import javassist.CtMethod;

public class MetaFieldDec {
	private MetaClass owner;
	private String name;
	private MetaClass type;
	private CtMethod getter;
	private CtMethod setter;

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

}
