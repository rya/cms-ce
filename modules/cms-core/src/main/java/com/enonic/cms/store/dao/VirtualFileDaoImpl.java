/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.cms.framework.blob.BlobStore;

import com.enonic.cms.store.vfs.db.VirtualFileEntity;

import com.enonic.cms.domain.EntityPageList;

public class VirtualFileDaoImpl
    extends AbstractBaseEntityDao<VirtualFileEntity>
    implements VirtualFileDao
{
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    /**
     * Prefix.
     */
    private String scheme;

    /**
     * Hidden file list.
     */
    private String excludePattern;

    /**
     * Default folder list.
     */
    private String defaultFolderList;

    private boolean defaultFoldersCreated = false;

    @Autowired
    @Qualifier("blobStore")
    private BlobStore blobStore;

    @PostConstruct
    public void afterPropertiesSet()
        throws Exception
    {
    }

    @PreDestroy
    public void destroy()
    {
    }

    /**
     * Set the hidden files.
     */
    public void setExcludePattern( String excludePattern )
    {
        this.excludePattern = excludePattern;
    }

    /**
     * Set the scheme.
     */
    public void setScheme( String scheme )
    {
        this.scheme = scheme;
    }

    public void setDefaultFolderList( String defaultFolderList )
    {
        this.defaultFolderList = defaultFolderList;
    }

    public List<VirtualFileEntity> findAll()
    {
        return findByNamedQuery( VirtualFileEntity.class, "VirtualFileEntity.getAll" );
    }

    public VirtualFileEntity findByKey( String key )
    {
        return get( VirtualFileEntity.class, key );
    }

    public EntityPageList<VirtualFileEntity> findAll( int index, int count )
    {
        return findPageList( VirtualFileEntity.class, null, index, count );
    }
}
