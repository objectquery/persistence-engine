package org.objectquery.persistence.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaClass {

	private String name;
	private Map<String, MetaField> fields = new HashMap<String, MetaField>();
	private Class<?> realClass;

	public MetaClass(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addField(String fieldName, MetaClass type) {
		fields.put(fieldName, new MetaField(this, fieldName, type));
	}

	public void setRealClass(Class<?> realClass) {
		this.realClass = realClass;
	}

	public Class<?> getRealClass() {
		return realClass;
	}

	public boolean hasField(String fieldName) {
		return fields.containsKey(fieldName);
	}

	public Collection<MetaField> getFields() {
		return fields.values();
	}

	public MetaField getField(String fieldName) {
		return fields.get(fieldName);
	}
}
