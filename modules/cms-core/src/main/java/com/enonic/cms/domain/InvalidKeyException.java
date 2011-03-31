/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

public class InvalidKeyException
    extends RuntimeException
    implements NotFoundErrorType
{
    private String key;

    private String message;

    private Class clazz;

    public InvalidKeyException( Integer key, Class<?> clazz )
    {
        this( String.valueOf( key ), clazz );
    }

    public InvalidKeyException( String key, Class<?> clazz )
    {
        this.key = key;
        this.message = "Invalid " + getClassName( clazz ) + ": " + key;
        this.clazz = clazz;
    }

    public InvalidKeyException( Integer key, Class<?> clazz, String detailMessage )
    {
        this.key = String.valueOf( key );
        this.message = "Invalid " + getClassName( clazz ) + " (" + detailMessage + "): " + key;
        this.clazz = clazz;
    }

    public InvalidKeyException( String key, Class<?> clazz, String detailMessage )
    {
        this.key = key;
        this.message = "Invalid " + getClassName( clazz ) + " (" + detailMessage + "): " + key;
        this.clazz = clazz;
    }

    private String getClassName( Class<?> clazz )
    {
        String longName = clazz.getName();
        int start = longName.lastIndexOf( '.' );
        return longName.substring( start + 1 );
    }

    public boolean forClass( Class c )
    {
        return clazz.equals( c );
    }

    public String getKey()
    {
        return key;
    }

    public String getMessage()
    {
        return message;
    }

}
