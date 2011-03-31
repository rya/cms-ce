/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class BooleanInput
    extends AbstractInput
    implements Serializable
{
    private static final long serialVersionUID = -2506295961977327065L;

    private Boolean value;

    /**
     * @param name
     * @param value If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public BooleanInput( String name, Boolean value )
    {
        super( InputType.BOOLEAN, name );

        this.value = value;
    }

    public Boolean getValue()
    {
        return value;
    }

}