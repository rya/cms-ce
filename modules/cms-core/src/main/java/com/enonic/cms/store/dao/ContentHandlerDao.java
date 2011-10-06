/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerKey;


public interface ContentHandlerDao
    extends EntityDao<ContentHandlerEntity>
{
    ContentHandlerEntity findByKey( ContentHandlerKey key );

    List<ContentHandlerEntity> findAll();
}