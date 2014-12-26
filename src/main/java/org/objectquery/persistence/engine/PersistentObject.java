package org.objectquery.persistence.engine;

public interface PersistentObject {

	public void load();

	public PersistenceKeeper getKeeper();
}
