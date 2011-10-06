/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.Arrays;
import java.util.List;

/**
 * Jul 27, 2010
 */
public abstract class AbstractInvalidContentQueryException
    extends RuntimeException
{
    public AbstractInvalidContentQueryException( String message )
    {
        super( message );
    }

    static String buildMessage( String name, String... issues )
    {
        List<String> issueList = Arrays.asList( issues );
        StringBuffer message = new StringBuffer();
        message.append( "The " + name + " have the following issues: " ).append( issueList.get( 0 ) );
        for ( int i = 1; i < issueList.size(); i++ )
        {
            message.append( "\n" ).append( issueList.get( i ) );
        }
        return message.toString();
    }
}
