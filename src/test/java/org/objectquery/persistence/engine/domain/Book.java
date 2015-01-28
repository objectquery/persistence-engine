package org.objectquery.persistence.engine.domain;

public interface Book {

	String getTitle();

	void setTitle(String title);

	Person getOwner();

	void setOwner(Person owner);

	Iterable<Book> getRefs();

	void addToRefs(Book book);

	boolean removeFromRefs(Book book);

	boolean hasInRefs(Book book);

	int countRefs();

}
