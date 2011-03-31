/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content.file;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.TextInput;

public class FileNameInput
    extends TextInput
    implements Serializable
{

    private static final long serialVersionUID = -8975870015698494500L;

    public FileNameInput( String value )
    {
        super( "name", value );
    }
}
