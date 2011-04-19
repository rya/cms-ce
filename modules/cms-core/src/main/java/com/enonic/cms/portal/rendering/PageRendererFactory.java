/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

import javax.inject.Inject;

import com.enonic.cms.core.VerticalProperties;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.TightestCacheSettingsResolver;
import com.enonic.cms.core.preferences.PreferenceService;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.portal.cache.SiteCachesService;
import com.enonic.cms.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.rendering.viewtransformer.PageTemplateXsltViewTransformer;

public class PageRendererFactory
{
    @Inject
    @Qualifier("siteCachesService")
    private SiteCachesService siteCachesService;

    @Inject
    private PreferenceService preferenceService;

    @Inject
    private SecurityService securityService;

    @Inject
    private DatasourceExecutorFactory datasourceExecutorFactory;

    @Inject
    private ResourceService resourceService;

    @Inject
    private DataSourceService dataSourceService;

    @Inject
    private PageTemplateXsltViewTransformer pageTemplateXsltViewTransformer;

    @Inject
    private SitePropertiesService sitePropertiesService;

    @Inject
    private SiteURLResolver siteURLResolver;

    @Inject
    private VerticalProperties verticalProperties;

    @Inject
    private TightestCacheSettingsResolver tightestCacheSettingsResolver;

    @Inject
    private TimeService timeService;

    @Inject
    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    @Inject
    private LivePortalTraceService livePortalTraceService;

    public PageRenderer createPageRenderer( PageRendererContext pageRendererContext )
    {
        PageRenderer pageRenderer = new PageRenderer( pageRendererContext, dataSourceService );

        pageRenderer.setDataSourceExecutorFactory( datasourceExecutorFactory );
        pageRenderer.setPageTemplateXsltViewTransformer( pageTemplateXsltViewTransformer );
        pageRenderer.setResourceService( resourceService );
        pageRenderer.setPageCacheService( siteCachesService.getPageCacheService( pageRendererContext.getSite().getKey() ) );
        pageRenderer.setVerticalProperties( verticalProperties );
        pageRenderer.setSiteURLResolver( siteURLResolver );
        pageRenderer.setSitePropertiesService( sitePropertiesService );
        pageRenderer.setTightestCacheSettingsResolver( tightestCacheSettingsResolver );
        pageRenderer.setTimeService( timeService );
        pageRenderer.setPostProcessInstructionExecutor( postProcessInstructionExecutor );
        pageRenderer.setLivePortalTraceService( livePortalTraceService );

        return pageRenderer;
    }
}
