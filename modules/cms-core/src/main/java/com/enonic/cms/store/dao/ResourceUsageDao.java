/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.resource.ResourceReferencer;
import com.enonic.cms.core.resource.ResourceReferencerType;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.portlet.PortletEntity;

public class ResourceUsageDao
{
    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private PortletDao contentObjectDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private ContentTypeEntityDao contentTypeDao;

    public HashMap<ResourceKey, Long> getUsageCountMap()
    {

        List allUsageCounts = getAllUsageCounts();
        HashMap<ResourceKey, Long> usageCountMap = new HashMap<ResourceKey, Long>();

        for ( int i = 0; i < allUsageCounts.size(); i++ )
        {
            ResourceKey resourceKey = (ResourceKey) ( (Object[]) allUsageCounts.get( i ) )[0];
            Long count = (Long) ( (Object[]) allUsageCounts.get( i ) )[1];

            if ( usageCountMap.containsKey( resourceKey ) )
            {
                count += usageCountMap.get( resourceKey );
            }
            usageCountMap.put( resourceKey, count );
        }
        return usageCountMap;
    }

    private List getAllUsageCounts()
    {
        List allUsageCounts = new ArrayList();
        allUsageCounts.addAll( contentObjectDao.getResourceUsageCountStyle() );
        allUsageCounts.addAll( contentObjectDao.getResourceUsageCountBorder() );
        allUsageCounts.addAll( contentTypeDao.getResourceUsageCountCSS() );
        allUsageCounts.addAll( pageTemplateDao.getResourceUsageCountStyle() );
        allUsageCounts.addAll( pageTemplateDao.getResourceUsageCountCSS() );
        allUsageCounts.addAll( siteDao.getResourceUsageCountDefaultCSS() );
        return allUsageCounts;
    }

    public Multimap<ResourceKey, ResourceReferencer> getUsedBy( ResourceKey resourceKey )
    {

        Multimap usedBy = HashMultimap.create();

        for ( PortletEntity obj : contentObjectDao.findByStyle( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.CONTENT_OBJECT_STYLE ) );
        }
        for ( PortletEntity obj : contentObjectDao.findByBorder( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.CONTENT_OBJECT_BORDER ) );
        }
        for ( ContentTypeEntity obj : contentTypeDao.findByCSS( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.CONTENT_TYPE_CSS ) );
        }
        for ( PageTemplateEntity obj : pageTemplateDao.findByStyle( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.PAGE_TEMPLATE_STYLE ) );
        }
        for ( PageTemplateEntity obj : pageTemplateDao.findByCSS( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.PAGE_TEMPLATE_CSS ) );
        }
        for ( SiteEntity obj : siteDao.findByDefaultCss( resourceKey ) )
        {
            usedBy.put( resourceKey, new ResourceReferencer( obj, ResourceReferencerType.SITE_DEFAULT_CSS ) );
        }
        return usedBy;
    }

    public void updateResoruceReference( ResourceKey oldResourceKey, ResourceKey newResourceKey )
    {
        contentObjectDao.updateResourceStyleReference( oldResourceKey, newResourceKey );
        contentObjectDao.updateResourceBorderReference( oldResourceKey, newResourceKey );
        contentTypeDao.updateResourceCSSReference( oldResourceKey, newResourceKey );
        pageTemplateDao.updateResourceStyleReference( oldResourceKey, newResourceKey );
        pageTemplateDao.updateResourceCSSReference( oldResourceKey, newResourceKey );
        siteDao.updateResourceCSSReference( oldResourceKey, newResourceKey );
    }

    public void updateResoruceReferencePrefix( String oldPrefix, String newPrefix )
    {
        contentObjectDao.updateResourceStyleReferencePrefix( oldPrefix, newPrefix );
        contentObjectDao.updateResourceBorderReferencePrefix( oldPrefix, newPrefix );
        contentTypeDao.updateResourceCSSReferencePrefix( oldPrefix, newPrefix );
        pageTemplateDao.updateResourceStyleReferencePrefix( oldPrefix, newPrefix );
        pageTemplateDao.updateResourceCSSReferencePrefix( oldPrefix, newPrefix );
        siteDao.updateResourceCSSReferencePrefix( oldPrefix, newPrefix );
    }
}
