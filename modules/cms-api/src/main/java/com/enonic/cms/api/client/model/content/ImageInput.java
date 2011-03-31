/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;

public class ImageInput
    extends AbstractIntValueInput
    implements Serializable
{
    private static final long serialVersionUID = 5644565717224129009L;

    private String text;

    /**
     * @param name
     * @param contentKey If you supply null as input value, the existing value will be removed in a 'replace new' scenario.
     */
    public ImageInput( String name, Integer contentKey )
    {
        super( InputType.IMAGE, name, contentKey );
    }

    public ImageInput setText( String value )
    {
        this.text = value;
        return this;
    }

    public String getText()
    {
        return this.text;
    }
}