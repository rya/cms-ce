/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class UrlInput
    extends AbstractStringValueInput
    implements Serializable
{
    private static final long serialVersionUID = -8570629603178360225L;

    /**
     * @param name
     * @param value If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public UrlInput( String name, String value )
    {
        super( InputType.URL, name, value );
    }
}