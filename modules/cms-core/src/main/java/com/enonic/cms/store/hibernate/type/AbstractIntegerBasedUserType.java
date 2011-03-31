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


public abstract class AbstractIntegerBasedUserType<T>
    extends AbstractBaseUserType
{
    protected AbstractIntegerBasedUserType( Class clazz )
    {
        super( clazz, Types.INTEGER );
    }

    public final Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        int value = rs.getInt( names[0] );
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
        else if ( value instanceof Integer )
        {
            st.setInt( index, (Integer) value );
        }
        else
        {
            //noinspection unchecked
            final T objectValue = (T) value;
            final Integer intValue = getIntegerValue( objectValue );
            st.setInt( index, intValue );
        }
    }

    public abstract T get( final int value );

    public abstract Integer getIntegerValue( final T value );

}