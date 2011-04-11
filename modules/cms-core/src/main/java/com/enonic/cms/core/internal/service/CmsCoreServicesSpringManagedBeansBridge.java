/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.content.ContentParserService;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.preferences.PreferenceService;
import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.portal.cache.SiteCachesService;
import com.enonic.cms.portal.rendering.PageRendererFactory;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;


public class CmsCoreServicesSpringManagedBeansBridge
{
    private static CmsCoreServicesSpringManagedBeansBridge instance;

    private AdminService adminService;

    private ContentService contentService;

    private DataSourceService dataSourceService;

    private PageRendererFactory pageRendererFactory;

    private PreferenceService preferenceService;

    private PresentationService presentationService;

    private ResourceService resourceService;

    private SecurityService securityService;

    private SiteCachesService siteCachesService;

    private SitePropertiesService sitePropertiesService;

    private SiteURLResolver siteURLResolver;

    private UserServicesService userServicesService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentParserService contentParserService;

    private SiteService siteService;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private SendMailService sendMailService;

    public CmsCoreServicesSpringManagedBeansBridge()
    {
        instance = this;
    }

    public static PresentationService getPresentationService()
    {
        return instance.presentationService;
    }

    public void setPresentationService( PresentationService value )
    {
        this.presentationService = value;
    }

    public static DataSourceService getDataSourceService()
    {
        return instance.dataSourceService;
    }

    public void setDataSourceService( DataSourceService value )
    {
        this.dataSourceService = value;
    }

    public static UserServicesService getUserServicesService()
    {
        return instance.userServicesService;
    }

    public static AdminService getAdminService()
    {
        return instance.adminService;
    }

    public void setAdminService( AdminService value )
    {
        this.adminService = value;
    }

    public static SiteURLResolver getSiteURLResolver()
    {
        return instance.siteURLResolver;
    }

    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public static PageRendererFactory getPageRendererFactory()
    {
        return instance.pageRendererFactory;
    }

    public void setPageRendererFactory( PageRendererFactory value )
    {
        this.pageRendererFactory = value;
    }

    public void setResourceService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public static ResourceService getResourceService()
    {
        return instance.resourceService;
    }

    public void setPreferenceService( PreferenceService preferenceService )
    {
        this.preferenceService = preferenceService;
    }

    public static PreferenceService getPreferenceService()
    {
        return instance.preferenceService;
    }

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public static SecurityService getSecurityService()
    {
        return instance.securityService;
    }

    public static SiteCachesService getSiteCachesService()
    {
        return instance.siteCachesService;
    }

    public void setSiteCachesService( SiteCachesService siteCachesService )
    {
        this.siteCachesService = siteCachesService;
    }

    public static SiteService getSiteService()
    {
        return instance.siteService;
    }

    public void setSiteService( SiteService value )
    {
        this.siteService = value;
    }

    /**
     * @return the contentService
     */
    public static ContentService getContentService()
    {
        return instance.contentService;
    }

    /**
     * @param contentService the contentService to set
     */
    public void setContentService( ContentService contentService )
    {
        this.contentService = contentService;
    }

    /**
     * @return the contentDao
     */
    public static ContentDao getContentDao()
    {
        return instance.contentDao;
    }

    public static ContentParserService getContentParserService()
    {
        return instance.contentParserService;
    }

    public static SiteDao getSiteDao()
    {
        return instance.siteDao;
    }

    public static MenuItemDao getMenuItemDao()
    {
        return instance.menuItemDao;
    }

    public static GroupDao getGroupDao()
    {
        return instance.groupDao;
    }

    /**
     * @param contentDao the contentDao to set
     */
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public static UserStoreService getUserStoreService()
    {
        return instance.userStoreService;
    }

    public void setUserServicesService( UserServicesService value )
    {
        this.userServicesService = value;
    }

    public static SendMailService getSendMailService()
    {
        return instance.sendMailService;
    }

    public void setSendMailService( SendMailService sendMailService )
    {
        this.sendMailService = sendMailService;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public static SitePropertiesService getSitePropertiesService()
    {
        return instance.sitePropertiesService;
    }

    public static UserDao getUserDao()
    {
        return instance.userDao;
    }
}
