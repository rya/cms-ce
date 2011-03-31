/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.Collection;

/**
 * This class implements variois parameter checks.
 */
public final class ParameterCheck
{
    /**
     * Check if the parameter is the supplied value is not null.
     */
    public static void mandatory( String name, Object value )
    {
        if ( ( name == null ) || ( name.length() == 0 ) )
        {
            throw new IllegalArgumentException( "Parameter name is mandatory" );
        }

        if ( value == null )
        {
            throw new IllegalArgumentException( name + " is a mandatory parameter" );
        }
    }

    /**
     * Check if the parameter is the supplied value is not null.
     */
    public static void mandatory( String name, String value )
    {
        if ( ( name == null ) || ( name.length() == 0 ) )
        {
            throw new IllegalArgumentException( "Parameter name is mandatory" );
        }

        if ( ( value == null ) || ( value.length() == 0 ) )
        {
            throw new IllegalArgumentException( name + " is a mandatory parameter" );
        }
    }

    /**
     * Checks that the collection parameter contains at least one item.
     */
    public static void mandatory( String name, Collection value )
    {
        if ( ( name == null ) || ( name.length() == 0 ) )
        {
            throw new IllegalArgumentException( "Parameter name is mandatory" );
        }

        if ( ( value == null ) || ( value.size() == 0 ) )
        {
            throw new IllegalArgumentException( name + " collection must contain at least one item" );
        }
    }
}
