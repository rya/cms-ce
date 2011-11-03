/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.store.support.EntityPageList;
import com.enonic.cms.core.content.UnitEntity;

public interface UnitDao
    extends EntityDao<UnitEntity>
{
    UnitEntity findByKey( Integer key );

    List<UnitEntity> getAll();

    EntityPageList<UnitEntity> findAll( int index, int count );
}
