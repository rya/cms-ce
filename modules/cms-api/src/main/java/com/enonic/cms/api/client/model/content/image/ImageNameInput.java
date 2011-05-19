package com.enonic.cms.api.client.model.content.image;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.TextInput;

public class ImageNameInput
        extends TextInput
        implements Serializable
{

    public ImageNameInput( String value )
    {
        super( "name", value );
    }
}