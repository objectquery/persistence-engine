package org.objectquery.persistence.engine.domain;

public interface Book {

	String getTitle();

	void setTitle(String title);

	Person getOwner();

	void setOwner(Person owner);

	Iterable<Book> getBooks();

	void addToBooks(Book book);

	boolean removeFromBooks(Book book);

	boolean hasInBooks(Book book);

	int countBooks();

}
