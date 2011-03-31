/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeprecatedFilesInput
    extends AbstractInput
    implements Serializable
{

    private static final long serialVersionUID = -5440598741791755353L;

    private List<FileInput> files = new ArrayList<FileInput>();

    public DeprecatedFilesInput( String name )
    {
        super( InputType.FILES, name );
    }

    public DeprecatedFilesInput addFile( Integer contentKey )
    {
        files.add( new FileInput( getName(), contentKey ) );
        return this;
    }

    public List<FileInput> getFiles()
    {
        return files;
    }
}
