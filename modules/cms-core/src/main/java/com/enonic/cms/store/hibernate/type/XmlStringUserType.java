/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.EqualsHelper;


public class XmlStringUserType
    implements UserType
{
    private Class clazz;

    private int[] sqlTypes;

    private int sqlType;

    public XmlStringUserType()
    {
        this.clazz = String.class;
        this.sqlType = Types.BLOB;
        this.sqlTypes = new int[]{Types.BLOB};
    }

    public int[] sqlTypes()
    {
        return sqlTypes;
    }

    protected int getSqlType()
    {
        return sqlType;
    }

    public boolean equals( Object x, Object y )
        throws HibernateException
    {
        return EqualsHelper.equals( x, y );
    }

    public int hashCode( Object x )
        throws HibernateException
    {
        return x.hashCode();
    }

    public Class returnedClass()
    {
        return clazz;
    }

    public Object deepCopy( Object value )
        throws HibernateException
    {
        return value;
    }

    public Serializable disassemble( Object value )
        throws HibernateException
    {
        if ( value == null )
        {
            return null;
        }
        else
        {
            return (Serializable) value;
        }
    }

    public Object replace( Object original, Object target, Object owner )
        throws HibernateException
    {
        return original;
    }

    public Object assemble( Serializable cached, Object owner )
        throws HibernateException
    {
        return cached;
    }

    public Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {

        String data;

        byte[] bytes = BinaryColumnReader.readBinary( names[0], rs );
        if ( bytes == null )
        {
            data = null;
        }
        else
        {
            try
            {
                data = new String( bytes, "UTF-8" );
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new HibernateException( e );
            }
        }

        return data;

    }

    public void nullSafeSet( PreparedStatement st, Object value, int index )
        throws HibernateException, SQLException
    {
        if ( value == null )
        {
            st.setNull( index, getSqlType() );
        }
        else
        {
            String s = (String) value;
            try
            {
                st.setBytes( index, s.getBytes( "UTF-8" ) );
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new HibernateException( e );
            }
        }
    }

    public boolean isMutable()
    {
        return false;
    }


}