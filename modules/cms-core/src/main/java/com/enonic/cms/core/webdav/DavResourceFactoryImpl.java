/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceName;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavMethods;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;

import com.enonic.cms.store.resource.FileResourceService;

/**
 * This class implemnts the resource factory.
 */
public final class DavResourceFactoryImpl
    implements DavResourceFactory
{
    private final FileResourceService resourceService;

    private final LockManager lockManager;


    /**
     * Construct the factory.
     */
    public DavResourceFactoryImpl( FileResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.lockManager = new SimpleLockManager();
    }

    public FileResourceService getFileResourceService()
    {
        return this.resourceService;
    }

    /**
     * {@inheritDoc}
     */
    public DavResource createResource( DavResourceLocator locator, DavSession session )
        throws DavException
    {
        FileResource fileObject = getFileObject( locator );
        DavResourceImpl resource = new DavResourceImpl( locator, this, session, fileObject );
        return setup( resource );
    }

    /**
     * {@inheritDoc}
     */
    public DavResource createResource( DavResourceLocator locator, DavServletRequest request, DavServletResponse response )
        throws DavException
    {
        FileResource fileObject = getFileObject( locator );
        DavResourceImpl resource;

        if ( fileObject == null )
        {
            boolean isCollection = DavMethods.isCreateCollectionRequest( request );
            resource = new DavResourceImpl( locator, this, request.getDavSession(), isCollection );
        }
        else
        {
            resource = new DavResourceImpl( locator, this, request.getDavSession(), fileObject );
        }

        return setup( resource );
    }

    private DavResource setup( DavResource resource )
    {
        resource.addLockManager( this.lockManager );
        return resource;
    }

    /**
     * Return the file.
     */
    private FileResource getFileObject( DavResourceLocator locator )
    {
        final FileResourceName name = new FileResourceName( locator.getRepositoryPath() );
        return this.resourceService.getResource( name );
    }
}
