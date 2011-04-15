package com.enonic.cms.api.plugin;

import java.text.MessageFormat;

public final class PluginException
    extends RuntimeException
{
    public PluginException( final String message, final Object... args )
    {
        super( MessageFormat.format( message, args ) );
    }

    public PluginException( final Throwable cause, final String message, final Object... args )
    {
        super( MessageFormat.format( message, args ), cause );
    }

    public PluginException( final Throwable cause )
    {
        super( cause );
    }
}
