/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerKey;

public class ContentHandlerEntityDao
    extends AbstractBaseEntityDao<ContentHandlerEntity>
    implements ContentHandlerDao
{
    public ContentHandlerEntity findByKey( ContentHandlerKey key )
    {
        return get( ContentHandlerEntity.class, key );
    }

    public List<ContentHandlerEntity> findAll()
    {
        return findByNamedQuery( ContentHandlerEntity.class, "ContentHandlerEntity.findAll" );
    }
}