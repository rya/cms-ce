/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;


public interface EntityDao<T>
{
    /**
     * Store transient entity.
     */
    void storeNew( T entity );

    /**
     * Store transient entities.
     */
    void storeNew( List<T> entities );

    /**
     * Update existing entity.
     *
     * @param entity an unattached entity.
     */
    void updateExisting( T entity );

    /**
     * Update existing entities.
     *
     * @param entities unattached entities.
     */
    void updateExisting( List<T> entities );

    /**
     * Use only this method when you do not know if you are storing a new or updating an existing entity.
     */
    void store( T entity );

    void storeAll( List<T> entities );

    void delete( T entity );

    void refresh( T entity );

    void evict( T entity );

    /**
     * Calculates number of instances in database.
     *
     * @return number of instances
     */
    Long count( Class<T> clazz );

    HibernateTemplate getHibernateTemplate();
}
