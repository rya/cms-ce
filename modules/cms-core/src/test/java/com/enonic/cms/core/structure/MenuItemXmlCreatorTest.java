/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import org.jdom.JDOMException;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

public class MenuItemXmlCreatorTest
    extends AbstractSiteXmlCreatorTest
{

    private MenuItemXMLCreatorSetting setting;

    private MenuItemXmlCreator xmlCreator;

    SiteEntity site_1;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        site_1 = new SiteEntity();
        site_1.setKey( 1 );
        site_1.setLanguage( null );
    }


    public void testCreateLegacyGetMenuItemWithParentsWhenWanted1()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-label-result-2.xml" );

        MenuItemEntity mi1 = createMenuItem( "1", "1", null, site_1 );
        MenuItemEntity mi11 = createMenuItem( "2", "1.1", mi1, site_1 );
        MenuItemEntity mi111 = createMenuItem( "3", "1.1.1", mi11, site_1 );

        mi1.setType( MenuItemType.LABEL );
        mi11.setType( MenuItemType.LABEL );
        mi111.setType( MenuItemType.LABEL );

        mi1.setMenuName( "1" );
        mi11.setMenuName( "1.1" );
        mi111.setMenuName( "1.1.1" );

        mi1.setDisplayName( "1" );
        mi11.setDisplayName( "1.1" );
        mi111.setDisplayName( "1.1.1" );

        setting = new MenuItemXMLCreatorSetting();
        setting.includeParents = true;
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi111 );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemWithParentsWhenWanted2()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-label-result-4.xml" );

        MenuItemEntity mi1 = createMenuItem( "1", "1", null, site_1 );

        mi1.setType( MenuItemType.LABEL );
        mi1.setMenuName( "1" );
        mi1.setDisplayName( "1" );

        setting = new MenuItemXMLCreatorSetting();
        setting.includeParents = true;
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi1 );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemWithoutParentsWhenNotWanted()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-label-result-3.xml" );

        MenuItemEntity mi1 = createMenuItem( "1", "1", null, site_1 );
        MenuItemEntity mi11 = createMenuItem( "2", "1.1", mi1, site_1 );
        MenuItemEntity mi111 = createMenuItem( "3", "1.1.1", mi11, site_1 );

        mi1.setType( MenuItemType.LABEL );
        mi11.setType( MenuItemType.LABEL );
        mi111.setType( MenuItemType.LABEL );

        mi1.setMenuName( "1" );
        mi11.setMenuName( "1.1" );
        mi111.setMenuName( "1.1.1" );

        mi1.setDisplayName( "1" );
        mi11.setDisplayName( "1.1" );
        mi111.setDisplayName( "1.1.1" );

        setting = new MenuItemXMLCreatorSetting();
        setting.includeParents = false;
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi111 );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemUrl()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-url-result-1.xml" );

        MenuItemEntity mi = createMenuItem( "1", "one", null, site_1 );
        mi.setUrl( "www.vg.no" );
        mi.setOpenNewWindowForURL( true );
        mi.setType( MenuItemType.URL );
        mi.setMenuName( "one" );
        mi.setDisplayName( "one" );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemLabel()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-label-result-1.xml" );

        MenuItemEntity mi = createMenuItem( "1", "one", null, site_1 );

        mi.setType( MenuItemType.LABEL );
        mi.setDisplayName( "one" );
        mi.setMenuName( "one" );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemShortcut()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-shortcut-result-1.xml" );

        MenuItemEntity shortcutMenuItem = createMenuItem( "1", "one", null, site_1 );
        MenuItemEntity toMenuItem = createMenuItem( "2", "two", null, site_1 );
        createMenuItemShortcut( shortcutMenuItem, toMenuItem, true );

        shortcutMenuItem.setType( MenuItemType.SHORTCUT );
        shortcutMenuItem.setDisplayName( "one" );
        shortcutMenuItem.setMenuName( "one" );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( shortcutMenuItem );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemBuiltInContent()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-content-result-1.xml" );

        MenuItemEntity mi = createMenuItem( "1", "one", null, site_1 );
        mi.setType( MenuItemType.CONTENT );
        mi.setMenuName( "one" );
        mi.setDisplayName( "one" );
        String xmlDataString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<data cachedisabled=\"false\" cachetype=\"default\">" +
            "<parameters><parameter name=\"key\" override=\"false\">1366</parameter></parameters>" +
            "<document>built in content</document></data>";
        mi.setXmlData( XMLDocumentFactory.create( xmlDataString ).getAsJDOMDocument() );

        mi.setSection( true );
        Set<ContentTypeEntity> filteredContentTypes = new LinkedHashSet<ContentTypeEntity>();
        filteredContentTypes.add( createContentType( "201", "type1" ) );
        filteredContentTypes.add( createContentType( "202", "type2" ) );
        mi.setContentTypeFilter( filteredContentTypes );

        PageEntity page = createPage( "201" );
        PageTemplateEntity pageTemplate = createPageTemplate( "301", "name-301" );
        pageTemplate.setType( PageTemplateType.CONTENT );
        page.setTemplate( pageTemplate );
        mi.setPage( page );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

    public void testCreateLegacyGetMenuItemReferencedContent()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-content-result-2.xml" );

        MenuItemEntity mi = createMenuItem( "1", "one", null, site_1 );
        mi.setType( MenuItemType.CONTENT );
        mi.setMenuName( "one" );
        mi.setDisplayName( "one" );

        String xmlDataString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<data cachedisabled=\"false\" cachetype=\"default\">" + "<document/></data>";
        mi.setXmlData( XMLDocumentFactory.create( xmlDataString ).getAsJDOMDocument() );
        mi.setContent( createContent( "1001" ) );

        PageEntity page = createPage( "201" );
        PageTemplateEntity pageTemplate = createPageTemplate( "301", "name-301" );
        pageTemplate.setType( PageTemplateType.CONTENT );
        page.setTemplate( pageTemplate );
        mi.setPage( page );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }


    public void testCreateLegacyGetMenuItemSection()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-section-result-1.xml" );

        MenuItemEntity mi = createMenuItem( "1", "one", null, site_1 );
        mi.setType( MenuItemType.SECTION );
        mi.setMenuName( "one" );
        mi.setDisplayName( "one " );

        mi.setSection( true );
        Set<ContentTypeEntity> filteredContentTypes = new LinkedHashSet<ContentTypeEntity>();
        filteredContentTypes.add( createContentType( "201", "type1" ) );
        filteredContentTypes.add( createContentType( "202", "type2" ) );
        mi.setContentTypeFilter( filteredContentTypes );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }


    public void testCreateLegacyGetMenuItemPage()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/MenuItemXmlCreatorTest-page-result-1.xml" );

        MenuItemEntity mi = createMenuItem( "1", "one", null, site_1 );
        mi.setType( MenuItemType.PAGE );
        mi.setDisplayName( "one" );
        mi.setMenuName( "one" );

        PageEntity page = createPage( "201" );
        PageTemplateEntity pageTemplate = createPageTemplate( "301", "name-301" );
        pageTemplate.setType( PageTemplateType.CONTENT );
        page.setTemplate( pageTemplate );
        mi.setPage( page );

        setting = new MenuItemXMLCreatorSetting();
        xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );
        XMLDocument xmlDoc = xmlCreator.createLegacyGetMenuItem( mi );

        assertEquals( expectedXml, getFormattedXmlString( xmlDoc ) );
    }

}
