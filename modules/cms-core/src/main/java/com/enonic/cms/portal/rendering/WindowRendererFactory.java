/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import javax.inject.Inject;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.portal.cache.PageCacheService;
import com.enonic.cms.portal.cache.SiteCachesService;
import com.enonic.cms.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.rendering.viewtransformer.PortletXsltViewTransformer;

/**
 * Apr 20, 2009
 */
public class WindowRendererFactory
{
    @Inject
    private SiteCachesService siteCachesService;

    @Inject
    private SecurityService securityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private DatasourceExecutorFactory datasourceExecutorFactory;

    @Inject
    private PortletXsltViewTransformer portletXsltViewTransformer;

    @Inject
    private VerticalProperties verticalProperties;

    @Inject
    private SitePropertiesService sitePropertiesService;

    @Inject
    private SiteURLResolver siteURLResolver;

    @Inject
    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    @Inject
    private LivePortalTraceService livePortalTraceService;

    public WindowRenderer createPortletRenderer( WindowRendererContext windowRendererContext )
    {
        PageCacheService pageCacheService = siteCachesService.getPageCacheService( windowRendererContext.getSite().getKey() );

        WindowRenderer windowRenderer = new WindowRenderer( windowRendererContext );

        windowRenderer.setDataSourceExecutorFactory( datasourceExecutorFactory );
        windowRenderer.setPageCacheService( pageCacheService );
        windowRenderer.setPortletXsltViewTransformer( portletXsltViewTransformer );
        windowRenderer.setResourceService( resourceService );
        windowRenderer.setSiteURLResolver( siteURLResolver );
        windowRenderer.setSitePropertiesService( sitePropertiesService );
        windowRenderer.setVerticalProperties( verticalProperties );
        windowRenderer.setPostProcessInstructionExecutor( postProcessInstructionExecutor );
        windowRenderer.setLiveTraceService( livePortalTraceService );

        return windowRenderer;
    }

}
