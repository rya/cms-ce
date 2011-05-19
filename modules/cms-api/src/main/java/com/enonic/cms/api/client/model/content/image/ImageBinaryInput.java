package com.enonic.cms.api.client.model.content.image;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.BinaryInput;

public class ImageBinaryInput
        extends BinaryInput
        implements Serializable
{

    public ImageBinaryInput( int existingBinaryKey )
    {
        super( "binarydata", existingBinaryKey );
    }

    public ImageBinaryInput( byte[] binary, String binaryName )
    {
        super( "binarydata", binary, binaryName );
    }
}