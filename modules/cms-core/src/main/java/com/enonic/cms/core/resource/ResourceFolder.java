/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.util.List;

public interface ResourceFolder
    extends ResourceBase
{
    ResourceFolder getFolder( String name );

    ResourceFile getFile( String name );

    List<ResourceFolder> getFolders();

    List<ResourceFile> getFiles();

    ResourceFolder createFolder( String name );

    ResourceFile createFile( String name );

    void removeFolder( String name );

    void removeFile( String name );

}
