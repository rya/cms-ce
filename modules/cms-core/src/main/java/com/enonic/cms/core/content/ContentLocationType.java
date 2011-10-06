/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;


public enum ContentLocationType
{
    MENUITEM( 1, "menuitem" ),
    SECTION( 2, "section" ),
    SECTION_AND_SECTION_HOME( 3, "section_and_sectionhome" ),
    SECTION_HOME( 4, "sectionhome" );

    private int key;

    private String shortName;

    ContentLocationType( int key, String shortName )
    {
        this.key = key;
        this.shortName = shortName;
    }

    public int getKey()
    {
        return key;
    }

    public String getShortName()
    {
        return shortName;
    }
}
