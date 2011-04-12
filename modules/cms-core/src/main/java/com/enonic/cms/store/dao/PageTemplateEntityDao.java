/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.domain.EntityPageList;

public class PageTemplateEntityDao
    extends AbstractBaseEntityDao<PageTemplateEntity>
    implements PageTemplateDao
{
    public PageTemplateEntity findByKey( int pageTemplateKey )
    {
        return get( PageTemplateEntity.class, pageTemplateKey );
    }

    /**
     * Find by content type.
     */
    @SuppressWarnings("unchecked")
    private Collection<PageTemplateEntity> findByContentType( int siteKey, int contentType )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findByContentType",
                                 new String[]{"siteKey", "contentTypeKey"}, new Object[]{siteKey, contentType} );
    }

    public PageTemplateEntity findFirstByContentType( int siteKey, int contentType, PageTemplateType pageTemplateType )
    {
        return findFirstByContentType( siteKey, contentType, new PageTemplateType[]{pageTemplateType} );
    }

    public PageTemplateEntity findFirstByContentType( int siteKey, int contentType, PageTemplateType[] legalPageTemplateTypes )
    {
        final Collection<PageTemplateEntity> pageTemplates = findByContentType( siteKey, contentType );
        for ( PageTemplateEntity pageTemplate : pageTemplates )
        {
            for ( PageTemplateType legalType : legalPageTemplateTypes )
            {
                if ( pageTemplate.getType().equals( legalType ) )
                {
                    return pageTemplate;
                }
            }
        }

        return null;
    }

    public List getResourceUsageCountStyle()
    {
        return getHibernateTemplate().findByNamedQuery( "PageTemplateEntity.getResourceUsageCountStyle" );
    }

    public List getResourceUsageCountCSS()
    {
        return getHibernateTemplate().findByNamedQuery( "PageTemplateEntity.getResourceUsageCountCSS" );
    }

    public List<PageTemplateEntity> findByStyle( ResourceKey resourceKey )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findByStyle", "styleKey", resourceKey );
    }

    public List<PageTemplateEntity> findByCSS( ResourceKey resourceKey )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findByCSS", "cssKey", resourceKey );
    }

    private List<PageTemplateEntity> findByStylePrefix( String prefix )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findByStylePrefix", "styleKeyPrefix", prefix );
    }

    private List<PageTemplateEntity> findByCSSPrefix( String prefix )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findByCSSPrefix", "cssKeyPrefix", prefix );
    }

    public Collection<PageTemplateEntity> findByContentObjectKeys( List<Integer> contentObjectKeys )
    {
        Set<PageTemplateEntity> pageTemplates = new HashSet<PageTemplateEntity>();

        List<PageTemplatePortletEntity> pageTemplateObjects =
            findByNamedQuery( PageTemplatePortletEntity.class, "PageTemplatePortletEntity.findByContentObjectKeys", "contentObjectKeys",
                              contentObjectKeys );

        for ( PageTemplatePortletEntity pageTemplateObject : pageTemplateObjects )
        {
            pageTemplates.add( pageTemplateObject.getPageTemplate() );
        }

        return pageTemplates;
    }

    public List<PageTemplateEntity> findByTypes( List<PageTemplateType> types )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findByTypes", "types", types );
    }

    public List<PageTemplateEntity> findBySiteKey( int key )
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findBySiteKey", "key", key );
    }

    public void updateResourceStyleReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        List<PageTemplateEntity> entityList = findByStyle( oldResourceKey );

        for ( PageTemplateEntity entity : entityList )
        {
            entity.setStyleKey( newResourceKey );
        }
    }

    public void updateResourceCSSReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        List<PageTemplateEntity> entityList = findByCSS( oldResourceKey );

        for ( PageTemplateEntity entity : entityList )
        {
            entity.setCssKey( newResourceKey );
        }
    }

    public void updateResourceStyleReferencePrefix( String oldPrefix, String newPrefix )
    {
        List<PageTemplateEntity> entityList = findByStylePrefix( oldPrefix + "%" );

        for ( PageTemplateEntity entity : entityList )
        {
            String key = entity.getStyleKey().toString();
            key = key.replace( oldPrefix, newPrefix );
            entity.setStyleKey( new ResourceKey( key ) );
        }
    }

    public void updateResourceCSSReferencePrefix( String oldPrefix, String newPrefix )
    {
        List<PageTemplateEntity> entityList = findByCSSPrefix( oldPrefix + "%" );

        for ( PageTemplateEntity entity : entityList )
        {
            String key = entity.getCssKey().toString();
            key = key.replace( oldPrefix, newPrefix );
            entity.setCssKey( new ResourceKey( key ) );
        }
    }

    public List<PageTemplateEntity> findAll()
    {
        return findByNamedQuery( PageTemplateEntity.class, "PageTemplateEntity.findAll" );
    }

    public EntityPageList<PageTemplateEntity> findAll( int index, int count )
    {
        return findPageList( PageTemplateEntity.class, null, index, count );
    }
}
