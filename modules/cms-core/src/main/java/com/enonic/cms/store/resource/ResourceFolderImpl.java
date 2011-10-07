/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceFolder;

public final class ResourceFolderImpl
    extends ResourceBaseImpl
    implements ResourceFolder
{
    public ResourceFolderImpl( FileResourceService service, FileResourceName name )
    {
        super( service, name );
    }

    public ResourceFolder getFolder( String name )
    {
        FileResource res = this.service.getResource( new FileResourceName( this.name, name ) );
        if ( ( res != null ) && res.isFolder() )
        {
            return new ResourceFolderImpl( this.service, res.getName() );
        }

        return null;
    }

    public ResourceFile getFile( String name )
    {
        FileResource res = this.service.getResource( new FileResourceName( this.name, name ) );
        if ( ( res != null ) && !res.isFolder() )
        {
            return new ResourceFileImpl( this.service, res.getName() );
        }

        return null;
    }

    public List<ResourceFolder> getFolders()
    {
        List<ResourceFolder> folders = new ArrayList<ResourceFolder>();
        for ( FileResourceName child : this.service.getChildren( this.name ) )
        {
            FileResource res = this.service.getResource( child );
            if ( res != null && res.isFolder() )
            {
                folders.add( new ResourceFolderImpl( this.service, res.getName() ) );
            }
        }

        return folders;
    }

    public List<ResourceFile> getFiles()
    {
        List<ResourceFile> files = new ArrayList<ResourceFile>();
        for ( FileResourceName child : this.service.getChildren( this.name ) )
        {
            FileResource res = this.service.getResource( child );
            if ( res != null && !res.isFolder() )
            {
                files.add( new ResourceFileImpl( this.service, res.getName() ) );
            }
        }

        return files;
    }

    public ResourceFolder createFolder( String name )
    {
        this.service.createFolder( new FileResourceName( this.name, name ) );
        FileResource res = this.service.getResource( new FileResourceName( this.name, name ) );

        if ( ( res != null ) && res.isFolder() )
        {
            return new ResourceFolderImpl( this.service, res.getName() );
        }

        return null;
    }

    public ResourceFile createFile( String name )
    {
        this.service.createFile( new FileResourceName( this.name, name ), null );
        FileResource res = this.service.getResource( new FileResourceName( this.name, name ) );

        if ( ( res != null ) && !res.isFolder() )
        {
            return new ResourceFileImpl( this.service, res.getName() );
        }

        return null;
    }

    public void removeFolder( String name )
    {
        remove( name );
    }

    public void removeFile( String name )
    {
        remove( name );
    }

    private void remove( String name )
    {
        this.service.deleteResource( new FileResourceName( this.name, name ) );
    }
}
