package org.objectquery.persistence.engine.impl;

public class MetaField {
	private MetaFieldDec declaration;
	private int id;

	public MetaField(MetaFieldDec declaration, int id) {
		super();
		this.declaration = declaration;
		this.id = id;
	}

	public MetaFieldDec getDeclaration() {
		return declaration;
	}

	public void setDeclaration(MetaFieldDec declaration) {
		this.declaration = declaration;
	}

	public int getId() {
		return id;
	}

	public boolean isPrimitive() {
		return getDeclaration().isPrimitive();
	}

}
