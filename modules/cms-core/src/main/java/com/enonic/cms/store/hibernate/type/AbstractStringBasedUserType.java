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


public abstract class AbstractStringBasedUserType<T>
    extends AbstractBaseUserType
{
    protected AbstractStringBasedUserType( Class clazz )
    {
        super( clazz, Types.VARCHAR );
    }

    public final Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        String value = rs.getString( names[0] );
        if ( rs.wasNull() )
        {
            return null;
        }

        return get( value );
    }

    public final void nullSafeSet( PreparedStatement st, Object value, int index )
        throws HibernateException, SQLException
    {
        if ( value == null )
        {
            st.setNull( index, getSqlType() );
        }
        else if ( value instanceof String )
        {
            st.setString( index, value.toString() );
        }
        else
        {
            //noinspection unchecked
            final T objectValue = (T) value;
            final String stringValue = getStringValue( objectValue );
            st.setString( index, stringValue );
        }
    }

    public abstract T get( final String value );

    public abstract String getStringValue( final T value );

}
