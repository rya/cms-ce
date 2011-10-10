/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

/**
 *
 */
public enum UserType
{
    NORMAL( 0, "Normal", false ),

    ANONYMOUS( 1, "Anonymous", true ),

    ADMINISTRATOR( 2, "Administrator", true );

    private Integer key;

    private String name;

    private boolean builtIn;

    UserType( Integer typeKey, String typeName, boolean builtIn )
    {
        this.key = typeKey;
        this.name = typeName;
        this.builtIn = builtIn;
    }

    public static UserType getByKey( int key )
    {
        for ( UserType type : values() )
        {
            if ( type.getKey() == key )
            {
                return type;
            }
        }

        return null;
    }

    public Integer getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public boolean isAnonymous()
    {
        return key.equals( ANONYMOUS.key );
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }
}
