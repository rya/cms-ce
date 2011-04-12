/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.domain.EntityPageList;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public class PortletEntityDao
    extends AbstractBaseEntityDao<PortletEntity>
    implements PortletDao
{

    public PortletEntity findByKey( int key )
    {
        return get( PortletEntity.class, key );
    }

    public PortletEntity findBySiteKeyAndNameIgnoreCase( SiteKey siteKey, String name )
    {
        if ( name == null )
        {
            throw new IllegalArgumentException( "Given name cannot be null" );
        }
        return findSingleByNamedQuery( PortletEntity.class, "PortletEntity.findBySiteKeyAndNameIgnoreCase", new String[]{"siteKey", "name"},
                                       new Object[]{siteKey, name.toLowerCase()} );
    }

    public List getResourceUsageCountStyle()
    {
        return getHibernateTemplate().findByNamedQuery( "PortletEntity.getResourceUsageCountStyle" );
    }

    public List getResourceUsageCountBorder()
    {
        return getHibernateTemplate().findByNamedQuery( "PortletEntity.getResourceUsageCountBorder" );
    }

    public List<PortletEntity> findByStyle( ResourceKey resourceKey )
    {
        return findByNamedQuery( PortletEntity.class, "PortletEntity.findByStyle", "styleKey", resourceKey );
    }

    public List<PortletEntity> findByBorder( ResourceKey resourceKey )
    {
        return findByNamedQuery( PortletEntity.class, "PortletEntity.findByBorder", "borderKey", resourceKey );
    }

    private List<PortletEntity> findByStylePrefix( String prefix )
    {
        return findByNamedQuery( PortletEntity.class, "PortletEntity.findByStylePrefix", "styleKeyPrefix", prefix );
    }

    private List<PortletEntity> findByBorderPrefix( String prefix )
    {
        return findByNamedQuery( PortletEntity.class, "PortletEntity.findByBorderPrefix", "borderKeyPrefix", prefix );
    }

    public void updateResourceStyleReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        List<PortletEntity> entityList = findByStyle( oldResourceKey );

        for ( PortletEntity entity : entityList )
        {
            entity.setStyleKey( newResourceKey );
        }
    }

    public void updateResourceBorderReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        List<PortletEntity> entityList = findByBorder( oldResourceKey );

        for ( PortletEntity entity : entityList )
        {
            entity.setBorderKey( newResourceKey );
        }
    }

    public void updateResourceStyleReferencePrefix( String oldPrefix, String newPrefix )
    {
        List<PortletEntity> entityList = findByStylePrefix( oldPrefix + "%" );

        for ( PortletEntity entity : entityList )
        {
            String key = entity.getStyleKey().toString();
            key = key.replace( oldPrefix, newPrefix );
            entity.setStyleKey( new ResourceKey( key ) );
        }
    }

    public void updateResourceBorderReferencePrefix( String oldPrefix, String newPrefix )
    {
        List<PortletEntity> entityList = findByBorderPrefix( oldPrefix + "%" );

        for ( PortletEntity entity : entityList )
        {
            String key = entity.getBorderKey().toString();
            key = key.replace( oldPrefix, newPrefix );
            entity.setBorderKey( new ResourceKey( key ) );
        }
    }

    public List<PortletEntity> findAll()
    {
        return findByNamedQuery( PortletEntity.class, "PortletEntity.findAll" );
    }

    public EntityPageList<PortletEntity> findAll( int index, int count )
    {
        return findPageList( PortletEntity.class, null, index, count );
    }
}