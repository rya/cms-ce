/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.xml;

/**
 * This class implement the xml tool exception.
 */
public final class XMLToolException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public XMLToolException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public XMLToolException( String message, Throwable cause )
    {
        super( message, cause );
    }

    /**
     * Construct the exception.
     */
    public XMLToolException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }
}
