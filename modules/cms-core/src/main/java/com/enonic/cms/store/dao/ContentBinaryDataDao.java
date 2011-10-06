/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;

public interface ContentBinaryDataDao
    extends EntityDao<ContentBinaryDataEntity>
{
    ContentBinaryDataEntity findByBinaryKey( Integer binaryKey );

    List<ContentBinaryDataEntity> findAllByBinaryKey( Integer binaryKey );
}
