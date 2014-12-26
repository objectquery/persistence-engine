persistence-engine
==================

The Persistence engine is an attempt to define a new way to persist java object.

## Concepts

- Only one instance per entity per VM.
- Linear entity navigation and query.

## Coding

Define entities

``` java
public interface Library {

  void setAddress(String address);

  String getAddress();

  Iterable<Book> getBooks();
  
  void addToBooks(Book book);
  
  boolean removeFromBooks(Book book);
  
  int countBooks();
  
  boolean hasInBooks(Book book);
  
  default Iterable<Book> getBooksByAuthor(String author) {
    return filter(getBooks()).has(book -> book.getAuthor(),author).iterable();
  }
}
```

``` java
public interface Book {

  void setTitle(String title);

  String getTitle();

  void setAuthor(String author);
  
  String getAuthor();

}
```
