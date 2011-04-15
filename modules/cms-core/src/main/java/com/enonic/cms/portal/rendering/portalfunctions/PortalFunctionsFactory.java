/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.portalfunctions;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.captcha.CaptchaService;
import com.enonic.cms.core.localization.LocalizationService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.portal.image.ImageService;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PortletDao;

public class PortalFunctionsFactory
{

    private static PortalFunctionsFactory instance;

    @Inject
    private SiteURLResolver siteURLResolver;

    private ContentDao contentDao;

    @Inject
    private MenuItemDao menuItemDao;

    @Inject
    private PortletDao portletDao;

    @Inject
    private CaptchaService captchaService;

    @Inject
    private LocalizationService localizeService;

    @Inject
    private LocaleResolverService localeResolverService;

    @Inject
    private ContentBinaryDataDao contentBinaryDataDao;

    @Inject
    private ImageService imageService;

    @Inject
    private SecurityService securityService;

    @Inject
    private CreateAttachmentUrlFunction createAttachmentUrlFunction;

    private SitePropertiesService sitePropertiesService;

    private final ThreadLocal<PortalFunctionsContext> context = new ThreadLocal<PortalFunctionsContext>();

    public static PortalFunctionsFactory get()
    {
        return instance;
    }

    public PortalFunctionsFactory()
    {
        instance = this;
    }

    public void setContext( PortalFunctionsContext value )
    {
        context.set( value );
    }

    public PortalFunctionsContext getContext()
    {
        return context.get();
    }

    public void removeContext()
    {
        context.remove();
    }

    public PortalFunctions createPortalFunctions()
    {
        HttpServletRequest httpRequest = ServletRequestAccessor.getRequest();

        PortalFunctions portalFunctions = new PortalFunctions();
        if ( getContext().getSiteURLResolver() != null )
        {
            portalFunctions.setSiteURLResolver( getContext().getSiteURLResolver() );
        }
        else
        {
            portalFunctions.setSiteURLResolver( siteURLResolver );
        }
        portalFunctions.setCaptchaService( captchaService );
        portalFunctions.setLocalizeService( localizeService );
        portalFunctions.setLocaleResolvingService( localeResolverService );
        portalFunctions.setContentDao( contentDao );
        portalFunctions.setMenuItemDao( menuItemDao );
        portalFunctions.setPortletDao( portletDao );
        portalFunctions.setRequest( httpRequest );
        portalFunctions.setEncodeURIs( getContext().isEncodeURIs() );
        portalFunctions.setContext( getContext() );
        portalFunctions.setContentBinaryDataDao( contentBinaryDataDao );
        portalFunctions.setImageService( imageService );
        portalFunctions.setSecurityService( securityService );
        portalFunctions.setCreateAttachmentUrlFunction( createAttachmentUrlFunction );
        portalFunctions.setSitePropertiesService( sitePropertiesService );

        return portalFunctions;
    }


    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    @Inject
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }
}
