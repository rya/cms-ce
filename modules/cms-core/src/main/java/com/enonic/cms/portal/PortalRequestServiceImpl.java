/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import javax.inject.Inject;

import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.portal.processor.PageRequestProcessorFactory;
import com.enonic.cms.portal.rendering.PageRendererFactory;
import com.enonic.cms.portal.rendering.WindowRendererFactory;

public final class PortalRequestServiceImpl
    implements PortalRequestService
{
    @Inject
    private SiteDao siteDao;

    @Inject
    private PortletDao portletDao;

    @Inject
    private UserDao userDao;

    @Inject
    private ContentDao contentDao;

    @Inject
    private LanguageDao languageDao;

    @Inject
    private PageRendererFactory pageRendererFactory;

    @Inject
    private WindowRendererFactory windowRendererFactory;

    @Inject
    private DataSourceService dataSourceService;

    @Inject
    private PortalAccessService portalAccessService;

    @Inject
    private PageRequestProcessorFactory pageRequestProcessorFactory;

    @Inject
    private LivePortalTraceService liveTraceService;

    public PortalResponse processRequest( final PortalRequest request )
    {
        PortalRequestProcessor portalRequestProcessor = new PortalRequestProcessor( request );
        portalRequestProcessor.setContentDao( contentDao );
        portalRequestProcessor.setDataSourceService( dataSourceService );
        portalRequestProcessor.setLanguageDao( languageDao );
        portalRequestProcessor.setLiveTraceService( liveTraceService );
        portalRequestProcessor.setPageRendererFactory( pageRendererFactory );
        portalRequestProcessor.setPageRequestProcessorFactory( pageRequestProcessorFactory );
        portalRequestProcessor.setPortalAccessService( portalAccessService );
        portalRequestProcessor.setPortletDao( portletDao );
        portalRequestProcessor.setSiteDao( siteDao );
        portalRequestProcessor.setUserDao( userDao );
        portalRequestProcessor.setWindowRendererFactory( windowRendererFactory );

        return portalRequestProcessor.processRequest();
    }

    public void setPageRendererFactory( PageRendererFactory value )
    {
        this.pageRendererFactory = value;
    }

    public void setWindowRendererFactory( WindowRendererFactory windowRendererFactory )
    {
        this.windowRendererFactory = windowRendererFactory;
    }

    public void setDataSourceService( DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }

    public void setLanguageDao( LanguageDao languageDao )
    {
        this.languageDao = languageDao;
    }

    public void setPortalAccessService( PortalAccessService portalAccessService )
    {
        this.portalAccessService = portalAccessService;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setPortletDao( PortletDao portletDao )
    {
        this.portletDao = portletDao;
    }

    public void setPageRequestProcessorFactory( PageRequestProcessorFactory value )
    {
        this.pageRequestProcessorFactory = value;
    }

    public void setSiteDao( SiteDao siteDao )
    {
        this.siteDao = siteDao;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setLivePortalTraceService( LivePortalTraceService liveTraceService )
    {
        this.liveTraceService = liveTraceService;
    }
}
