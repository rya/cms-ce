/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.rendering.portalfunctions;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.localization.LocalizationService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PortletDao;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SiteURLResolver;
import com.enonic.cms.core.captcha.CaptchaService;

import com.enonic.cms.business.portal.image.ImageService;

public class PortalFunctionsFactory
{

    private static PortalFunctionsFactory instance;

    private SiteURLResolver siteURLResolver;

    private ContentDao contentDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private PortletDao portletDao;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private LocalizationService localizeService;

    @Autowired
    private LocaleResolverService localeResolverService;

    @Autowired
    private ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    private ImageService imageService;

    @Autowired
    private SecurityService securityService;

    @Autowired
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

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }
}
