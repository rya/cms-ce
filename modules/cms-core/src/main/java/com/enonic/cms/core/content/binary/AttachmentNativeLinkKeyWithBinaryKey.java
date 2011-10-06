/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.core.content.ContentKey;

public class AttachmentNativeLinkKeyWithBinaryKey
    extends AttachmentNativeLinkKey
{
    private BinaryDataKey binaryKey = null;

    public AttachmentNativeLinkKeyWithBinaryKey( ContentKey contentKey, BinaryDataKey binaryKey )
    {
        super( contentKey );
        this.binaryKey = binaryKey;
    }

    public BinaryDataKey getBinaryKey()
    {
        return binaryKey;
    }

    public void setBinaryKey( int binaryKey )
    {
        this.binaryKey = new BinaryDataKey( binaryKey );
    }

    public void setBinaryKey( String binaryKey )
    {
        if ( binaryKey == null || binaryKey.equals( "" ) )
        {
            return;
        }

        try
        {
            this.binaryKey = new BinaryDataKey( binaryKey );
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalArgumentException( "Argument is not a valid binary key" );
        }
    }

    public String asUrlRepresentation()
    {
        String key = getContentKey() + "/binary/" + getBinaryKey();

        if ( getExtension() != null )
        {
            return key + "." + getExtension();
        }
        return key;
    }

}
