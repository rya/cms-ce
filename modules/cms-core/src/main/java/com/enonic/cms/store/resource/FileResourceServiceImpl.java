/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.resource.FileResourceData;
import com.enonic.cms.core.resource.FileResourceName;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.BlobStoreObject;
import com.enonic.cms.framework.util.MimeTypeResolver;

import com.enonic.cms.store.support.EntityChangeListener;
import com.enonic.cms.store.support.EntityChangeListenerHub;
import com.enonic.cms.store.vfs.db.VirtualFileEntity;

public final class FileResourceServiceImpl
    implements FileResourceService, EntityChangeListener, BeanPostProcessor
{
    private BlobStore blobStore;

    private SessionFactory sessionFactory;

    private final ArrayList<FileResourceListener> listeners;

    public FileResourceServiceImpl()
    {
        this.listeners = new ArrayList<FileResourceListener>();
        EntityChangeListenerHub.getInstance().addListener( this );
    }

    public void setBlobStore( BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private Session openSession()
    {
        return this.sessionFactory.getCurrentSession();
    }

    private FileResource newResource( FileResourceName name, VirtualFileEntity entity )
    {
        FileResource res = new FileResource( name );
        res.setFolder( entity.isFolder() );
        res.setSize( entity.getLength() );
        res.setBlobKey( entity.getBlobKey() );
        res.setMimeType( MimeTypeResolver.getInstance().getMimeType( name.getName() ) );
        res.setLastModified( new DateTime( entity.getLastModified() ) );
        return res;
    }

    public FileResource getResource( FileResourceName name )
    {
        Session session = openSession();
        return doGetResource( session, name );
    }

    private FileResource doGetResource( Session session, FileResourceName name )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );
        if ( entity == null )
        {
            if ( name.isRoot() )
            {
                doCreateFolder( session, name );
                entity = findEntity( session, key );
            }
        }

        if ( entity != null )
        {
            return newResource( name, entity );
        }

        return null;
    }

    private boolean doCreateFolder( Session session, FileResourceName name )
    {
        if ( name == null )
        {
            return false;
        }

        doCreateFolder( session, name.getParent() );

        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );
        if ( entity != null )
        {
            return false;
        }

        entity = new VirtualFileEntity();
        entity.setKey( key );
        entity.setBlobKey( null );
        entity.setParentKey( createKey( name.getParent() ) );
        entity.setLength( -1 );
        entity.setName( name.getName() );
        entity.setLastModified( System.currentTimeMillis() );
        session.saveOrUpdate( entity );
        return true;
    }

    public boolean createFolder( FileResourceName name )
    {
        Session session = openSession();
        return doCreateFolder( session, name );
    }

    public boolean createFile( FileResourceName name, FileResourceData data )
    {
        Session session = openSession();
        return !name.isRoot() && doCreateFile( session, name, data );
    }

    private boolean doCreateFile( Session session, FileResourceName name, FileResourceData data )
    {
        String key = createKey( name );
        if ( findEntity( session, key ) != null )
        {
            return false;
        }

        doCreateFolder( session, name.getParent() );
        VirtualFileEntity entity = new VirtualFileEntity();
        entity.setKey( key );
        entity.setBlobKey( null );
        entity.setParentKey( createKey( name.getParent() ) );
        entity.setLength( 0 );
        entity.setName( name.getName() );
        entity.setLastModified( System.currentTimeMillis() );
        setBlob( entity, data != null ? data.getAsBytes() : new byte[0] );
        session.saveOrUpdate( entity );
        return true;
    }

    public boolean deleteResource( FileResourceName name )
    {
        Session session = openSession();
        return doDeleteResource( session, name );
    }

    private boolean doDeleteResource( Session session, FileResourceName name )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );
        return doDeleteResource( session, entity );
    }

    private boolean doDeleteResource( Session session, VirtualFileEntity entity )
    {
        if ( entity == null )
        {
            return false;
        }

        for ( VirtualFileEntity child : findChildren( session, entity.getKey() ) )
        {
            doDeleteResource( session, child );
        }

        session.delete( entity );
        return true;
    }

    public List<FileResourceName> getChildren( FileResourceName name )
    {
        Session session = openSession();
        return doGetChildren( session, name );
    }

    private List<FileResourceName> doGetChildren( Session session, FileResourceName name )
    {
        ArrayList<FileResourceName> list = new ArrayList<FileResourceName>();

        String key = createKey( name );
        for ( VirtualFileEntity entity : findChildren( session, key ) )
        {
            list.add( new FileResourceName( name, entity.getName() ) );
        }

        return list;
    }

    public FileResourceData getResourceData( FileResourceName name )
    {
        Session session = openSession();
        return doGetResourceData( session, name );
    }

    private FileResourceData doGetResourceData( Session session, FileResourceName name )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );

        if ( entity == null )
        {
            return null;
        }

        if ( entity.isFolder() )
        {
            return null;
        }

        final byte[] bytes = getBlob( entity );
        if (bytes == null) {
            throw new IllegalStateException("Blob for resource [" + name.toString() +
                "] is not found. Please check your blobstore configuration.");
        }

        final FileResourceData data = new FileResourceData();
        data.setAsBytes( bytes );
        return data;
    }

    public boolean setResourceData( FileResourceName name, FileResourceData data )
    {
        Session session = openSession();
        return doSetResourceData( session, name, data );
    }

    private boolean doSetResourceData( Session session, FileResourceName name, FileResourceData data )
    {
        String key = createKey( name );
        VirtualFileEntity entity = findEntity( session, key );

        if ( entity == null )
        {
            return false;
        }

        if ( entity.isFolder() )
        {
            return false;
        }

        setBlob( entity, data.getAsBytes() );
        return true;
    }

    private void setBlob( VirtualFileEntity entity, byte[] data )
    {
        BlobStoreObject blob = new BlobStoreObject( data );
        this.blobStore.put( blob );
        entity.setBlobKey( blob.getId() );
        entity.setLength( blob.getSize() );
        entity.setLastModified( System.currentTimeMillis() );
    }

    private byte[] getBlob( VirtualFileEntity entity )
    {
        String key = entity.getBlobKey();
        if ( key == null )
        {
            return null;
        }

        BlobRecord blob = this.blobStore.getRecord( new BlobKey( key ) );
        return blob != null ? blob.getAsBytes() : null;
    }

    private String createKey( FileResourceName name )
    {
        if ( name == null )
        {
            return null;
        }

        try
        {
            return DigestUtils.shaHex( name.getPath().getBytes( "UTF-8" ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    private VirtualFileEntity findEntity( Session session, String key )
    {
        return (VirtualFileEntity) session.get( VirtualFileEntity.class, key );
    }

    @SuppressWarnings("unchecked")
    private List<VirtualFileEntity> findChildren( Session session, String key )
    {
        Query query = session.getNamedQuery( "VirtualFileEntity.findChildren" );
        query.setParameter( "parentKey", key );
        return query.list();
    }

    public boolean moveResource( FileResourceName from, FileResourceName to )
    {
        Session session = openSession();
        return doMoveResource( session, from, to );
    }

    private boolean doMoveResource( Session session, FileResourceName from, FileResourceName to )
    {
        if ( doCopyResource( session, from, to ) )
        {
            doDeleteResource( session, from );
            return true;
        }

        return false;
    }

    public boolean copyResource( FileResourceName from, FileResourceName to )
    {
        Session session = openSession();
        return doCopyResource( session, from, to );
    }

    private boolean doCopyResource( Session session, FileResourceName from, FileResourceName to )
    {
        String fromKey = createKey( from );
        VirtualFileEntity fromEntity = findEntity( session, fromKey );
        return doCopyResource( session, fromEntity, to );
    }

    private boolean doCopyResource( Session session, VirtualFileEntity from, FileResourceName to )
    {
        if ( from == null )
        {
            return false;
        }

        if ( from.isFile() )
        {
            return doCopyResourceFile( session, from, to );
        }
        else
        {
            return doCopyResourceFolder( session, from, to );
        }
    }

    private boolean doCopyResourceFile( Session session, VirtualFileEntity from, FileResourceName to )
    {
        String toKey = createKey( to );
        VirtualFileEntity toEntity = findEntity( session, toKey );

        if ( toEntity != null )
        {
            return false;
        }

        doCreateFolder( session, to.getParent() );
        toEntity = createNewEntity( from, to );
        session.saveOrUpdate( toEntity );
        return true;
    }

    private boolean doCopyResourceFolder( Session session, VirtualFileEntity from, FileResourceName to )
    {
        if ( moveToSubfolderOfSelf( from, to ) )
        {
            return false;
        }

        String toKey = createKey( to );
        VirtualFileEntity toEntity = findEntity( session, toKey );

        if ( toEntity != null )
        {
            return false;
        }

        doCreateFolder( session, to );
        for ( VirtualFileEntity child : findChildren( session, from.getKey() ) )
        {
            doCopyResource( session, child, new FileResourceName( to, child.getName() ) );
        }

        return true;
    }


    private boolean moveToSubfolderOfSelf( VirtualFileEntity parent, FileResourceName potentialChild )
    {
        FileResourceName parentFileResource = new FileResourceName( parent.getName() );

        if ( getAllParents( potentialChild ).contains( parentFileResource ) )
        {
            return true;
        }

        return false;
    }

    private List<FileResourceName> getAllParents( FileResourceName fileResourceName )
    {
        List<FileResourceName> allParents = new ArrayList<FileResourceName>();

        FileResourceName currParent = fileResourceName.getParent();

        while ( currParent != null )
        {
            allParents.add( currParent );
            currParent = currParent.getParent();
        }

        return allParents;
    }

    private VirtualFileEntity createNewEntity( VirtualFileEntity oldEntity, FileResourceName newName )
    {
        VirtualFileEntity entity = new VirtualFileEntity();
        entity.setKey( createKey( newName ) );
        entity.setParentKey( createKey( newName.getParent() ) );
        entity.setLastModified( System.currentTimeMillis() );
        entity.setLength( oldEntity.getLength() );
        entity.setBlobKey( oldEntity.getBlobKey() );
        entity.setName( newName.getName() );
        return entity;
    }

    private FileResourceName createNameFromEntity( Session session, VirtualFileEntity entity )
    {
        if ( entity.getParentKey() == null )
        {
            return new FileResourceName( "/" );
        }

        final VirtualFileEntity parent = (VirtualFileEntity) session.get( VirtualFileEntity.class, entity.getParentKey() );
        if ( parent == null )
        {
            return new FileResourceName( entity.getName() );
        }
        else
        {
            return new FileResourceName( createNameFromEntity( session, parent ), entity.getName() );
        }
    }

    private void publishResourceEvent( Session session, VirtualFileEntity entity, FileResourceEvent.Type type )
    {
        if ( this.listeners.isEmpty() )
        {
            return;
        }

        final FileResourceName name = createNameFromEntity( session, entity );
        final FileResourceEvent event = new FileResourceEvent( type, name );

        for ( FileResourceListener listener : this.listeners )
        {
            listener.resourceChanged( event );
        }
    }

    public void entityInserted( Session session, Object entity )
    {
        if ( entity instanceof VirtualFileEntity )
        {
            publishResourceEvent( session, (VirtualFileEntity) entity, FileResourceEvent.Type.ADDED );
        }
    }

    public void entityUpdated( Session session, Object entity )
    {
        if ( entity instanceof VirtualFileEntity )
        {
            publishResourceEvent( session, (VirtualFileEntity) entity, FileResourceEvent.Type.UPDATED );
        }
    }

    public void entityDeleted( Session session, Object entity )
    {
        if ( entity instanceof VirtualFileEntity )
        {
            publishResourceEvent( session, (VirtualFileEntity) entity, FileResourceEvent.Type.DELETED );
        }
    }

    public Object postProcessBeforeInitialization( Object bean, String name )
    {
        return bean;
    }

    public Object postProcessAfterInitialization( Object bean, String name )
    {
        if ( bean instanceof FileResourceListener )
        {
            this.listeners.add( (FileResourceListener) bean );
        }

        return bean;
    }
}
