package org.objectquery.persistence.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MetaClass {

	private String name;
	private Map<String, MetaField> fields = new HashMap<String, MetaField>();
	private Set<MetaClass> supers = new HashSet<MetaClass>();
	private Class<?> realClass;

	public MetaClass(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public MetaField addField(String fieldName, MetaClass type) {
		MetaField field = new MetaField(this, fieldName, type);
		fields.put(fieldName, field);
		return field;
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

	public Set<MetaClass> getSupers() {
		return supers;
	}

	public void addSuper(MetaClass meta) {
		this.supers.add(meta);
	}

	public Collection<MetaField> getFieldHierarchy() {
		Set<MetaClass> classes = new LinkedHashSet<MetaClass>();
		getPlainHierarchy(supers, classes);
		classes.add(this);
		Map<String, MetaField> fields = new HashMap<String, MetaField>();
		for (MetaClass metaClass : classes) {
			for (MetaField field : metaClass.fields.values()) {
				fields.put(field.getName(), field);
			}
		}
		return fields.values();
	}

	private void getPlainHierarchy(Set<MetaClass> supers, Set<MetaClass> classes) {
		for (MetaClass metaClass : supers) {
			getPlainHierarchy(metaClass.supers, classes);
		}
		classes.addAll(supers);
	}

}
