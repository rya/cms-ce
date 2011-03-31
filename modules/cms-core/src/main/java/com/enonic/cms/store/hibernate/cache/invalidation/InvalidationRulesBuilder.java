/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.cache.invalidation;

import java.util.HashMap;
import java.util.Iterator;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Value;

/**
 * This class implements the invalidation rules builder.
 */
public final class InvalidationRulesBuilder
{
    /**
     * Invalidation rules map.
     */
    private final InvalidationRules rules;

    /**
     * Hibernate configuraiton.
     */
    private final Configuration configuration;

    /**
     * Manual map of table names to cache names.
     */
    private final HashMap<String, String[]> tableToCache;

    /**
     * Construct the builder.
     */
    public InvalidationRulesBuilder( Configuration configuration )
    {
        this.rules = new InvalidationRules();
        this.configuration = configuration;
        this.tableToCache = new HashMap<String, String[]>();

        // Add manual cache name assosications
        // addTableToCacheNames("tmenuitem", "page");
        // addTableToCacheNames("tcontentobject", "page");
    }

    /**
     * Add manual cache name association.
     */
    private void addTableToCacheNames( String tableName, String... cacheNames )
    {
        this.tableToCache.put( tableName.toLowerCase(), cacheNames );
    }

    /**
     * Build the rules.
     */
    public InvalidationRules build()
    {
        // Create all rules first
        for ( Iterator i = this.configuration.getClassMappings(); i.hasNext(); )
        {
            createTableRule( (RootClass) i.next() );
        }

        // Build the mapping
        for ( Iterator i = this.configuration.getClassMappings(); i.hasNext(); )
        {
            buildMapping( (RootClass) i.next() );
        }

        // Add query cache regions
        for ( Object tmp : this.configuration.getNamedQueries().values() )
        {
            addQueryCacheRegion( (NamedQueryDefinition) tmp );
        }

        return this.rules;
    }

    /**
     * Build the root class.
     */
    private void createTableRule( RootClass mapping )
    {
        addTableRule( mapping.getTable().getName(), mapping.getClassName() );
    }

    /**
     * Add query cache region.
     */
    private void addQueryCacheRegion( NamedQueryDefinition mapping )
    {
        String cacheRegion = mapping.getCacheRegion();
        if ( cacheRegion != null )
        {
            this.rules.addQueryCacheRegion( cacheRegion );
        }
    }

    /**
     * Build the root class.
     */
    private void buildMapping( RootClass mapping )
    {
        for ( Iterator i = mapping.getPropertyIterator(); i.hasNext(); )
        {
            buildMapping( (Property) i.next() );
        }
    }

    /**
     * Build the property mapping.
     */
    private void buildMapping( Property mapping )
    {
        Value value = mapping.getValue();
        if ( value != null )
        {
            buildMapping( value );
        }
    }

    /**
     * Build the value mapping.
     */
    private void buildMapping( Value mapping )
    {
        if ( mapping instanceof Collection )
        {
            buildMapping( (Collection) mapping );
        }
    }

    /**
     * Build the value mapping.
     */
    private void buildMapping( Collection mapping )
    {
        String cacheRegion = mapping.getCacheRegionName();
        String collectionTable = mapping.getCollectionTable().getName();
        TableInvalidation rules = getOrCreateTableRule( collectionTable );
        rules.addCollectionRole( cacheRegion );
    }

    /**
     * Add invalidation rules.
     */
    private TableInvalidation addTableRule( String tableName, String className )
    {
        Class entityClass = className != null ? findClass( className ) : null;
        TableInvalidation rule = new TableInvalidation( tableName, entityClass );
        this.rules.addTableRule( rule );

        String[] cacheNames = this.tableToCache.get( rule.getTableName() );
        if ( cacheNames != null )
        {
            for ( String cacheName : cacheNames )
            {
                rule.addCacheName( cacheName );
            }
        }

        return rule;
    }

    /**
     * Return or create rule by table.
     */
    private TableInvalidation getOrCreateTableRule( String tableName )
    {
        TableInvalidation rule = this.rules.getTableRuleByName( tableName );
        if ( rule == null )
        {
            rule = addTableRule( tableName, null );
        }

        return rule;
    }

    /**
     * Find the class.
     */
    private Class findClass( String className )
    {
        try
        {
            return Class.forName( className );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
