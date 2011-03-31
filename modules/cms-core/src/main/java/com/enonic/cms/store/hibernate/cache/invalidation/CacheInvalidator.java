/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Database;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.datatypes.CharType;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.sql.model.datatypes.IntegerType;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.store.DatabaseAccessor;

/**
 * This class implements the cache invalidator.
 */
public class CacheInvalidator
{
    /**
     * Session factory.
     */
    private final SessionFactory sessionFactory;

    /**
     * Cache manager.
     */
    private final CacheManager cacheMananger;

    /**
     * Invalidation rules.
     */
    private final InvalidationRules invalidationRules;

    private final PrimaryKeyResolver primaryKeyResolver = new PrimaryKeyResolver();

    private Map<String, Column[]> tableMap = new HashMap<String, Column[]>();

    /**
     * Construct the invalidator.
     */
    public CacheInvalidator( Configuration configuration, SessionFactory sessionFactory, CacheManager cacheMananger )
    {
        this.sessionFactory = sessionFactory;
        this.cacheMananger = cacheMananger;
        InvalidationRulesBuilder builder = new InvalidationRulesBuilder( configuration );
        this.invalidationRules = builder.build();

        Database db = DatabaseAccessor.getLatestDatabase();
        Table[] tables = db.getTables();
        for ( Table table : tables )
        {
            tableMap.put( table.getName().toLowerCase(), table.getPrimaryKeys() );
        }
    }

    /**
     * Analyze the SQL. If it's not a select, find the affected table and invalidate it.
     */
    public void invalidateSql( String sql )
    {
        invalidateSql( sql, null );
    }

    /**
     * Analyze the SQL. If it's not a select, find the affected table and invalidate it.
     */
    public void invalidateSql( String sql, List paramList )
    {
        SqlAnalyzer analyzer = new SqlAnalyzer( sql.trim().toLowerCase() );

        if ( analyzer.resolveTableName() != null )
        {
            invalidateTable( analyzer, paramList );
        }
    }

    /**
     * Find primary key. Return null if not found.
     */
    private Serializable findPrimaryKeyValue( String tableName, String sql, List paramList )
    {

        Column[] primaryKeyColumns = tableMap.get( tableName );
        if ( primaryKeyColumns == null )
        {
            return null;
        }

        if ( primaryKeyColumns.length == 1 )
        {

            String columnName = primaryKeyColumns[0].getName().toLowerCase();
            DataType dataType = primaryKeyColumns[0].getType();

            if ( dataType instanceof IntegerType )
            {
                return primaryKeyResolver.resolveIntegerValue( sql, paramList, columnName );
            }
            else if ( dataType instanceof CharType )
            {
                return primaryKeyResolver.resolveStringValue( sql, paramList, columnName );
            }

        }

        return null;
    }


    /**
     * Find the right table to invalidate and make calls to invalidate the necessary domain objects and collections that are affected by the
     * table change.
     */
    private void invalidateTable( SqlAnalyzer analyzer, List paramList )
    {
        TableInvalidation rule = this.invalidationRules.getTableRuleByName( analyzer.resolveTableName() );
        if ( rule != null )
        {
            Serializable primaryKey = null;
            if ( !analyzer.isInsertType() )
            {
                primaryKey = findPrimaryKeyValue( analyzer.resolveTableName(), analyzer.getSql(), paramList );
            }
            invalidateTable( rule, primaryKey, analyzer.isInsertType() );
        }
    }

    /**
     * Invalidate on table.
     */
    private void invalidateTable( TableInvalidation rule, Serializable primaryKey, boolean insertType )
    {
        invalidateNamedQueries();
        invalidateCollectionCaches( rule );
        invalidateOtherCaches( rule );

        if ( !insertType )
        {
            invalidateEntityCache( rule, primaryKey );
        }
    }

    /**
     * Invalidate queries.
     */
    private void invalidateNamedQueries()
    {
        this.sessionFactory.evictQueries();
        for ( String regionName : this.invalidationRules.getQueryCacheRegions() )
        {
            this.sessionFactory.evictQueries( regionName );
        }
    }

    /**
     * Invalidate entity cache.
     */
    private void invalidateEntityCache( TableInvalidation rule, Serializable primaryKey )
    {
        Class entityClass = rule.getEntityClass();
        if ( entityClass != null )
        {

            if ( primaryKey != null )
            {
                this.sessionFactory.evict( entityClass, primaryKey );
            }
            else
            {
                this.sessionFactory.evict( entityClass );
            }
        }
    }

    /**
     * Invalidate collection cache.
     */
    private void invalidateCollectionCaches( TableInvalidation rule )
    {
        for ( String roleName : rule.getCollectionRoles() )
        {
            this.sessionFactory.evictCollection( roleName );
        }
    }

    /**
     * Invalidate other cache.
     */
    private void invalidateOtherCaches( TableInvalidation rule )
    {
        for ( String cacheName : rule.getCacheNames() )
        {
            CacheFacade cache = this.cacheMananger.getCache( cacheName );
            if ( cache != null )
            {
                cache.removeAll();
            }
        }
    }
}
