/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.cache;

public interface Cache
{
    /**
     * Add an object to the cache with the specified key.
     *
     * @param key
     * @param object
     */
    void addObject( Object key, Object object );

    /**
     * Remove all objects from the cache.
     */
    void clear();

    /**
     * Retrieve an object from the cache.
     *
     * @param key
     * @return
     */
    Object getObject( Object key );

    /**
     * Remove one object from the cache.
     *
     * @param key
     * @return The removed object, or NULL if the object was not found.
     */
    Object remove( Object key );

    /**
     * Number of objects in the cache.
     *
     * @return
     */
    int size();

    /**
     * Removes all object with a value
     *
     * @param value
     */
    void removeValues( Object value );
}
