/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api;

import java.io.InputStream;
import java.util.Properties;

public final class Version
{
    private final static Properties PROPS =
        loadProperties();

    private final static String TITLE =
        "Enonic CMS";
    
    private final static String COPYRIGHT =
        "Copyright (c) 2000-2011 Enonic AS";

    /**
     * Return the version title.
     */
    public static String getTitle()
    {
        return TITLE;
    }

    /**
     * Return the copyright notice.
     */
    public static String getCopyright()
    {
        return COPYRIGHT;
    }

    /**
     * Return the version number.
     */
    public static String getVersion()
    {
        final String version = PROPS.getProperty("version", "x.x.x");
        final String timestamp = getTimestamp();

        if ((timestamp != null) && version.endsWith("-SNAPSHOT")) {
            return version.replaceAll("-SNAPSHOT", "-" + timestamp);
        }

        return version;
    }

    /**
     * Return the version title.
     */
    public static String getTitleAndVersion()
    {
        return getTitle() + " " + getVersion();
    }

    public static String getTimestamp()
    {
        return PROPS.getProperty("timestamp", null);
    }

    /**
     * Print out version.
     */
    public static void main( String[] args )
    {
        System.out.println( getTitleAndVersion() );
    }

    private static Properties loadProperties()
    {
        final Properties props = new Properties();
        final InputStream in = Version.class.getResourceAsStream("version.properties");
        if (in == null) {
            return props;
        }

        try {
            props.load(in);
        } catch (Exception e) {
            // Do nothing
        }

        return props;
    }
}
