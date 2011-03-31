/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.hibernate.util.EqualsHelper;


public abstract class AbstractBaseUserType
    implements UserType
{
    private Class clazz;

    private int[] sqlTypes;

    private int sqlType;

    protected AbstractBaseUserType( Class clazz, int sqlType )
    {
        this.clazz = clazz;
        this.sqlType = sqlType;
        this.sqlTypes = new int[]{sqlType};
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

}