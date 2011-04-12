/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.portlet;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;

public class PortletKey
    extends AbstractIntegerBasedKey
    implements Serializable
{

    public PortletKey( String key )
    {
        init( key );
    }

    public PortletKey( int key )
    {
        init( key );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        PortletKey key = (PortletKey) o;

        return intValue == key.intValue;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 325, 237 ).append( intValue ).toHashCode();
    }

    public static PortletKey parse( String str )
    {

        if ( str == null )
        {
            return null;
        }

        return new PortletKey( str );
    }
}
