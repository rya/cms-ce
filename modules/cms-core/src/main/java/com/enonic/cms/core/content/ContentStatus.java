/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * Dec 11, 2009
 */
public enum ContentStatus
{
    DRAFT( 0, "draft" ),
    SNAPSHOT( 1, "snapshot" ),
    APPROVED( 2, "approved" ),
    ARCHIVED( 3, "archived" );

    // These are calculated
    //PUBLISH_WAITING( 4, "waiting" ),
    //PUBLISHED( 5, "published" ),
    //PUBLISH_EXPIRED( 6, "publish-expired" );


    private int key;

    private String name;

    ContentStatus( int key, String name )
    {
        this.key = key;
        this.name = name;
    }

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public static ContentStatus get( Integer value )
    {
        if ( value == null )
        {
            return null;
        }
        for ( ContentStatus s : ContentStatus.values() )
        {
            if ( s.key == value )
            {
                return s;
            }
        }
        return null;
    }
}
