/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the invalidation rule on a table.
 */
public final class TableInvalidation
{

    /**
     * Table name.
     */
    private final String tableName;

    /**
     * Entity class (may be null).
     */
    private final Class<?> entityClass;

    /**
     * Collection roles to invalidate.
     */
    private final HashSet<String> collectionRoles;

    /**
     * Caches to invalidate.
     */
    private final HashSet<String> cacheNames;

    /**
     * Construct the invalidation rules.
     */
    public TableInvalidation( String tableName, Class<?> entityClass )
    {
        this.tableName = tableName.toLowerCase();
        this.entityClass = entityClass;
        this.collectionRoles = new HashSet<String>();
        this.cacheNames = new HashSet<String>();
    }

    /**
     * Return the table name.
     */
    public String getTableName()
    {
        return this.tableName;
    }

    /**
     * Return the entity class (may be null).
     */
    public Class<?> getEntityClass()
    {
        return this.entityClass;
    }

    /**
     * Return all collection roles to invalidate.
     */
    public Collection<String> getCollectionRoles()
    {
        return this.collectionRoles;
    }

    /**
     * Add a collection role.
     */
    public void addCollectionRole( String roleName )
    {
        this.collectionRoles.add( roleName );
    }

    /**
     * Return all caches to invalidate.
     */
    public Collection<String> getCacheNames()
    {
        return this.cacheNames;
    }

    /**
     * Add a cache name.
     */
    public void addCacheName( String cacheName )
    {
        this.cacheNames.add( cacheName );
    }

    /**
     * Return a string description.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append( "TableInvalidation[" );
        str.append( "tableName = " ).append( this.tableName ).append( ", " );
        str.append( "entityClass = " ).append( this.entityClass != null ? this.entityClass.getName() : "null" ).append( ", " );
        str.append( "collectionRoles = " ).append( this.collectionRoles.toString() ).append( ", " );
        str.append( "cacheNames = " ).append( this.cacheNames.toString() ).append( "]" );
        return str.toString();
    }
}
