/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SupportedLock;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameIterator;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.api.Version;

import com.enonic.cms.domain.resource.FileResource;
import com.enonic.cms.domain.resource.FileResourceData;
import com.enonic.cms.domain.resource.FileResourceName;

/**
 * This class implements the resource.
 */
public final class DavResourceImpl
        implements DavResource
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( DavResourceImpl.class );

    /**
     * Session.
     */
    private DavSession session;

    /**
     * Resource factory.
     */
    private final DavResourceFactoryImpl factory;

    /**
     * Locator.
     */
    private DavResourceLocator locator;

    private FileResource file;

    /**
     * True if collection.
     */
    private boolean isCollection;

    /**
     * Properties.
     */
    private DavPropertySet properties = new DavPropertySet();

    private boolean propsInitialized = false;

    private LockManager lockManager;

    /**
     * Construct the resource.
     */
    public DavResourceImpl( DavResourceLocator locator, DavResourceFactoryImpl factory, DavSession session, boolean isCollection )
            throws DavException
    {
        this( locator, factory, session, null );
        this.isCollection = isCollection;
    }

    /**
     * Construct the resource.
     */
    public DavResourceImpl( DavResourceLocator locator, DavResourceFactoryImpl factory, DavSession session, FileResource file )
            throws DavException
    {
        this.session = session;
        this.factory = factory;
        this.locator = locator;

        if ( locator.getResourcePath() != null )
        {
            setFile( file );
        }
        else
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }
    }

    /**
     * Set the file object.
     */
    private void setFile( FileResource file )
            throws DavException
    {
        this.file = file;
        if ( this.file != null )
        {
            this.isCollection = this.file.isFolder();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getComplianceClass()
    {
        return "1, 2";
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedMethods()
    {
        return "OPTIONS, GET, HEAD, POST, TRACE, PROPFIND, PROPPATCH, MKCOL, COPY, PUT, DELETE, MOVE, LOCK, UNLOCK";
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return this.file != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCollection()
    {
        return this.isCollection;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayName()
    {
        String resPath = getResourcePath();
        return ( resPath != null ) ? Text.getName( resPath ) : resPath;
    }

    /**
     * {@inheritDoc}
     */
    public DavResourceLocator getLocator()
    {
        return this.locator;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourcePath()
    {
        return this.locator.getResourcePath();
    }

    /**
     * {@inheritDoc}
     */
    public String getHref()
    {
        return this.locator.getHref( isCollection() );
    }

    /**
     * {@inheritDoc}
     */
    public long getModificationTime()
    {
        return getLastModifiedTime();
    }

    /**
     * {@inheritDoc}
     */
    public void spool( OutputContext outputContext )
            throws IOException
    {
        if ( this.isCollection )
        {
            spoolCollection( outputContext );
        }
        else
        {
            spoolResource( outputContext );
        }
    }

    /**
     * Return true if root.
     */
    private boolean isRoot()
    {
        return this.locator.getRepositoryPath().equals( "/" );
    }

    /**
     * Spool collection.
     */
    private void spoolCollection( OutputContext context )
            throws IOException
    {
        context.setModificationTime( new Date().getTime() );
        context.setContentType( "text/html" );
        context.setETag( "" );

        if ( context.hasStream() )
        {
            PrintWriter writer = new PrintWriter( new OutputStreamWriter( context.getOutputStream(), "utf8" ) );
            writer.print( "<html><head><title>" );
            writer.print( getResourcePath() );
            writer.print( "</title></head>" );
            writer.print( "<body><h2>" );
            writer.print( "WebDav " + getResourcePath() );
            writer.print( "</h2><ul>" );

            if ( !isRoot() )
            {
                writer.print( "<li><a href=\"..\">..</a></li>" );
            }

            DavResourceIterator iter = getMembers();
            while ( iter.hasNext() )
            {
                DavResource child = iter.nextResource();
                String label = Text.getName( child.getResourcePath() );
                writer.print( "<li><a href=\"" );
                writer.print( child.getHref() );
                writer.print( "\">" );
                writer.print( label );
                writer.print( "</a></li>" );
            }

            writer.print( "</ul><hr size=\"1\"><em>Powered by " );
            writer.print( Version.getTitleAndVersion() );
            writer.print( "</em></body></html>" );
            writer.close();
        }
    }

    /**
     * Return the last modified time.
     */
    private long getLastModifiedTime()
    {
        return this.file.getLastModified().getMillis();
    }

    /**
     * Spool resource.
     */
    private void spoolResource( OutputContext context )
            throws IOException
    {
        final long length = this.file.getSize();
        final long modTime = this.file.getLastModified().getMillis();

        context.setContentLength( length );
        context.setModificationTime( modTime );
        context.setContentType( MimeTypeResolver.getInstance().getMimeType( this.file.getName().getName() ) );
        context.setETag( "\"" + length + "-" + modTime + "\"" );

        final FileResourceData data = this.factory.getFileResourceService().getResourceData( this.file.getName() );
        if ( ( data != null ) && context.hasStream() )
        {
            IOUtils.write( data.getAsBytes(), context.getOutputStream() );
        }
    }

    /**
     * {@inheritDoc}
     */
    public DavPropertyName[] getPropertyNames()
    {
        return getProperties().getPropertyNames();
    }

    /**
     * {@inheritDoc}
     */
    public DavProperty getProperty( DavPropertyName name )
    {
        return getProperties().get( name );
    }

    /**
     * {@inheritDoc}
     */
    public DavPropertySet getProperties()
    {
        initProperties();
        return this.properties;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperty( DavProperty property )
            throws DavException
    {
        alterProperty( property );
    }

    /**
     * {@inheritDoc}
     */
    public void removeProperty( DavPropertyName propertyName )
            throws DavException
    {
        alterProperty( propertyName );
    }

    /**
     * Alter property.
     */
    private void alterProperty( Object prop )
            throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        alterProperties( Arrays.asList( prop ) );
    }

    /**
     * {@inheritDoc}
     */
    public MultiStatusResponse alterProperties( DavPropertySet setProperties, DavPropertyNameSet removePropertyNames )
            throws DavException
    {
        List<Object> changeList = new ArrayList<Object>();
        if ( removePropertyNames != null )
        {
            DavPropertyNameIterator it = removePropertyNames.iterator();
            while ( it.hasNext() )
            {
                changeList.add( it.next() );
            }
        }

        if ( setProperties != null )
        {
            DavPropertyIterator it = setProperties.iterator();
            while ( it.hasNext() )
            {
                changeList.add( it.next() );
            }
        }

        return alterProperties( changeList );
    }

    /**
     * {@inheritDoc}
     */
    public MultiStatusResponse alterProperties( List changeList )
            throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        return new MultiStatusResponse( getHref(), null );
    }

    /**
     * {@inheritDoc}
     */
    public DavResource getCollection()
    {
        DavResource parent = null;
        if ( getResourcePath() != null && !getResourcePath().equals( "/" ) )
        {
            String parentPath = Text.getRelativeParent( getResourcePath(), 1 );
            if ( parentPath.equals( "" ) )
            {
                parentPath = "/";
            }

            DavResourceLocator parentloc =
                    this.locator.getFactory().createResourceLocator( this.locator.getPrefix(), this.locator.getWorkspacePath(),
                                                                     parentPath );

            try
            {
                parent = this.factory.createResource( parentloc, this.session );
            }
            catch ( DavException e )
            {
                LOG.error( "Failed to get collection", e );
            }
        }

        return parent;
    }

    /**
     * {@inheritDoc}
     */
    public void addMember( DavResource member, InputContext inputContext )
            throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_CONFLICT );
        }

        try
        {
            String memberName = Text.getName( member.getLocator().getRepositoryPath() );
            if ( member.isCollection() )
            {
                createCollection( memberName, inputContext );
            }
            else
            {
                createFile( memberName, inputContext );
            }
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to add member", e );
            throw new DavException( DavServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    /**
     * Create collection.
     */
    private void createCollection( String memberName, InputContext inputContext )
            throws IOException, DavException
    {
        if ( inputContext.hasStream() )
        {
            throw new DavException( DavServletResponse.SC_UNSUPPORTED_MEDIA_TYPE );
        }

        final FileResourceName name = new FileResourceName( this.file.getName(), memberName );
        this.factory.getFileResourceService().createFolder( name );
    }

    /**
     * Create file.
     */
    private void createFile( String memberName, InputContext inputContext )
            throws IOException
    {
        final FileResourceName name = new FileResourceName( this.file.getName(), memberName );
        FileResourceData data = FileResourceData.create( new byte[0] );

        if ( inputContext.hasStream() )
        {
            data = FileResourceData.create( IOUtils.toByteArray( inputContext.getInputStream() ) );
        }

        if ( this.factory.getFileResourceService().getResource( name ) != null )
        {
            this.factory.getFileResourceService().setResourceData( name, data );
        }
        else
        {
            this.factory.getFileResourceService().createFile( name, data );
        }
    }

    /**
     * {@inheritDoc}
     */
    public DavResourceIterator getMembers()
    {
        ArrayList<DavResource> list = new ArrayList<DavResource>();

        try
        {
            if ( exists() && isCollection() )
            {
                for ( DavResourceLocator child : getChildLocators() )
                {
                    list.add( this.factory.createResource( child, this.session ) );
                }
            }
        }
        catch ( DavException e )
        {
            LOG.error( "Failed to get members", e );
        }

        return new DavResourceIteratorImpl( list );
    }

    /**
     * Return member locators.
     */
    private List<DavResourceLocator> getChildLocators()
    {
        ArrayList<DavResourceLocator> list = new ArrayList<DavResourceLocator>();

        for ( FileResourceName name : this.factory.getFileResourceService().getChildren( this.file.getName() ) )
        {
            list.add( this.locator.getFactory().createResourceLocator( this.locator.getPrefix(), this.locator.getWorkspacePath(), name.getPath(),
                                                                       false ) );
        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public void removeMember( DavResource member )
            throws DavException
    {
        if ( !exists() || !member.exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        try
        {
            removeMember( member.getLocator().getRepositoryPath() );
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to remove member", e );
            throw new DavException( DavServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    /**
     * Remove member.
     */
    private void removeMember( String fileName )
            throws IOException
    {
        final FileResourceName name = new FileResourceName( fileName );
        this.factory.getFileResourceService().deleteResource( name );
    }

    /**
     * {@inheritDoc}
     */
    public void move( DavResource destination )
            throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        try
        {
            move( destination.getLocator().getRepositoryPath() );
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to move resource", e );
            throw new DavException( DavServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    /**
     * Move.
     */
    private void move( String fileName )
            throws IOException
    {
        final FileResourceName name = new FileResourceName( fileName );
        this.factory.getFileResourceService().moveResource( this.file.getName(), name );
    }

    /**
     * {@inheritDoc}
     */
    public void copy( DavResource destination, boolean shallow )
            throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        if ( !destination.getCollection().exists() )
        {
            throw new DavException( DavServletResponse.SC_CONFLICT );
        }

        try
        {
            copy( destination.getLocator().getRepositoryPath() );
        }
        catch ( IOException e )
        {
            LOG.error( "Failed to copy resource", e );
            throw new DavException( DavServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage() );
        }
    }

    /**
     * Copy.
     */
    private void copy( String fileName )
            throws IOException
    {
        final FileResourceName name = new FileResourceName( fileName );
        this.factory.getFileResourceService().copyResource( this.file.getName(), name );
    }

    public boolean isLockable( Type type, Scope scope )
    {
        return Type.WRITE.equals( type ) && Scope.EXCLUSIVE.equals( scope );
    }

    public boolean hasLock( Type type, Scope scope )
    {
        return getLock( type, scope ) != null;
    }

    public ActiveLock getLock( Type type, Scope scope )
    {
        ActiveLock lock = null;
        if ( exists() && Type.WRITE.equals( type ) && Scope.EXCLUSIVE.equals( scope ) )
        {
            lock = this.lockManager.getLock( type, scope, this );
        }
        return lock;
    }

    public ActiveLock[] getLocks()
    {
        ActiveLock writeLock = getLock( Type.WRITE, Scope.EXCLUSIVE );
        return ( writeLock != null ) ? new ActiveLock[]{writeLock} : new ActiveLock[0];
    }

    public ActiveLock lock( LockInfo lockInfo )
            throws DavException
    {
        if ( isLockable( lockInfo.getType(), lockInfo.getScope() ) )
        {
            return this.lockManager.createLock( lockInfo, this );
        }
        else
        {
            throw new DavException( DavServletResponse.SC_PRECONDITION_FAILED, "Unsupported lock type or scope." );
        }
    }

    public ActiveLock refreshLock( LockInfo lockInfo, String lockToken )
            throws DavException
    {
        if ( !exists() )
        {
            throw new DavException( DavServletResponse.SC_NOT_FOUND );
        }

        ActiveLock lock = getLock( lockInfo.getType(), lockInfo.getScope() );
        if ( lock == null )
        {
            throw new DavException( DavServletResponse.SC_PRECONDITION_FAILED,
                                    "No lock with the given type/scope present on resource " + getResourcePath() );
        }

        lock = this.lockManager.refreshLock( lockInfo, lockToken, this );
        return lock;
    }

    public void unlock( String lockToken )
            throws DavException
    {
        ActiveLock lock = getLock( Type.WRITE, Scope.EXCLUSIVE );
        if ( lock == null )
        {
            throw new DavException( HttpServletResponse.SC_PRECONDITION_FAILED );
        }
        else if ( lock.isLockedByToken( lockToken ) )
        {
            lockManager.releaseLock( lockToken, this );
        }
        else
        {
            throw new DavException( DavServletResponse.SC_LOCKED );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addLockManager( LockManager lockManager )
    {
        this.lockManager = lockManager;
    }

    /**
     * {@inheritDoc}
     */
    public DavResourceFactory getFactory()
    {
        return this.factory;
    }

    /**
     * {@inheritDoc}
     */
    public DavSession getSession()
    {
        return this.session;
    }

    /**
     * Fill the set of properties
     */
    private void initProperties()
    {
        if ( !exists() || this.propsInitialized )
        {
            return;
        }

        if ( getDisplayName() != null )
        {
            properties.add( new DefaultDavProperty( DavPropertyName.DISPLAYNAME, getDisplayName() ) );
        }

        if ( isCollection() )
        {
            properties.add( new ResourceType( ResourceType.COLLECTION ) );
            properties.add( new DefaultDavProperty( DavPropertyName.ISCOLLECTION, "1" ) );
        }
        else
        {
            properties.add( new ResourceType( ResourceType.DEFAULT_RESOURCE ) );
            properties.add( new DefaultDavProperty( DavPropertyName.ISCOLLECTION, "0" ) );
        }

        String contentType = MimeTypeResolver.getInstance().getMimeType( getResourcePath() );
        properties.add( new DefaultDavProperty( DavPropertyName.GETCONTENTTYPE, contentType ) );

        long modifiedTime = getLastModifiedTime();
        if ( modifiedTime != DavConstants.UNDEFINED_TIME )
        {
            properties.add( new DefaultDavProperty( DavPropertyName.GETLASTMODIFIED,
                                                    DavConstants.modificationDateFormat.format( new Date( modifiedTime ) ) ) );
        }

        if ( !isCollection() )
        {
            properties.add( new DefaultDavProperty( DavPropertyName.GETCONTENTLENGTH, getSize() + "" ) );
        }

        properties.add( new LockDiscovery( getLock( Type.WRITE, Scope.EXCLUSIVE ) ) );
        SupportedLock supportedLock = new SupportedLock();
        supportedLock.addEntry( Type.WRITE, Scope.EXCLUSIVE );
        properties.add( supportedLock );

        propsInitialized = true;
    }

    private long getSize()
    {
        return this.file.getSize();
    }
}
