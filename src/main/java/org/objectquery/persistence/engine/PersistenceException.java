package org.objectquery.persistence.engine;


public class PersistenceException extends RuntimeException {

	private static final long serialVersionUID = 3393492715724931885L;

	public PersistenceException(String string) {
		super(string);
	}

	public PersistenceException(Exception e) {
		super(e);
	}

}
