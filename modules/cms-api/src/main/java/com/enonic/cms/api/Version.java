/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api;

import java.util.regex.Pattern;

public final class Version
{
    private final static String TITLE =
        "Enonic CMS";
    
    private final static String COPYRIGHT =
        "Copyright (c) 2000-2011 Enonic AS.";

    private final static String VERSION =
        Version.class.getPackage().getImplementationVersion();

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
        return VERSION != null ? VERSION : "x.x.x";
    }

    /**
     * Return the version title.
     */
    public static String getTitleAndVersion()
    {
        return getTitle() + " " + getVersion();
    }

    /**
     * Print out version.
     */
    public static void main( String[] args )
    {
        System.out.println( getTitleAndVersion() );
    }
}
