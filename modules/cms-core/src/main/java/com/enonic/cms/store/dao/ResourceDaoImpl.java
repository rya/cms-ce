/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import javax.inject.Inject;

import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceFolder;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.store.resource.FileResourceService;
import com.enonic.cms.store.resource.ResourceFolderImpl;

public class ResourceDaoImpl
    implements ResourceDao
{
    @Inject
    private FileResourceService fileResourceService;

    public ResourceFolder getResourceRoot()
    {
        return doGetResourceRoot();
    }

    public ResourceFile getResourceFile( ResourceKey resourceKey )
    {
        if ( resourceKey == null )
        {
            throw new IllegalArgumentException( "Given resourceKey cannot be null" );
        }
        return doGetResourceRoot().getFile( resourceKey.toString() );
    }

    public ResourceFolder getResourceFolder( ResourceKey resourceKey )
    {
        if ( resourceKey == null )
        {
            throw new IllegalArgumentException( "Given resourceKey cannot be null" );
        }
        return doGetResourceRoot().getFolder( resourceKey.toString() );
    }

    private ResourceFolder doGetResourceRoot()
    {
        return new ResourceFolderImpl( this.fileResourceService, new FileResourceName( "/" ) );
    }
}