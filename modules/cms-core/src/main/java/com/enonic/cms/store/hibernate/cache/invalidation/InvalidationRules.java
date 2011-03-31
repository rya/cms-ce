/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class implements the invalidation rules.
 */
public final class InvalidationRules
{
    /**
     * Invalidation rules by table.
     */
    private final HashMap<String, TableInvalidation> mapByTable;

    /**
     * Invalidation rules by entity class.
     */
    private final HashMap<String, TableInvalidation> mapByEntity;

    /**
     * Query cache regions.
     */
    private final HashSet<String> queryCacheRegions;

    /**
     * Construct the invalidation rules.
     */
    public InvalidationRules()
    {
        this.mapByTable = new HashMap<String, TableInvalidation>();
        this.mapByEntity = new HashMap<String, TableInvalidation>();
        this.queryCacheRegions = new HashSet<String>();
    }

    /**
     * Add invalidation rules.
     */
    public void addTableRule( TableInvalidation rule )
    {
        this.mapByTable.put( rule.getTableName(), rule );
        Class<?> entityClass = rule.getEntityClass();
        if ( entityClass != null )
        {
            this.mapByEntity.put( entityClass.getName(), rule );
        }
    }

    /**
     * Return the rules by table.
     */
    public TableInvalidation getTableRuleByName( String tableName )
    {
        return this.mapByTable.get( tableName.toLowerCase() );
    }

    /**
     * Return the rules by entity.
     */
    public TableInvalidation getTableRuleByEntity( String entityClass )
    {
        return this.mapByEntity.get( entityClass );
    }

    /**
     * Return the rules by entity.
     */
    public TableInvalidation getTableRuleByEntity( Class<?> entityClass )
    {
        return getTableRuleByEntity( entityClass.getName() );
    }

    /**
     * Return all query cache regions.
     */
    public HashSet<String> getQueryCacheRegions()
    {
        return this.queryCacheRegions;
    }

    /**
     * Add query cache region.
     */
    public void addQueryCacheRegion( String cacheRegion )
    {
        this.queryCacheRegions.add( cacheRegion );
    }
}
