/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

/**
 * Jul 9, 2009
 */
public enum LogType
{
    LOGIN( 0 ),
    LOGIN_USERSTORE( 1 ),
    LOGIN_FAILED( 2 ),
    LOGOUT( 3 ),
    ENTITY_CREATED( 4 ),
    ENTITY_UPDATED( 5 ),
    ENTITY_REMOVED( 6 ),
    ENTITY_OPENED( 7 );

    private Integer value;

    LogType( Integer value )
    {
        this.value = value;
    }

    public Integer asInteger()
    {
        return value;
    }

    public static LogType parse( Integer value )
    {
        for ( LogType current : values() )
        {
            if ( current.asInteger().equals( value ) )
            {
                return current;
            }
        }
        return null;
    }
}
