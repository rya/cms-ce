/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.store.vfs.db.VirtualFileEntity;

import com.enonic.cms.core.EntityPageList;

public interface VirtualFileDao
{
    public List<VirtualFileEntity> findAll();

    public VirtualFileEntity findByKey( final String parentKey );

    EntityPageList<VirtualFileEntity> findAll( int index, int count );
}

