/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractResultSet
    implements ResultSet
{
    protected int fromIndex;

    protected int totalCount;

    private List<String> errorList = new ArrayList<String>();

    protected AbstractResultSet( int fromIndex, int totalCount )
    {
        this.fromIndex = fromIndex;
        this.totalCount = totalCount;
    }

    /**
     * @inheritDoc
     */
    public int getTotalCount()
    {
        return this.totalCount;
    }

    /**
     * @inheritDoc
     */
    public int getFromIndex()
    {
        return this.fromIndex;
    }

    public ResultSet addError( String message )
    {
        errorList.add( message );
        return this;
    }

    public boolean hasErrors()
    {
        return errorList.size() > 0;
    }

    public List<String> getErrors()
    {
        return errorList;
    }
}
