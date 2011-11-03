/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

public final class UserField
{
    private final UserFieldType type;

    private Object value;

    public UserField( UserFieldType type )
    {
        this( type, null );
    }

    public UserField( UserFieldType type, Object value )
    {
        this.type = type;
        setValue( value );
    }

    public UserFieldType getType()
    {
        return this.type;
    }

    public boolean isOfType( UserFieldType type )
    {
        return this.type == type;
    }

    public Object getValue()
    {
        return this.value;
    }

    public void setValue( Object value )
    {
        checkType( value );
        this.value = value;
    }

    private void checkType( Object value )
    {
        if ( value == null )
        {
            return;
        }

        Class<?> clz = value.getClass();
        if ( !this.type.isOfType( clz ) )
        {
            throw new IllegalArgumentException( "Value must be of type [" + clz.getName() + "]" );
        }
    }
}
