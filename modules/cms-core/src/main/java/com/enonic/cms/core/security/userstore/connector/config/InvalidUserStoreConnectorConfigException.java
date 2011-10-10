/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

public class InvalidUserStoreConnectorConfigException
    extends RuntimeException
{
    public InvalidUserStoreConnectorConfigException( final String configName, final String detailMessage )
    {
        super( createMessage( configName, detailMessage ) );
    }

    public static String createMessage( final String configName, final String detailMessage )
    {
        return "Invalid userstore connector config '" + configName + "'. " + detailMessage;
    }
}
