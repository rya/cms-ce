/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the translated query.
 */
public final class TranslatedQuery
{
    /**
     * Query.
     */
    private final String query;

    /**
     * From index.
     */
    private final int index;

    /**
     * Query count.
     */
    private final int count;

    /**
     * Parameters to set on the query.
     */
    private Map<String, Object> parameters;

    /**
     * Construct the query.
     */
    public TranslatedQuery( String query, int index, int count, Map<String, Object> parameters )
    {
        this.query = query;
        this.index = index;
        this.count = count;
        this.parameters = parameters == null ? new HashMap<String, Object>() : parameters;
    }

    /**
     * Return the HSQL query.
     */
    public String getQuery()
    {
        return this.query;
    }

    /**
     * Return the index.
     */
    public int getIndex()
    {
        return this.index;
    }

    /**
     * Return the count.
     */
    public int getCount()
    {
        return this.count;
    }

    public void addParameter( String key, Object value )
    {
        parameters.put( key, value );
    }

    public Map<String, Object> getParameters()
    {
        return parameters;
    }
}
