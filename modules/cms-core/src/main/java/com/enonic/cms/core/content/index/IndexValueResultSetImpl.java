/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the index value result set.
 */
public final class IndexValueResultSetImpl
    implements IndexValueResultSet
{
    /**
     * Index.
     */
    private final int fromIndex;

    /**
     * Total count.
     */
    private final int totalCount;

    /**
     * List of values.
     */
    private final List<IndexValueResult> list;

    /**
     * Construct the result set.
     */
    public IndexValueResultSetImpl( int fromIndex, int totalCount )
    {
        this.fromIndex = fromIndex;
        this.totalCount = totalCount;
        this.list = new ArrayList<IndexValueResult>();
    }

    /**
     * Return the count.
     */
    public int getCount()
    {
        return this.list.size();
    }

    /**
     * Return from index.
     */
    public int getFromIndex()
    {
        return this.fromIndex;
    }

    /**
     * Return total count.
     */
    public int getTotalCount()
    {
        return this.totalCount;
    }

    /**
     * Return the result.
     */
    public IndexValueResult getIndexValue( int num )
    {
        return this.list.get( num );
    }

    /**
     * Add entry.
     */
    public void add( IndexValueResult result )
    {
        this.list.add( result );
    }
}
