/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

/**
 * Jul 9, 2009
 */
public enum Table
{
    CONTENT( 0 ),
    MENUITEM( 1 ),
    SECTION( 3 ),
    RESOURCE( 4 );

    private Integer key;

    Table( Integer key )
    {
        this.key = key;
    }

    public Integer asInteger()
    {
        return key;
    }

    public static Table parse( Integer value )
    {
        for ( Table current : Table.values() )
        {
            if ( current.asInteger().equals( value ) )
            {
                return current;
            }
        }
        return null;
    }

}
