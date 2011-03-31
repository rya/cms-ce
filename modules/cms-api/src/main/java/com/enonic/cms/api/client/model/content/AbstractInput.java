/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public abstract class AbstractInput
    implements Input, Serializable
{

    private static final long serialVersionUID = -9210995121218180981L;

    private InputType type;

    private String name;

    protected AbstractInput( InputType type, String name )
    {
        this.type = type;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public InputType getType()
    {
        return type;
    }
}