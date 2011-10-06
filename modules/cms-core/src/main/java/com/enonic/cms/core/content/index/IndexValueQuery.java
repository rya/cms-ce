/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

/**
 * This class implements the index value query.
 */
public final class IndexValueQuery
    extends AbstractQuery
{
    /**
     * Path of index value.
     */
    private final String field;

    private int index = 0;

    private int count = Integer.MAX_VALUE;

    private boolean descOrder;

    public IndexValueQuery( String field )
    {
        this.field = field;
    }

    public String getField()
    {
        return this.field;
    }

    public int getIndex()
    {
        return this.index;
    }

    public void setIndex( int index )
    {
        this.index = index;
    }

    public int getCount()
    {
        return this.count;
    }

    public void setCount( int count )
    {
        this.count = count;
    }

    public boolean isDescOrder()
    {
        return this.descOrder;
    }

    public void setDescOrder( boolean descOrder )
    {
        this.descOrder = descOrder;
    }
}
