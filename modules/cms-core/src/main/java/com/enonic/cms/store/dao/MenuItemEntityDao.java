/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemSpecification;
import org.hibernate.Query;

import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.page.PageSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateSpecification;

public class MenuItemEntityDao
    extends AbstractBaseEntityDao<MenuItemEntity>
    implements MenuItemDao
{
    public Collection<MenuItemEntity> findAll()
    {
        return findByNamedQuery( MenuItemEntity.class, "MenuItemEntity.findAll" );
    }

    public List<MenuItemEntity> findBySpecification( MenuItemSpecification spec )
    {
        String hql = buildHQL( spec );
        Query compiled = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( hql );
        compiled.setCacheable( true );

        return (List<MenuItemEntity>) compiled.list();
    }

    public MenuItemEntity findByKey( int menuItemKey )
    {
        return get( MenuItemEntity.class, menuItemKey );
    }

    public MenuItemEntity findByKey( MenuItemKey menuItemKey )
    {
        return get( MenuItemEntity.class, menuItemKey.toInt() );
    }

    public List<MenuItemEntity> findByKeys( Collection<MenuItemKey> menuItemKeys )
    {
        List<MenuItemEntity> menuItems = new ArrayList<MenuItemEntity>();
        for ( MenuItemKey menuItemKey : menuItemKeys )
        {
            MenuItemEntity menuItem = findByKey( menuItemKey );
            if ( menuItem != null )
            {
                menuItems.add( menuItem );
            }
        }
        return menuItems;
    }


    public Collection<MenuItemEntity> findBySiteKey( int siteKey )
    {
        return findByNamedQuery( MenuItemEntity.class, "MenuItemEntity.findBySiteKey", "siteKey", siteKey );
    }

    public Collection<MenuItemEntity> findTopMenuItems( int siteKey )
    {
        return findByNamedQuery( MenuItemEntity.class, "MenuItemEntity.findTopMenuItems", "siteKey", siteKey );
    }

    public MenuItemEntity findContentPage( int siteKey, int contentKey )
    {
        return findSingleByNamedQuery( MenuItemEntity.class, "MenuItemEntity.findContentPage", new String[]{"siteKey", "contentKey"},
                                       new Object[]{siteKey, contentKey} );
    }

    private String buildHQL( MenuItemSpecification spec )
    {
        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        hqlQuery.addFromTable( MenuItemEntity.class.getName(), "mei", SelectBuilder.NO_JOIN, null );

        if ( spec.getMenuItemName() != null )
        {
            hqlQuery.addFilter( "AND", "mei.name = '" + spec.getMenuItemName() + "'" );
        }
        if ( spec.getParentKey() != null )
        {
            hqlQuery.addFilter( "AND", "mei.parent = " + spec.getParentKey() );
        }
        if ( spec.getRootLevelOnly() != null && spec.getRootLevelOnly() )
        {
            hqlQuery.addFilter( "AND", "mei.parent is null" );
        }
        if ( spec.getType() != null )
        {
            hqlQuery.addFilter( "AND", "mei.menuItemType = " + spec.getType().getKey() );
        }
        if ( spec.getSiteKey() != null )
        {
            hqlQuery.addFilter( "AND", "mei.site.key = " + spec.getSiteKey() );
        }
        if ( spec.getMenuItemShortcut() != null )
        {
            hqlQuery.addFilter( "AND", "mei.menuItemShortcut = " + spec.getMenuItemShortcut().getKey() );
        }
        if ( spec.getPageSpecification() != null )
        {
            PageSpecification pageSpec = spec.getPageSpecification();
            if ( pageSpec.getTemplateSpecification() != null )
            {
                PageTemplateSpecification pageTemplateSpec = pageSpec.getTemplateSpecification();
                if ( pageTemplateSpec.getType() != null )
                {
                    hqlQuery.addFilter( "AND", "mei.page.template.type = " + pageTemplateSpec.getType().getKey() );
                }

                if ( pageTemplateSpec.getPageTemplateKey() != null )
                {
                    hqlQuery.addFilter( "AND", "mei.page.template.key = " + pageTemplateSpec.getPageTemplateKey().toInt() );
                }
            }
        }

        return hqlQuery.toString();
    }

    public EntityPageList<MenuItemEntity> findAll( int index, int count )
    {
        return findPageList( MenuItemEntity.class, null, index, count );
    }
}
