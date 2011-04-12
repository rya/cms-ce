/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentBinaryDataEntityDao
    extends AbstractBaseEntityDao<ContentBinaryDataEntity>
    implements ContentBinaryDataDao
{

    @Autowired
    private BinaryDataDao binaryDataDao;


    public ContentBinaryDataEntity findByBinaryKey( Integer binaryKey )
    {
        return findFirstByNamedQuery( ContentBinaryDataEntity.class, "ContentBinaryDataEntity.findByBinaryKey", "key", binaryKey );
    }

    public List<ContentBinaryDataEntity> findAllByBinaryKey( Integer binaryKey )
    {
        return findByNamedQuery( ContentBinaryDataEntity.class, "ContentBinaryDataEntity.findByBinaryKey", "key", binaryKey );
    }

}
