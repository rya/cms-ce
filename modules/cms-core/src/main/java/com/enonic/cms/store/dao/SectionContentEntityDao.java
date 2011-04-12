/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

public class SectionContentEntityDao
    extends AbstractBaseEntityDao<SectionContentEntity>
    implements SectionContentDao
{

    public SectionContentEntity findByKey( SectionContentKey key )
    {
        return get( SectionContentEntity.class, key.toInt() );
    }

    public SectionContentEntity findByMenuItemAndContentKeys( MenuItemKey menuItemKey, ContentKey contentKey )
    {
        String[] names = new String[]{"menuItemKey", "contentKey"};
        Object[] values = new Object[]{menuItemKey.toInt(), contentKey.toInt()};
        return findSingleByNamedQuery( SectionContentEntity.class, "SectionContentEntity.findByMenuItemAndContentKeys", names, values );
    }

    public int deleteByContentKey( ContentKey key )
    {
        return deleteByNamedQuery( "SectionContentEntity.deleteByContentKey", "contentKey", key.toInt() );
    }

    public Integer getCountNamedContentsInSection( MenuItemKey menuItemKey, String contentName )
    {
        String[] names = new String[]{"menuItemKey", "contentName"};
        Object[] values = new Object[]{menuItemKey.toInt(), contentName.toLowerCase()};

        Long count = findSingleByNamedQuery( Long.class, "SectionContentEntity.findNamedContentInSection", names, values );

        return new Integer( count.intValue() );
    }

}

