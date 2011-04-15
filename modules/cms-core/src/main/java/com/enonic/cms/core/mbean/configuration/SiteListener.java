/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.MBeanExporter;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteEventListener;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.portal.cache.SiteCachesService;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.SiteKey;

public class SiteListener
        implements SiteEventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SiteListener.class );

    private String objectNamePrefix = null;

    public void setObjectNamePrefix( String objectNamePrefix )
    {
        this.objectNamePrefix = objectNamePrefix;
    }

    @Inject
    @Qualifier("siteService")
    private SiteService siteService;

    @Inject
    @Qualifier("mbeanExporter")
    private MBeanExporter mbeanExporter;

    @Inject
    @Qualifier("sitePropertiesService")
    private SitePropertiesService sitePropertiesService;

    @Inject
    private SiteDao siteDao;

    @Inject
    @Qualifier("siteCachesService")
    private SiteCachesService siteCachesService;

    @PostConstruct
    public void afterPropertiesSet()
            throws Exception
    {
        siteService.registerSiteEventListener( this );
    }

    public void destroy()
    {
        siteService.unregisterSiteEventListener( this );
    }

    public void onSiteRegistered( SiteKey siteKey )
    {
        try
        {
            SiteEntity site = siteDao.findByKey( siteKey.toInt() );
            mbeanExporter.getServer().registerMBean( createSite( site ), createObjectName( site ) );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to register mbean for site: " + siteKey, e );
        }
    }

    public void onSiteUnregistered( SiteKey siteKey )
    {
        try
        {
            SiteEntity site = siteDao.findByKey( siteKey.toInt() );
            mbeanExporter.getServer().unregisterMBean( createObjectName( site ) );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to unregister mbean for site: " + siteKey, e );
        }
    }

    private Site createSite( SiteEntity site )
    {
        Site s = new Site( site.getKey() );
        s.setSiteProperties( sitePropertiesService.getSiteProperties( site.getKey() ).getProperties() );

        s.setSiteUrl( site.getSiteURL() );
        s.setPageCacheEnabled( siteCachesService.getPageCacheService( site.getKey() ).isEnabled() );
        return s;
    }

    private ObjectName createObjectName( SiteEntity site )
            throws Exception
    {
        return new ObjectName( objectNamePrefix + site.getName() + " (" + site.getKey() + ")" );
    }
}
