/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

/**
 * This exception is used by the query parser.
 */
public final class QueryParserException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public QueryParserException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public QueryParserException( Exception cause )
    {
        super( cause.getMessage() );
    }
}
