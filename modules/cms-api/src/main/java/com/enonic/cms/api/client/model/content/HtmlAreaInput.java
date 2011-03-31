/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class HtmlAreaInput
    extends AbstractStringValueInput
    implements Serializable
{
    private static final long serialVersionUID = 5376163221831940395L;

    /**
     * @param name
     * @param value If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public HtmlAreaInput( String name, String value )
    {
        super( InputType.HTML_AREA, name, value );
    }
}