/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.sql.model.Table;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.engine.BaseEngine;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.dbmodel.VerticalDatabase;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.service.KeyService;
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

import com.enonic.cms.domain.CmsDateAndTimeFormats;

public abstract class BaseHandler
{
    protected final VerticalDatabase db = VerticalDatabase.getInstance();

    protected BaseEngine baseEngine;

    protected VerticalProperties verticalProperties;

    // Services:

    @Autowired
    protected LogService logService;

    @Autowired
    protected ContentService contentService;

    private KeyService keyService;

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


    public void setKeyService( KeyService value )
    {
        this.keyService = value;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }

    protected final CategoryHandler getCategoryHandler()
    {
        return baseEngine.getCategoryHandler();
    }

    protected final ContentHandler getContentHandler()
    {
        return baseEngine.getContentHandler();
    }

    protected final CommonHandler getCommonHandler()
    {
        return baseEngine.getCommonHandler();
    }

    protected final ContentObjectHandler getContentObjectHandler()
    {
        return baseEngine.getContentObjectHandler();
    }

    protected final GroupHandler getGroupHandler()
    {
        return baseEngine.getGroupHandler();
    }

    protected final LanguageHandler getLanguageHandler()
    {
        return baseEngine.getLanguageHandler();
    }

    protected final LogHandler getLogHandler()
    {
        return baseEngine.getLogHandler();
    }

    protected final MenuHandler getMenuHandler()
    {
        return baseEngine.getMenuHandler();
    }

    protected final PageHandler getPageHandler()
    {
        return baseEngine.getPageHandler();
    }

    protected final PageTemplateHandler getPageTemplateHandler()
    {
        return baseEngine.getPageTemplateHandler();
    }

    protected final SectionHandler getSectionHandler()
    {
        return baseEngine.getSectionHandler();
    }

    protected final SecurityHandler getSecurityHandler()
    {
        return baseEngine.getSecurityHandler();
    }

    protected final UserHandler getUserHandler()
    {
        return baseEngine.getUserHandler();
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

    protected final Connection getConnection()
        throws SQLException
    {
        return baseEngine.getConnection();
    }

    protected final Date parseDate( String dateString )
        throws ParseException
    {
        return CmsDateAndTimeFormats.parseFrom_STORE_DATE( dateString );
    }

    public final int getNextKey( String tableName )
        throws VerticalKeyException
    {
        return keyService.generateNextKeySafe( tableName );
    }

    public final int getNextKey( Table table )
        throws VerticalKeyException
    {
        return keyService.generateNextKeySafe( table.getName() );
    }
}
