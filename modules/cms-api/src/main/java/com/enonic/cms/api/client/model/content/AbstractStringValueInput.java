/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public abstract class AbstractStringValueInput
    extends AbstractInput
    implements Serializable, StringValueInput
{

    private static final long serialVersionUID = 163193387005073086L;

    private String value;


    protected AbstractStringValueInput( InputType type, String name, String value )
    {
        super( type, name );
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public String getValueAsString()
    {
        return value;
    }

    public int getLength()
    {
        if ( value == null )
        {
            return 0;
        }

        return value.length();
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "name = " ).append( getName() );
        s.append( ", value = " ).append( getValueAsString() );
        return s.toString();
    }
}
