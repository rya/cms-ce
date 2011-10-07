/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote.plugin;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jul 14, 2010
 * Time: 1:59:55 PM
 */
public class RemoteUserStorePluginConnectionException
    extends RemoteUserStorePluginException
{

    public RemoteUserStorePluginConnectionException( String message )
    {
        super( message );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public RemoteUserStorePluginConnectionException( String message, Throwable cause )
    {
        super( message, cause );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public RemoteUserStorePluginConnectionException( Throwable cause )
    {
        super( cause );    //To change body of overridden methods use File | Settings | File Templates.
    }
}
