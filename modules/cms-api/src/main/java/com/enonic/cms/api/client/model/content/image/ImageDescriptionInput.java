package com.enonic.cms.api.client.model.content.image;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.TextAreaInput;

public class ImageDescriptionInput
    extends TextAreaInput
    implements Serializable
{
    private static final long serialVersionUID = -3893002034248445490L;

    public ImageDescriptionInput( String value )
    {
        super( "description", value );
    }
}
