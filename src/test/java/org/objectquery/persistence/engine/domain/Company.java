package org.objectquery.persistence.engine.domain;

public interface Company extends Organization {

	@Override
	public String getName();

	@Override
	public void setName(String name);

}
