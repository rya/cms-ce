/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class implements the command client.
 */
public final class ClientException
    extends RuntimeException
{

    private static final long serialVersionUID = 1300813464472621779L;

    public ClientException( String message )
    {
        super( message );
    }

    public ClientException( Throwable cause )
    {
        super( buildMessage( cause ) );
    }

    private static String buildMessage( Throwable cause )
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter exceptionWriter = new PrintWriter( stringWriter );
        cause.printStackTrace( exceptionWriter );
        StringBuffer message = new StringBuffer();
        message.append( "Exception message is: '" ).append( cause.getMessage() );
        message.append( "', exception class is: " ).append( cause.getClass() );
        message.append( System.getProperty( "line.separator" ) );
        message.append( "StackTrace: " );
        message.append( System.getProperty( "line.separator" ) );
        message.append( stringWriter.toString() );
        return message.toString();
    }
}