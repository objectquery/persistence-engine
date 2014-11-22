package org.objectquery.persistence.engine;

public interface ClassFactory {

	Class<?> getRealClass(Class<?> class1);

	public MetaClass getClassMetadata(Class<?> clazz);
}
