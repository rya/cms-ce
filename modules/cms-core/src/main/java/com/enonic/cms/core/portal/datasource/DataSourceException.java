package com.enonic.cms.core.portal.datasource;

public final class DataSourceException
    extends RuntimeException
{
    public DataSourceException(final String message)
    {
        super(message);
    }

    public DataSourceException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
