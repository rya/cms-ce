/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.Date;

import org.joda.time.DateTime;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.core.structure.access.MenuItemAccessResolver;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemType;
import com.enonic.cms.domain.structure.page.PageEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;

public abstract class AbstractSiteXmlCreatorTest
    extends AbstractXmlCreatorTest
{

    protected final Date MENUITEM_DEFAULT_TIMESTAMP = new DateTime( 2007, 7, 25, 9, 0, 0, 0 ).toDate();

    protected final String xmlDataString =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<data cachedisabled=\"false\" cachetype=\"default\">" +
            "<parameters><parameter name=\"p1\" override=\"false\">v1</parameter>" +
            "<parameter name=\"p2\" override=\"false\">v2</parameter>" + "<parameter name=\"p3\" override=\"false\">v3</parameter>" +
            "</parameters><document/></data>";


    protected UserEntity standardUser;

    protected MenuItemAccessResolver menuItemAccessResolver;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        GroupDao groupDao = new GroupEntityDao()
        {

            @Override
            public GroupEntity findBuiltInAnonymous()
            {
                return null;
            }

            @Override
            public GroupEntity findBuiltInEnterpriseAdministrator()
            {
                return null;
            }
        };
        menuItemAccessResolver = new MenuItemAccessResolver( groupDao )
        {
            @Override
            public boolean doHasAccess( UserEntity user, MenuItemEntity menuItemEntity, MenuItemAccessType menuItemAccessType )
            {
                return true;
            }
        };

        standardUser = new UserEntity();
        standardUser.setKey( new UserKey( "KEY" ) );
        standardUser.setDisplayName( "Fullname" );
        standardUser.setName( "uid" );
    }

    protected LanguageEntity createLanguage( String key, String code, String description )
    {
        LanguageEntity language = new LanguageEntity();
        language.setKey( new LanguageKey( key ) );
        language.setCode( code );
        language.setDescription( description );
        return language;
    }

    protected MenuItemEntity createMenuItem( String key, String name, MenuItemEntity parent, SiteEntity site )
    {
        return createMenuItem( key, name, parent, site, false );
    }

    protected MenuItemEntity createMenuItem( String key, String name, MenuItemEntity parent, SiteEntity site, boolean includeParams )
    {

        MenuItemEntity mi = new MenuItemEntity();
        mi.setKey( Integer.parseInt( key ) );
        mi.setName( name );
        mi.setOrder( 0 );
        mi.setType( MenuItemType.CONTENT );
        mi.setHidden( false );
        mi.setTimestamp( MENUITEM_DEFAULT_TIMESTAMP );
        if ( parent != null )
        {
            mi.setParent( parent );
            parent.addChild( mi );
        }
        mi.setSite( site );
        mi.setOwner( standardUser );
        mi.setModifier( standardUser );
        if ( includeParams )
        {
            XMLDocument doc = XMLDocumentFactory.create( xmlDataString );
            mi.setXmlData( doc.getAsJDOMDocument() );
        }
        return mi;

    }

    protected void createMenuItemShortcut( MenuItemEntity shortcutMenuItem, MenuItemEntity toMenuItem, boolean forward )
    {
        shortcutMenuItem.setMenuItemShortcut( toMenuItem );
        shortcutMenuItem.setShortcutForward( forward );
    }

    protected PageEntity createPage( String key )
    {
        PageEntity page = new PageEntity();
        page.setKey( Integer.valueOf( key ) );

        return page;
    }

    protected PageTemplateEntity createPageTemplate( String key, String name )
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( Integer.parseInt( key ) );
        pageTemplate.setName( name );
        return pageTemplate;
    }
}
