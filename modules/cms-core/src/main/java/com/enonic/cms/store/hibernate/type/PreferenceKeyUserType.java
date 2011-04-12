/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;

import com.enonic.cms.core.preferences.PreferenceKey;


public class PreferenceKeyUserType
    extends AbstractKeyType
{

    public PreferenceKeyUserType()
    {
        super( "preference_key", PreferenceKey.class, Types.VARCHAR );
    }

    public Object get( ResultSet rs, String name )
        throws HibernateException, SQLException
    {

        String key = rs.getString( name );
        if ( key == null )
        {
            return null;
        }
        return new PreferenceKey( key );
    }

    public void set( PreparedStatement st, Object value, int index )
        throws HibernateException, SQLException
    {
        st.setString( index, value.toString() );
    }

    public String toString( Object value )
        throws HibernateException
    {
        return value.toString();
    }

    public Object fromStringValue( String s )
    {
        return new PreferenceKey( s );
    }

}