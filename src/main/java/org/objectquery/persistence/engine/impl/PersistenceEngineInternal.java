package org.objectquery.persistence.engine.impl;

import org.objectquery.persistence.engine.PersistenceEngine;

public interface PersistenceEngineInternal extends PersistenceEngine {

	Transaction newTransaction();

	Transaction current();

}
