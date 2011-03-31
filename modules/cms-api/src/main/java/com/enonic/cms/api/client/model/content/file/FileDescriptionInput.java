/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content.file;

import java.io.Serializable;

import com.enonic.cms.api.client.model.content.TextAreaInput;

public class FileDescriptionInput
    extends TextAreaInput
    implements Serializable
{

    private static final long serialVersionUID = 2102540486236670427L;

    public FileDescriptionInput( String value )
    {
        super( "description", value );
    }
}