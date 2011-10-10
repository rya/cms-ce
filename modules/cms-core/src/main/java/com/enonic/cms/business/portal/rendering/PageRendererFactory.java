/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.rendering;

import com.enonic.cms.core.plugin.ExtensionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.service.DataSourceService;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SiteURLResolver;
import com.enonic.cms.business.TightestCacheSettingsResolver;
import com.enonic.cms.core.preference.PreferenceService;
import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.business.portal.cache.SiteCachesService;
import com.enonic.cms.business.portal.datasource.DatasourceExecutorFactory;
import com.enonic.cms.business.portal.instruction.PostProcessInstructionExecutor;
import com.enonic.cms.business.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.business.portal.rendering.viewtransformer.PageTemplateXsltViewTransformer;

public class PageRendererFactory
{
    @Autowired
    @Qualifier("siteCachesService")
    private SiteCachesService siteCachesService;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DatasourceExecutorFactory datasourceExecutorFactory;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PageTemplateXsltViewTransformer pageTemplateXsltViewTransformer;

    @Autowired
    private SitePropertiesService sitePropertiesService;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Autowired
    private VerticalProperties verticalProperties;

    @Autowired
    private TightestCacheSettingsResolver tightestCacheSettingsResolver;

    @Autowired
    private TimeService timeService;

    @Autowired
    private PostProcessInstructionExecutor postProcessInstructionExecutor;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ExtensionManager extensionManager;

    public PageRenderer createPageRenderer( PageRendererContext pageRendererContext )
    {
        PageRenderer pageRenderer = new PageRenderer( pageRendererContext, livePortalTraceService );

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
        pageRenderer.setDataSourceService( dataSourceService );
        pageRenderer.setExtensionManager( extensionManager );

        return pageRenderer;
    }
}
