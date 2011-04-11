/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.core.security.userstore.MemberOfResolver;

public final class AdminEngine
    extends BaseEngine
    implements InitializingBean
{
    private CategoryHandler categoryHandler;

    private CommonHandler commonHandler;

    private ContentHandler contentHandler;

    private ContentObjectHandler contentObjectHandler;

    private GroupHandler groupHandler;

    @Autowired
    private MemberOfResolver memberOfResolver;

    private LanguageHandler languageHandler;


    private LogHandler logHandler;

    private MenuHandler menuHandler;

    private PageHandler pageHandler;

    private PageTemplateHandler pageTemplateHandler;

    private SectionHandler sectionHandler;

    private SecurityHandler securityHandler;

    private SystemHandler systemHandler;

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

    public void setSystemHandler( SystemHandler systemHandler )
    {
        this.systemHandler = systemHandler;
    }

    public void setSecurityHandler( SecurityHandler securityHandler )
    {
        this.securityHandler = securityHandler;
    }
}
