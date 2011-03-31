/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

/**
 * This class implements the xml exception.
 */
public final class XMLException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public XMLException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public XMLException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public XMLException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
