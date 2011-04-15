/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.processor;

import javax.inject.Inject;

import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.portal.PageRequestType;
import com.enonic.cms.portal.PortalAccessService;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.SectionContentDao;

/**
 * Sep 29, 2009
 */
public class PageRequestProcessorFactory
{
    @Inject
    private ContentDao contentDao;

    @Inject
    private SectionContentDao sectionContentDao;

    @Inject
    private PageTemplateDao pageTemplateDao;

    @Inject
    private PortalAccessService portalAccessService;

    @Inject
    private LocaleResolverService localeResolverService;

    @Inject
    private DeviceClassResolverService deviceClassResolverService;

    public AbstractPageRequestProcessor create( PageRequestProcessorContext context )
    {
        final AbstractPageRequestProcessor pageRequestProcessor;

        final PageRequestType pageRequestType = context.getPageRequestType();

        if ( PageRequestType.CONTENT.equals( pageRequestType ) )
        {
            pageRequestProcessor = new ContentRequestProcessor( context );

        }
        else if ( PageRequestType.MENUITEM.equals( pageRequestType ) )
        {
            pageRequestProcessor = new PageRequestProcessor( context );
        }
        else
        {
            throw new IllegalArgumentException( "PageRequestType not supported: " + pageRequestType );
        }

        pageRequestProcessor.setContentDao( contentDao );
        pageRequestProcessor.setPageTemplateDao( pageTemplateDao );
        pageRequestProcessor.setPortalAccessService( portalAccessService );
        pageRequestProcessor.setDeviceClassResolverService( deviceClassResolverService );
        pageRequestProcessor.setLocaleResolverService( localeResolverService );
        pageRequestProcessor.setSectionContentDao( sectionContentDao );

        return pageRequestProcessor;
    }
}
