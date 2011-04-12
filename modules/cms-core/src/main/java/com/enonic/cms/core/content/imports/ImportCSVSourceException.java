/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

/**
 * Feb 7, 2010
 */
public class ImportCSVSourceException
    extends ImportException
{
    public ImportCSVSourceException( int lineNummber, String line, String message )
    {
        super( buildMessage( lineNummber, line, message ) );
    }

    public ImportCSVSourceException( int lineNummber, Throwable t )
    {
        super( buildMessage( lineNummber, t ), t );
    }

    public ImportCSVSourceException( String message, Throwable t )
    {
        super( message, t );
    }

    private static String buildMessage( int lineNummber, Throwable t )
    {
        return "Failed to read line " + lineNummber + ": " + t.getMessage();
    }

    private static String buildMessage( int lineNummber, String line, String message )
    {
        StringBuffer msg = new StringBuffer();
        msg.append( "Error at line " ).append( lineNummber );
        msg.append( ": " ).append( message );
        if ( !message.endsWith( "." ) )
        {
            msg.append( "." );
        }
        msg.append( " Line was: " + line );
        return msg.toString();
    }
}
