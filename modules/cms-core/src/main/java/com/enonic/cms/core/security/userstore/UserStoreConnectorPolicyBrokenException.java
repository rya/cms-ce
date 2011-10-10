/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

public class UserStoreConnectorPolicyBrokenException
    extends RuntimeException
{
    public UserStoreConnectorPolicyBrokenException( final String userstoreName, final String userstoreConnectorName, final String message )
    {
        super( buildMessage( userstoreName, userstoreConnectorName, message ) );
    }

    private static String buildMessage( final String userstoreName, final String userstoreConnectorName, final String message )
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "Userstore connector policy broken for userstore '" ).append( userstoreName ).append( "'" );
        buf.append( " using connector '" ).append( userstoreConnectorName ).append( "': " ).append( message );
        return buf.toString();
    }
}