/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import javax.inject.Inject;

import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.engine.BaseEngine;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PageDao;
import com.enonic.cms.store.dao.PageTemplateDao;
import com.enonic.cms.store.dao.PortletDao;
import com.enonic.cms.store.dao.ResourceDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

public abstract class BaseHandler
{
    protected BaseEngine baseEngine;

    @Inject
    protected VerticalProperties verticalProperties;

    // Services:

    @Inject
    protected LogService logService;

    @Inject
    protected ContentService contentService;

    @Inject
    protected SecurityService securityService;

    // Daos:

    @Inject
    protected BinaryDataDao binaryDataDao;

    @Inject
    protected ContentBinaryDataDao contentBinaryDataDao;

    @Inject
    protected ContentDao contentDao;

    @Inject
    protected PortletDao portletDao;

    @Inject
    protected CategoryDao categoryDao;

    @Inject
    protected GroupDao groupDao;

    @Inject
    protected LanguageDao languageDao;

    @Inject
    protected MenuItemDao menuItemDao;

    @Inject
    protected PageDao pageDao;

    @Inject
    protected PageTemplateDao pageTemplateDao;

    @Inject
    protected ResourceDao resourceDao;

    @Inject
    protected SiteDao siteDao;

    @Inject
    protected UserDao userDao;

    @Inject
    protected UserStoreDao userStoreDao;

    @Inject
    protected MemberOfResolver memberOfResolver;

    public BaseHandler()
    {

    }

    public void init()
    {
    }

    public void setVerticalProperties( VerticalProperties value )
    {
        this.verticalProperties = value;
    }

    public void setBaseEngine( BaseEngine value )
    {
        this.baseEngine = value;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }
}
