/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.adminweb.access.AdminConsoleLoginAccessResolver;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.resource.access.ResourceAccessResolver;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.core.structure.menuitem.MenuItemService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentHandlerDao;
import com.enonic.cms.store.dao.ContentIndexDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UnitDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;
import com.enonic.cms.upgrade.UpgradeService;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.core.content.ContentParserService;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.category.CategoryService;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.content.imports.ImportService;
import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.core.country.CountryService;
import com.enonic.cms.core.locale.LocaleService;

import com.enonic.cms.business.portal.cache.SiteCachesService;
import com.enonic.cms.business.portal.rendering.PageRendererFactory;
import com.enonic.cms.business.preview.PreviewService;
import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.core.timezone.TimeZoneService;

public abstract class AbstractAdminwebServlet
    extends HttpServlet
    implements Controller, ServletContextAware, InitializingBean, DisposableBean, ApplicationContextAware
{

    @Autowired
    protected VerticalProperties verticalProperties;

    // Daos:

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected ContentIndexDao contentIndexDao;

    @Autowired
    protected ContentHandlerDao contentHandlerDao;

    @Autowired
    protected ContentTypeDao contentTypeDao;

    @Autowired
    protected ContentVersionDao contentVersionDao;

    @Autowired
    protected GroupDao groupDao;

    protected LanguageDao languageDao;

    @Autowired
    protected MenuItemDao menuItemDao;

    @Autowired
    protected PageTemplateDao pageTemplateDao;

    @Autowired
    protected PortletDao portletDao;

    @Autowired
    protected SiteDao siteDao;

    @Autowired
    protected UnitDao unitDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected UserStoreDao userStoreDao;

    // Services:

    @Autowired
    protected AdminService adminService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected ContentParserService contentParserService;

    @Autowired
    protected CountryService countryService;

    @Autowired
    protected DeviceClassResolverService deviceClassResolverService;

    @Autowired
    protected ImportService importService;

    @Autowired
    protected KeyService keyService;

    @Autowired
    protected LocaleService localeService;

    @Autowired
    protected LocaleResolverService localeResolverService;

    @Autowired
    protected LogService logService;

    @Autowired
    protected MenuItemService menuItemService;

    @Autowired
    protected PresentationService presentation;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected CategoryService categoryService;

    @Autowired
    protected SendMailService sendMailService;

    @Autowired
    protected SiteService siteService;

    @Autowired
    protected SiteCachesService siteCachesService;

    @Autowired
    protected SitePropertiesService sitePropertiesService;

    @Autowired
    protected TimeService timeService;

    @Autowired
    protected PreviewService previewService;

    @Autowired
    protected TimeZoneService timeZoneService;

    @Autowired
    protected UpgradeService upgradeService;

    @Autowired
    protected UserStoreService userStoreService;

    // Factories:

    @Autowired
    protected ImportJobFactory importJobFactory;

    @Autowired
    protected PageRendererFactory pageRendererFactory;

    @Autowired
    protected SynchronizeUserStoreJobFactory synchronizeUserStoreJobFactory;

    // Resolvers:

    @Autowired
    protected AdminConsoleLoginAccessResolver adminConsoleLoginAccessResolver;

    @Autowired
    protected MemberOfResolver memberOfResolver;

    @Autowired
    protected ResourceAccessResolver resourceAccessResolver;

    private ServletContext servletContext;

    public final ServletContext getServletContext()
    {
        return this.servletContext;
    }

    public final void setServletContext( final ServletContext servletContext )
    {
        this.servletContext = servletContext;
    }

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext( ApplicationContext applicationContext )
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public ModelAndView handleRequest( final HttpServletRequest request, final HttpServletResponse response )
        throws Exception
    {
        service( request, response );
        return null;
    }

    public void afterPropertiesSet()
        throws Exception
    {
        final String servletName = getClass().getSimpleName();

        init( new ServletConfig()
        {
            public String getServletName()
            {
                return servletName;
            }

            public ServletContext getServletContext()
            {
                return servletContext;
            }

            public String getInitParameter( final String name )
            {
                return null;
            }

            public Enumeration getInitParameterNames()
            {
                return Collections.enumeration( Collections.EMPTY_LIST );
            }
        } );
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );

        final ServletContext context = getServletContext();
        AdminStore.initialize( context );
    }

    protected AdminService lookupAdminBean()
    {
        return adminService;
    }

    protected KeyService lookupKeyBean()
        throws VerticalAdminException
    {
        return keyService;
    }


    protected boolean isRequestForAdminPath( String path, HttpServletRequest request )
    {
        if ( path == null )
        {
            throw new NullPointerException( path );
        }
        if ( !path.startsWith( "/" ) )
        {
            throw new IllegalArgumentException( "Expected a path that starts with a forward slash" );
        }

        return request.getRequestURI().endsWith( path );
    }

    protected void redirectClientToAdminPath( String adminPath, HttpServletRequest request, HttpServletResponse response )
        throws VerticalAdminException
    {
        redirectClientToAdminPath( adminPath, (MultiValueMap) null, request, response );
    }

    protected void redirectClientToAdminPath( String adminPath, ExtendedMap formItems, HttpServletRequest request,
                                              HttpServletResponse response )
        throws VerticalAdminException
    {
        MultiValueMap mv = null;
        if ( formItems != null )
        {
            mv = new MultiValueMap( formItems );
        }
        redirectClientToAdminPath( adminPath, mv, request, response );
    }

    protected void redirectClientToAdminPath( String adminPath, String parameterName, String parameterValue, HttpServletRequest request,
                                              HttpServletResponse response )
        throws VerticalAdminException
    {
        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( parameterName, parameterValue );

        redirectClientToAdminPath( adminPath, queryParams, request, response );
    }

    protected void redirectClientToAdminPath( String adminPath, MultiValueMap queryParams, HttpServletRequest request,
                                              HttpServletResponse response )
        throws VerticalAdminException
    {
        AdminHelper.redirectClientToAdminPath( request, response, adminPath, queryParams );
    }

    protected void redirectClientToReferer( HttpServletRequest request, HttpServletResponse response )
    {
        AdminHelper.redirectClientToReferer( request, response );
    }

    protected void redirectClientToURL( URL url, HttpServletResponse response )
    {
        AdminHelper.redirectToURL( url, response );
    }

    protected void redirectClientToAbsoluteUrl( String url, HttpServletResponse response )
    {
        AdminHelper.redirectClientToAbsoluteUrl( url, response );
    }


    protected void forwardRequest( String adminPath, HttpServletRequest request, HttpServletResponse response )
        throws VerticalAdminException
    {
        int length = adminPath != null ? adminPath.length() : 0;
        length += "/admin".length();

        StringBuffer newUrl = new StringBuffer( length );
        newUrl.append( "/admin" );
        newUrl.append( adminPath );

        try
        {
            RequestDispatcher dispatcher = request.getRequestDispatcher( newUrl.toString() );
            dispatcher.forward( request, response );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to forward request to \"%0\": %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, message, adminPath, ioe );
        }
        catch ( ServletException se )
        {
            String message = "Failed to forward request to \"%0\": %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, message, adminPath, se );
        }
    }

}
