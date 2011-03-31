/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content.file;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.BinaryInput;

public class FileBinaryInput
    extends BinaryInput
    implements Serializable
{

    private static final long serialVersionUID = 5420630140813139647L;

    public FileBinaryInput( int existingBinaryKey )
    {
        super( "binarydata", existingBinaryKey );
    }

    public FileBinaryInput( byte[] binary, String binaryName )
    {
        super( "binarydata", binary, binaryName );
    }
}