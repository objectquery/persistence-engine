package org.objectquery.persistence.engine.domain;

public interface Book {

	String getTitle();

	void setTitle(String title);

	Person getOwner();

	void setOwner(Person owner);

}
