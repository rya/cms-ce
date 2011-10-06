/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

/**
 * This class implements the index value query.
 */
public final class AggregatedQuery
    extends AbstractQuery
{
    /**
     * Path of index value.
     */
    private final String field;

    /**
     * Construct the query.
     */
    public AggregatedQuery( String field )
    {
        this.field = field;
    }

    /**
     * Return the field.
     */
    public String getField()
    {
        return this.field;
    }
}
