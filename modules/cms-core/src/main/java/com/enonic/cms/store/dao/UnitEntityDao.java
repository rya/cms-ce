/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.domain.EntityPageList;

public class UnitEntityDao
    extends AbstractBaseEntityDao<UnitEntity>
    implements UnitDao
{
    public UnitEntity findByKey( Integer key )
    {
        UnitEntity unit = get( UnitEntity.class, key );

        if ( unit == null )
        {
            return null;
        }

        return unit;
    }

    public List<UnitEntity> getAll()
    {
        return findByNamedQuery( UnitEntity.class, "UnitEntity.getAll" );
    }

    public EntityPageList<UnitEntity> findAll( int index, int count )
    {
        return findPageList( UnitEntity.class, "x.deleted = 0", index, count );
    }
}