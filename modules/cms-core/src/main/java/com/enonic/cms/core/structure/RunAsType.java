/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

public enum RunAsType
{
    INHERIT( 0 ),

    DEFAULT_USER( 1 ),

    PERSONALIZED( 2 );

    private Integer key;


    RunAsType( int key )
    {
        this.key = key;
    }

    public Integer getKey()
    {
        return key;
    }

    public static RunAsType get( Integer key )
    {
        for ( RunAsType type : RunAsType.values() )
        {
            if ( type.getKey().intValue() == key )
            {
                return type;
            }
        }
        return null;
    }
}