/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class XmlInput
    extends AbstractStringValueInput
    implements Serializable
{
    private static final long serialVersionUID = -5340534057441421930L;

    /**
     * @param name
     * @param value If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public XmlInput( String name, String value )
    {
        super( InputType.XML, name, value );
    }
}