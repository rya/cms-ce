/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public abstract class AbstractIntValueInput
    extends AbstractInput
    implements Serializable, IntValueInput
{

    private static final long serialVersionUID = 291449569158030474L;

    private Integer value;


    protected AbstractIntValueInput( InputType type, String name, Integer value )
    {
        super( type, name );
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }

    public Integer getValueAsInt()
    {
        return value;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "name = " ).append( getName() );
        s.append( ", value = " ).append( getValueAsInt() );
        return s.toString();
    }
}