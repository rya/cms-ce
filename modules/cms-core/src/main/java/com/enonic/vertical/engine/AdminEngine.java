/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.handlers.BinaryDataHandler;
import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.ContentHandler;
import com.enonic.vertical.engine.handlers.ContentObjectHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;
import com.enonic.vertical.engine.handlers.LanguageHandler;
import com.enonic.vertical.engine.handlers.LogHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.PageHandler;
import com.enonic.vertical.engine.handlers.PageTemplateHandler;
import com.enonic.vertical.engine.handlers.SectionHandler;
import com.enonic.vertical.engine.handlers.SecurityHandler;
import com.enonic.vertical.engine.handlers.SystemHandler;
import com.enonic.vertical.engine.handlers.UnitHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.core.content.IndexService;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.ContentService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.security.user.User;

public final class AdminEngine
    extends BaseEngine
    implements InitializingBean
{

    private SiteDao siteDao;

    private BinaryDataHandler binaryDataHandler;

    private CategoryHandler categoryHandler;

    private CommonHandler commonHandler;

    private ContentHandler contentHandler;

    private ContentService contentService;

    private ContentObjectHandler contentObjectHandler;

    private GroupHandler groupHandler;

    @Autowired
    private MemberOfResolver memberOfResolver;

    private IndexService indexService;

    private LanguageHandler languageHandler;


    private LogHandler logHandler;

    private MenuHandler menuHandler;

    private PageHandler pageHandler;

    private PageTemplateHandler pageTemplateHandler;

    private SectionHandler sectionHandler;

    private SecurityHandler securityHandler;

    private SecurityService securityService;

    private SystemHandler systemHandler;

    private UnitHandler unitHandler;

    private UserHandler userHandler;

    public void afterPropertiesSet()
        throws Exception
    {
        init();
    }

    private void init()
    {
        // event listeners
        contentHandler.addListener( logHandler );
        contentHandler.addListener( sectionHandler );
        menuHandler.addListener( logHandler );
    }

    public CategoryHandler getCategoryHandler()
    {
        return categoryHandler;
    }

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public ContentObjectHandler getContentObjectHandler()
    {
        return contentObjectHandler;
    }

    public GroupHandler getGroupHandler()
    {
        return groupHandler;
    }

    public LanguageHandler getLanguageHandler()
    {
        return languageHandler;
    }

    public LogHandler getLogHandler()
    {
        return logHandler;
    }

    public MenuHandler getMenuHandler()
    {
        return menuHandler;
    }

    public PageHandler getPageHandler()
    {
        return pageHandler;
    }

    public PageTemplateHandler getPageTemplateHandler()
    {
        return pageTemplateHandler;
    }

    public SectionHandler getSectionHandler()
    {
        return sectionHandler;
    }

    public SecurityHandler getSecurityHandler()
    {
        return securityHandler;
    }

    public UserHandler getUserHandler()
    {
        return userHandler;
    }

    public boolean isEnterpriseAdmin( User user )
    {
        return memberOfResolver.hasEnterpriseAdminPowers( user.getKey() );
    }

    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseSchema();
    }

    public boolean initializeDatabaseValues()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseValues();
    }

    public Document getAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {

        try
        {
            openSharedConnection();
            return doGetAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );
        }
        finally
        {
            closeSharedConnection();
        }
    }

    private Document doGetAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {

        Document menuDoc = menuHandler.getAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );

        // Sort menuitems based on order
        Element[] siteElems = XMLTool.getElements( menuDoc.getDocumentElement() );
        for ( Element siteElem : siteElems )
        {
            XMLTool.sortChildElements( siteElem, "order", false, true );
        }

        return menuDoc;
    }

    /**
     * Return a map of top level menus with name.
     *
     * @return A map with the keys of the top level menus as keys, and their names as the corresponding value.
     * @throws SQLException If a database error occurs.
     */
    public Map<Integer, String> getMenuMap()
        throws SQLException
    {
        return this.menuHandler.getMenuMap();
    }

    public long getArchiveSizeByCategory( int categoryKey )
    {
        return this.categoryHandler.getArchiveSizeByCategory( CategoryKey.parse( categoryKey ) );
    }

    public long getArchiveSizeByUnit( int unitKey )
    {
        return this.categoryHandler.getArchiveSizeByUnit( unitKey );
    }

    public void setBinaryDataHandler( BinaryDataHandler binaryDataHandler )
    {
        this.binaryDataHandler = binaryDataHandler;
    }

    public void setCategoryHandler( CategoryHandler categoryHandler )
    {
        this.categoryHandler = categoryHandler;
    }

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void setContentHandler( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void setContentService( ContentService service )
    {
        contentService = service;
    }

    public void setContentObjectHandler( ContentObjectHandler contentObjectHandler )
    {
        this.contentObjectHandler = contentObjectHandler;
    }

    public void setGroupHandler( GroupHandler groupHandler )
    {
        this.groupHandler = groupHandler;
    }

    public void setLanguageHandler( LanguageHandler languageHandler )
    {
        this.languageHandler = languageHandler;
    }

    public void setLogHandler( LogHandler logHandler )
    {
        this.logHandler = logHandler;
    }

    public void setMenuHandler( MenuHandler menuHandler )
    {
        this.menuHandler = menuHandler;
    }

    public void setPageHandler( PageHandler pageHandler )
    {
        this.pageHandler = pageHandler;
    }

    public void setPageTemplateHandler( PageTemplateHandler pageTemplateHandler )
    {
        this.pageTemplateHandler = pageTemplateHandler;
    }

    public void setSectionHandler( SectionHandler sectionHandler )
    {
        this.sectionHandler = sectionHandler;
    }

    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }

    public void setUnitHandler( UnitHandler unitHandler )
    {
        this.unitHandler = unitHandler;
    }

    public void setSystemHandler( SystemHandler systemHandler )
    {
        this.systemHandler = systemHandler;
    }

    public void setSecurityHandler( SecurityHandler securityHandler )
    {
        this.securityHandler = securityHandler;
    }

    public void setSecurityService( SecurityService service )
    {
        securityService = service;
    }

    public void setSiteDao( SiteDao value )
    {
        this.siteDao = value;
    }

    public void setIndexService( IndexService service )
    {
        indexService = service;
    }
}
