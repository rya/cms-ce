/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeprecatedImagesInput
    extends AbstractInput
    implements Serializable
{

    private static final long serialVersionUID = -361568442672011904L;

    private List<ImageInput> items = new ArrayList<ImageInput>();

    public DeprecatedImagesInput( String name )
    {
        super( InputType.IMAGES, name );
    }

    public DeprecatedImagesInput add( Integer contentKey )
    {
        items.add( new ImageInput( getName(), contentKey ) );
        return this;
    }

    public DeprecatedImagesInput add( Integer contentKey, String text )
    {
        items.add( new ImageInput( getName(), contentKey ).setText( text ) );
        return this;
    }

    public List<ImageInput> getImages()
    {
        return items;
    }

}