/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import org.hibernate.EntityMode;
import org.hibernate.type.ImmutableType;


public abstract class AbstractKeyType
    extends ImmutableType
{

    private String name;

    private Class clazz;

    private int sqlType;

    protected AbstractKeyType( String name, Class clazz, int sqlType )
    {

        this.name = name;
        this.clazz = clazz;
        this.sqlType = sqlType;
    }

    public boolean isEqual( Object x, Object y )
    {
        return x == y || ( x != null && y != null && x.equals( y ) );
    }

    public int getHashCode( Object x, EntityMode entityMode )
    {
        return x.hashCode();
    }

    public Class getReturnedClass()
    {
        return clazz;
    }

    public String getName()
    {
        return name;
    }

    public int sqlType()
    {
        return sqlType;
    }
}
