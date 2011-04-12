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
    DRAFT( 0, "draft", "Draft" ),
    SNAPSHOT( 1, "snapshot", "Snapshot" ),
    APPROVED( 2, "approved", "Approved" ),
    ARCHIVED( 3, "archived", "Archived" );

    // These are calculated
    //PUBLISH_WAITING( 4, "waiting" ),
    //PUBLISHED( 5, "published" ),
    //PUBLISH_EXPIRED( 6, "publish-expired" );


    private int key;

    private String name;
    private final String label;

    ContentStatus( int key, String name, String label )
    {
        this.key = key;
        this.name = name;
        this.label = label;
    }

    public int getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
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
