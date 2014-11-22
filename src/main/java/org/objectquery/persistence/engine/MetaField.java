package org.objectquery.persistence.engine;

public class MetaField {
	private MetaClass owner;
	private String name;
	private MetaClass type;

	public MetaField(MetaClass owner, String fieldName, MetaClass type) {
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
}
