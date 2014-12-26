package org.objectquery.persistence.engine.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MetaClass {

	private String name;
	private boolean primitive = false;
	private Map<String, MetaFieldDec> declaredFields = new HashMap<String, MetaFieldDec>();
	private Map<String, MetaField> realFields = new HashMap<String, MetaField>();
	private MetaField[] fieldIndex;
	private Set<MetaClass> supers = new HashSet<MetaClass>();
	private Class<?> realClass;
	private Class<?> type;

	public MetaClass(String name) {
		this.name = name;
	}

	public MetaClass(String name, boolean primitive) {
		this.name = name;
		this.primitive = primitive;
	}

	public String getName() {
		return name;
	}

	public MetaFieldDec addField(String fieldName, MetaClass type) {
		MetaFieldDec field = new MetaFieldDec(this, fieldName, type);
		declaredFields.put(fieldName, field);
		return field;
	}

	public void setRealClass(Class<?> realClass) {
		this.realClass = realClass;
	}

	public Class<?> getRealClass() {
		return realClass;
	}

	public boolean hasDeclaredField(String fieldName) {
		return declaredFields.containsKey(fieldName);
	}

	public Collection<MetaFieldDec> getDeclaredFields() {
		return declaredFields.values();
	}

	public MetaFieldDec getDeclaredField(String fieldName) {
		return declaredFields.get(fieldName);
	}

	public Set<MetaClass> getSupers() {
		return supers;
	}

	public void addSuper(MetaClass meta) {
		this.supers.add(meta);
	}

	public Collection<MetaField> getFields() {
		return realFields.values();
	}

	public MetaField getField(String name) {
		return realFields.get(name);
	}

	public MetaField getFieldById(int id) {
		return fieldIndex[id];
	}

	private void getPlainHierarchy(Set<MetaClass> supers, Set<MetaClass> classes) {
		for (MetaClass metaClass : supers) {
			getPlainHierarchy(metaClass.supers, classes);
		}
		classes.addAll(supers);
	}

	public boolean isPrimitive() {
		return primitive;
	}

	public void initStructures() {
		Set<MetaClass> classes = new LinkedHashSet<MetaClass>();
		getPlainHierarchy(supers, classes);
		classes.add(this);

		for (MetaClass metaClass : classes) {
			int i = 0;
			for (MetaFieldDec field : metaClass.declaredFields.values()) {
				MetaField cur = realFields.get(field.getName());
				if (cur != null) {
					cur.setDeclaration(field);
				} else {
					cur = new MetaField(field, i++);
					realFields.put(field.getName(), cur);
				}
			}
		}
		fieldIndex = new MetaField[realFields.size()];
		for (MetaField field : realFields.values()) {
			fieldIndex[field.getId()] = field;
		}

	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}
}
