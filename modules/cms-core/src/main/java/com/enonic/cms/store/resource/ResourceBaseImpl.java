/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import java.io.Serializable;
import java.util.Calendar;

import com.enonic.cms.core.resource.*;
import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.ResourceFolder;
import com.enonic.cms.core.resource.ResourceKey;

abstract class ResourceBaseImpl
    implements Serializable, ResourceBase
{
    protected final FileResourceName name;

    protected final FileResourceService service;

    public ResourceBaseImpl( FileResourceService service, FileResourceName name )
    {
        this.service = service;
        this.name = name;
    }

    public String getName()
    {
        return this.name.getName();
    }

    public boolean exists()
    {
        return this.service.getResource( this.name ) != null;
    }

    public String getPath()
    {
        return this.name.getPath();
    }

    public ResourceKey getResourceKey()
    {
        return new ResourceKey( getPath() );
    }

    public ResourceFolder getParentFolder()
    {
        if ( this.name.getParent() != null )
        {
            return new ResourceFolderImpl( this.service, this.name.getParent() );
        }
        else
        {
            return null;
        }
    }

    protected final FileResource ensureResource()
    {
        FileResource resource = this.service.getResource( this.name );
        if ( resource == null )
        {
            throw new IllegalStateException( "Resource [" + this.name + "] does not exist" );
        }

        return resource;
    }

    public Calendar getLastModified()
    {
        return ensureResource().getLastModified().toGregorianCalendar();
    }

    public boolean isHidden()
    {
        return this.name.isHidden();
    }

    public ResourceKey moveTo( ResourceFolder destinationFolder )
    {
        if ( destinationFolder == null )
        {
            throw new IllegalArgumentException( "Destination cannot be null" );
        }
        else if ( !( destinationFolder instanceof ResourceFolderImpl ) )
        {
            throw new IllegalArgumentException( "Destination '" + destinationFolder.getResourceKey() + "' must be a folder" );
        }

        boolean exists = ( (ResourceBaseImpl) destinationFolder ).exists();
        if ( !exists )
        {
            throw new IllegalArgumentException( "Destination '" + destinationFolder.getResourceKey() + "' does not exist" );
        }

        FileResourceName destName = new FileResourceName( ( (ResourceBaseImpl) destinationFolder ).name, this.name.getName() );
//        (destName);
        this.service.moveResource( this.name, destName );
        return new ResourceKey( ( (ResourceBaseImpl) destinationFolder ).name.getPath() );
    }

    public String getETag()
    {
        return ensureResource().getBlobKey();
    }
}
