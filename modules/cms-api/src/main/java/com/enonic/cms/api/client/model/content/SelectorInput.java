/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

/**
 * Use this class for inputting values for radio buttons or drop down boxes.
 */
public class SelectorInput
    extends AbstractStringValueInput
    implements Serializable
{
    private static final long serialVersionUID = -7498420759751730017L;

    /**
     * @param name
     * @param value If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public SelectorInput( String name, String value )
    {
        super( InputType.SELECTOR, name, value );
    }
}