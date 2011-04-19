/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import com.enonic.cms.framework.util.PropertiesUtil;

/**
 * Properties for Vertical Site. Loaded from default properties defined in default.properties file and custom properties file specified a
 * run-time.
 */
public final class VerticalProperties
{
    private static VerticalProperties verticalProperties;

    private Properties properties;

    public static VerticalProperties getVerticalProperties()
    {
        return verticalProperties;
    }

    public VerticalProperties()
    {
        verticalProperties = this;
    }

    public void setProperties( Properties properties )
    {
        this.properties = properties;
    }

    public boolean getPropertyAsBoolean( final String key, final boolean defaultValue )
    {
        final String property = getProperty( key );

        if ( property == null )
        {
            return defaultValue;
        }
        if ( property.toLowerCase().equals( "true" ) )
        {
            return true;
        }
        if ( property.toLowerCase().equals( "false" ) )
        {
            return false;
        }
        return defaultValue;
    }

    public int getPropertyAsInt( String key )
    {
        String s = getProperty( key );
        return Integer.parseInt( s );
    }

    public String getProperty( final String key )
    {
        final String systemProperty = StringUtils.trimToNull( System.getProperty( key ) );
        if ( systemProperty != null )
        {
            return systemProperty;
        }
        return StringUtils.trimToNull( properties.getProperty( key ) );
    }

    public String getProperty( final String key, final String defaultValue )
    {
        final String systemProperty = StringUtils.trimToNull( System.getProperty( key ) );
        if ( systemProperty != null )
        {
            return systemProperty;
        }
        return StringUtils.trimToNull( properties.getProperty( key, defaultValue ) );
    }

    public Properties getSubSet( final String base )
    {
        return PropertiesUtil.getSubSet( properties, base );
    }

    public String getSMTPHost()
    {
        return getMailSmtpHost();
    }

    public int getAutologinTimeout()
    {
        return Integer.parseInt( getProperty( "com.enonic.vertical.presentation.autologinTimeout", "30" ) );
    }

    public String getAdminPassword()
    {
        return getProperty( "cms.admin.password" );
    }

    public String getAdminEmail()
    {
        return getProperty( "cms.admin.email" );
    }

    public String getAdminNewPasswordMailSubject()
    {
        return getProperty( "cms.admin.newPasswordMailSubject" );
    }

    public String getAdminNewPasswordMailBody()
    {
        return getProperty( "cms.admin.newPasswordMailBody" );
    }

    public String getMailSmtpHost()
    {
        return getProperty( "cms.mail.smtpHost" );
    }

    public String getUrlCharacterEncoding()
    {
        return getProperty( "cms.url.characterEncoding" );
    }

    public String getDataSourceUserAgent()
    {
        return getProperty( "cms.enonic.vertical.presentation.dataSource.getUrl.userAgent" );
    }

    public String getDatasourceDefaultResultRootElement()
    {
        return getProperty( "cms.datasource.defaultResultRootElement" );
    }
}
