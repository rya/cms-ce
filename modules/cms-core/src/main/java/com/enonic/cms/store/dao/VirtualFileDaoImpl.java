/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.cms.framework.blob.BlobStore;

import com.enonic.cms.store.vfs.db.VirtualFileEntity;

import com.enonic.cms.domain.EntityPageList;
import org.springframework.stereotype.Repository;

@Repository("virtualFileDao")
public final class VirtualFileDaoImpl
    extends AbstractBaseEntityDao<VirtualFileEntity>
    implements VirtualFileDao
{
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
