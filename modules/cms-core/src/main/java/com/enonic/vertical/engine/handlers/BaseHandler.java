/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;

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

    protected VerticalProperties verticalProperties;

    // Services:

    @Autowired
    protected LogService logService;

    @Autowired
    protected ContentService contentService;

    protected SecurityService securityService;

    // Daos:

    @Autowired
    protected BinaryDataDao binaryDataDao;

    @Autowired
    protected ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected PortletDao portletDao;

    @Autowired
    protected CategoryDao categoryDao;

    @Autowired
    protected GroupDao groupDao;

    @Autowired
    protected LanguageDao languageDao;

    @Autowired
    protected MenuItemDao menuItemDao;

    @Autowired
    protected PageDao pageDao;

    @Autowired
    protected PageTemplateDao pageTemplateDao;

    @Autowired
    protected ResourceDao resourceDao;

    @Autowired
    protected SiteDao siteDao;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected UserStoreDao userStoreDao;

    @Autowired
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

    protected final void close( ResultSet resultSet )
    {
        baseEngine.close( resultSet );
    }

    protected final void close( Statement stmt )
    {

        baseEngine.close( stmt );
    }

    protected final void close( Connection con )
    {

        baseEngine.close( con );
    }
}
