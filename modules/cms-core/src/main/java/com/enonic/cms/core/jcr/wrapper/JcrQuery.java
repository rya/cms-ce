/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

public interface JcrQuery
{
    public JcrNodeIterator execute();

    public JcrQuery bindValue( String varName, Object value );

    public JcrQuery setLimit(long limit);

    public JcrQuery setOffset(long offset);

}
