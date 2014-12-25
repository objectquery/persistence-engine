package org.objectquery.persistence.engine;

import org.objectquery.persistence.engine.impl.MetaClass;

public interface ClassFactory {

	Class<?> getRealClass(Class<?> class1);

	public MetaClass getClassMetadata(Class<?> clazz);
}
