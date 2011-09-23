package com.enonic.cms.api.client.model.content.image;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.TextInput;

public class ImageNameInput
        extends TextInput
        implements Serializable
{

    private static final long serialVersionUID = -4097807956530351622L;

    public ImageNameInput( String value )
    {
        super( "name", value );
    }
}