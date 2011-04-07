/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enonic.cms.framework.xml.XMLBuilder;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.structure.access.MenuItemAccessResolver;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.domain.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.page.PageEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;

public class MenuItemXmlCreator
{

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm" );

    private MenuItemXMLCreatorSetting setting;

    private MenuItemAccessResolver menuItemAccessResolver;

    private MenuItemAccessRightsAccumulatedXmlCreator accessRightsAccumulatedXmlCreator = new MenuItemAccessRightsAccumulatedXmlCreator();

    private boolean includeSiteInfo = false;

    private boolean includePathInfo = false;

    private boolean includePageTemplateName = false;

    private boolean includeAnonynousReadInfo = false;

    private boolean includeUserAccessRightsInfo = false;

    public MenuItemXmlCreator( MenuItemXMLCreatorSetting setting, MenuItemAccessResolver menuItemAccessResolver )
    {
        this.setting = setting;
        this.menuItemAccessResolver = menuItemAccessResolver;
    }

    public void setIncludeAnonynousReadInfo( boolean value )
    {
        this.includeAnonynousReadInfo = value;
    }

    public void setIncludeUserAccessRightsInfo( boolean includeUserAccessRightsInfo )
    {
        this.includeUserAccessRightsInfo = includeUserAccessRightsInfo;
    }

    public Element createMenuItemElement( final MenuItemEntity menuItem )
    {
        final XMLBuilder xmlDoc = new XMLBuilder( "menuitems" );
        doAddMenuItemElement( xmlDoc, menuItem, false, 0 );
        return (Element) xmlDoc.getRootElement().getChild( "menuitem" ).detach();
    }

    public Element createMenuItemElement( MenuItemEntity menuItem, MenuItemAccumulatedAccessRights accessRightsForUser )
    {
        return createMenuItemElement( menuItem, accessRightsForUser, null );
    }

    public Element createMenuItemElement( MenuItemEntity menuItem, MenuItemAccumulatedAccessRights accessRightsForUser,
                                          MenuItemAccumulatedAccessRights accesRightsForAnonymous )
    {
        XMLBuilder xmlDoc = new XMLBuilder( "menuitems" );
        doAddMenuItemElement( xmlDoc, menuItem, false, 0 );
        final Element menuItemEl = (Element) xmlDoc.getRootElement().getChild( "menuitem" ).detach();
        if ( includeAnonynousReadInfo )
        {
            menuItemEl.setAttribute( "anonread", accesRightsForAnonymous.isReadAccess() ? "true" : "false" );
        }
        if ( includeUserAccessRightsInfo )
        {
            accessRightsAccumulatedXmlCreator.setUserRightAttributes( menuItemEl, accessRightsForUser );
        }

        return menuItemEl;
    }

    public XMLDocument createLegacyGetMenuItem( MenuItemEntity menuItem )
    {

        if ( menuItem == null )
        {
            XMLBuilder xmlDoc = new XMLBuilder( "menuitems" );
            return xmlDoc.getDocument();
        }

        // forcing mandantory settings
        setting.menuItemLevels = 1;
        setting.includeTypeSpecificXML = true;
        setting.includeDocumentElement = true;
        setting.includeChildren = false;

        // checking access rights first, returning empty menuitems element if no read right
        if ( !menuItemAccessResolver.hasAccess( setting.user, menuItem, MenuItemAccessType.READ ) )
        {
            XMLBuilder xmlDoc = new XMLBuilder( "menuitems" );
            xmlDoc.setAttribute( "istop", menuItem.isAtTopLevel() ? "yes" : "no" );
            return xmlDoc.getDocument();
        }

        // simple handling if parents is not wanted
        if ( !setting.includeParents )
        {
            XMLBuilder xmlDoc = new XMLBuilder( "menuitems" );
            xmlDoc.setAttribute( "istop", menuItem.isAtTopLevel() ? "yes" : "no" );
            addMenuItemElement( xmlDoc, menuItem, false );
            return xmlDoc.getDocument();
        }

        // special handling if parents wanted...
        List<MenuItemEntity> menuItemPath = menuItem.getMenuItemPath();
        // build one document for each menuitem in path
        List<Document> xmlDocs = new ArrayList<Document>();
        for ( MenuItemEntity curMenuItem : menuItemPath )
        {
            XMLBuilder xmlDoc = new XMLBuilder( "menuitems" );
            xmlDoc.setAttribute( "istop", curMenuItem.isAtTopLevel() ? "yes" : "no" );
            addMenuItemElement( xmlDoc, curMenuItem, false );
            xmlDocs.add( xmlDoc.getRootDocument() );
        }
        // merge the menuitems into one doc (the doc in position 0)
        while ( xmlDocs.size() > 1 )
        {
            insertLastMenuItemAsASubnode( xmlDocs );
        }

        return XMLDocumentFactory.create( xmlDocs.get( 0 ) );
    }

    private void insertLastMenuItemAsASubnode( List<Document> xmlDocs )
    {

        Document last = xmlDocs.get( xmlDocs.size() - 1 );
        xmlDocs.remove( last );
        Document parent = xmlDocs.get( xmlDocs.size() - 1 );
        Element rootLast = last.getRootElement();
        Element parentRoot = parent.getRootElement();
        Element parentMenuItem = parentRoot.getChild( "menuitem" );
        parentMenuItem.removeChild( "menuitems" );
        parentMenuItem.addContent( rootLast.detach() );
    }

    public Document createMenuItemsDocument( Iterable<MenuItemEntity> menuItems, String rootElementName )
    {
        return doCreateMenuItemsDocument( menuItems, rootElementName );
    }

    private Document doCreateMenuItemsDocument( Iterable<MenuItemEntity> menuItems, String rootElementName )
    {
        XMLBuilder xmlDoc = new XMLBuilder( rootElementName );
        for ( MenuItemEntity menuItem : menuItems )
        {
            doAddMenuItemElement( xmlDoc, menuItem, setting.includeChildren, 0 );
        }
        return new Document( (Element) xmlDoc.getRootElement().detach() );
    }

    public void addMenuItemElement( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {
        doAddMenuItemElement( xmlDoc, menuItem, true, 0 );
    }

    public void addMenuItemElement( XMLBuilder xmlDoc, MenuItemEntity menuItem, boolean includeChildren )
    {
        doAddMenuItemElement( xmlDoc, menuItem, includeChildren, 0 );
    }

    private void doAddMenuItemElement( XMLBuilder xmlDoc, MenuItemEntity menuItem, boolean includeChildren, int menuItemLevelsWalked )
    {

        xmlDoc.startElement( "menuitem" );
        xmlDoc.setAttribute( "key", menuItem.getKey() );
        xmlDoc.setAttribute( "menukey", menuItem.getSite().getKey().toString() );
        xmlDoc.setAttribute( "modifier", menuItem.getModifier().getKey().toString() );
        xmlDoc.setAttribute( "order", menuItem.getOrder() );
        xmlDoc.setAttribute( "owner", menuItem.getOwner().getKey().toString() );
        xmlDoc.setAttribute( "timestamp", TIMESTAMP_FORMATTER.print( menuItem.getTimestamp().getTime() ) );
        xmlDoc.setAttribute( "type", menuItem.getType().getName() );
        xmlDoc.setAttribute( "visible", menuItem.getHidden() ? "no" : "yes" );
        if ( menuItem.getParent() != null )
        {
            xmlDoc.setAttribute( "parent", menuItem.getParent().getKey() );
        }
        if ( includeSiteInfo )
        {
            xmlDoc.setAttribute( "site-name", menuItem.getSite().getName() );
        }
        addLanguageAttributes( xmlDoc, menuItem );

        xmlDoc.addContentElement( "name", menuItem.getName() );
        xmlDoc.addContentElement( "menu-name", asEmptyIfNull( menuItem.getMenuName() ) );
        xmlDoc.addContentElement( "display-name", asEmptyIfNull( menuItem.getDisplayName() ) );

        xmlDoc.addContentElement( "show-in-menu", menuItem.getHidden() ? "false" : "true" );
        xmlDoc.addContentElement( "description", menuItem.getDescription() );
        xmlDoc.addContentElement( "keywords", menuItem.getKeywords() );

        if ( includePathInfo )
        {
            xmlDoc.startElement( "path" );
            xmlDoc.addContent( menuItem.getPathAsString() );
            xmlDoc.endElement();
        }

        addFromXmlData( xmlDoc, menuItem );

        addTypeSpecificXml( xmlDoc, menuItem );

        addMenuItemsElement( xmlDoc, menuItem, includeChildren, menuItemLevelsWalked + 1 );

        if ( setting.activeMenuItem != null )
        {

            if ( menuItem.getKey() == setting.activeMenuItem.getKey() )
            {
                xmlDoc.setAttribute( "active", "true" );
                xmlDoc.setAttribute( "path", "true" );
            }

            if ( menuItem.isParentOf( setting.activeMenuItem ) )
            {
                xmlDoc.setAttribute( "path", "true" );
            }
        }

        xmlDoc.endElement();
    }

    private String resolveDisplayName( MenuItemEntity menuItem )
    {
        if ( StringUtils.isEmpty( menuItem.getMenuName() ) )
        {
            return menuItem.getName();
        }
        return menuItem.getMenuName();
    }

    private void addLanguageAttributes( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {
        LanguageEntity language = menuItem.getLanguage();
        if ( language == null )
        {
            return;
        }
        xmlDoc.setAttribute( "language", language.getDescription() );
        xmlDoc.setAttribute( "languagecode", language.getCode() );
        LanguageKey languageKey = language.getKey();
        xmlDoc.setAttribute( "languagekey", languageKey != null ? languageKey.toString() : "" );
    }


    private void addFromXmlData( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {

        if ( !menuItem.hasXmlData() )
        {
            xmlDoc.startElement( "parameters" );
            xmlDoc.endElement();
            return;
        }

        Document dataDoc = menuItem.getXmlDataAsClonedJDomDocument();
        Element dataEl = dataDoc.getRootElement();

        // parameters element
        Element parametersEl = dataEl.getChild( "parameters" );
        if ( parametersEl != null )
        {
            addParametersElement( xmlDoc, (Element) parametersEl.detach() );
        }
        else
        {
            xmlDoc.startElement( "parameters" );
            xmlDoc.endElement();
        }

        // document element
        Element documentEl = dataEl.getChild( "document" );
        if ( documentEl != null )
        {
            documentEl.detach();
            if ( setting.includeDocumentElement )
            {
                addDocumentElement( xmlDoc, (Element) documentEl.detach() );
            }
        }

        // data element
        addDataElement( xmlDoc, (Element) dataEl.detach() );
    }

    private void addDataElement( XMLBuilder xmlDoc, Element dataEl )
    {

        Element menuItemElement = xmlDoc.getCurrentElement();
        menuItemElement.addContent( dataEl.detach() );
    }

    private void addParametersElement( XMLBuilder xmlDoc, Element parametersEl )
    {

        Element menuItemElement = xmlDoc.getCurrentElement();
        menuItemElement.addContent( parametersEl );
    }

    private void addDocumentElement( XMLBuilder xmlDoc, Element documentEl )
    {

        Element menuItemElement = xmlDoc.getCurrentElement();
        menuItemElement.addContent( documentEl );
        documentEl.setAttribute( "mode", "xhtml" );
    }

    private void addTypeSpecificXml( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {

        int type = menuItem.getType().getKey();
        // special handling of type url, cause it has to be displayed whatever is set in includeTypeSpecificXML
        if ( type == 2 )
        {
            addUrlElement( xmlDoc, menuItem );
            return;
        }

        if ( setting.includeTypeSpecificXML )
        {
            // build type-specific XML:
            switch ( type )
            {
                case 1: // page
                    addPageElement( xmlDoc, menuItem );
                    break;
                //case 2: // url
                //    addUrlElement(xmlDoc, menuItem);
                //    break;
                case 4:// content
                    addSectionElement( xmlDoc, menuItem );
                    addPageElement( xmlDoc, menuItem );
                    addContentKeyAttribute( xmlDoc, menuItem );
                    break;
                case 5: // label
                    break;
                case 6:// section
                    addSectionElement( xmlDoc, menuItem );
                    break;
                case 7:// shortcut
                    addShortcutElement( xmlDoc, menuItem );
                    break;
            }
        }
    }

    private void addContentKeyAttribute( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {

        ContentEntity content = menuItem.getContent();
        if ( content != null )
        {
            xmlDoc.setAttribute( "contentkey", String.valueOf( content.getKey() ) );
        }
    }

    private void addUrlElement( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {

        int menuItemType = menuItem.getType().getKey();
        if ( menuItemType != 2 )
        {
            return;
        }
        String url = menuItem.getUrl();
        if ( url == null )
        {
            return;
        }
        xmlDoc.startElement( "url" );
        xmlDoc.setAttribute( "newwindow", menuItem.isOpenNewWindowForURL() ? "yes" : "no" );
        xmlDoc.addContent( url );
        xmlDoc.endElement();
    }

    protected void addShortcutElement( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {

        int menuItemType = menuItem.getType().getKey();
        if ( menuItemType != 7 )
        {
            return;
        }
        MenuItemEntity shortcutDestination = menuItem.getMenuItemShortcut();
        if ( shortcutDestination == null )
        {
            return;
        }

        xmlDoc.startElement( "shortcut" );
        xmlDoc.setAttribute( "key", shortcutDestination.getKey() );
        xmlDoc.setAttribute( "name", shortcutDestination.getName() );
        xmlDoc.setAttribute( "forward", menuItem.isShortcutForward() ? "true" : "false" );

        xmlDoc.endElement();
    }

    /**
     * Creates the <code>page</code> node, based on the page entity in the menuitem.
     *
     * @param xmlDoc   The XML which the page is added to.
     * @param menuItem The menu item that holds the page node.
     */
    private void addPageElement( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {

        PageEntity page = menuItem.getPage();
        if ( page == null )
        {
            return;
        }
        PageTemplateEntity pageTemplate = page.getTemplate();
        xmlDoc.startElement( "page" );
        xmlDoc.setAttribute( "key", page.getKey() );
        xmlDoc.setAttribute( "pagetemplatekey", Integer.toString( pageTemplate.getKey() ) );
        xmlDoc.setAttribute( "pagetemplate", pageTemplate.getName() );
        xmlDoc.setAttribute( "pagetemplatetype", pageTemplate.getType().getKey() );
        if ( includePageTemplateName )
        {
            xmlDoc.setAttribute( "pagetemplatename", pageTemplate.getName() );
        }
        xmlDoc.endElement();
    }

    /**
     * Gets the section of a menuItem, and if it's not <code>null</code>, it's added to the XML.
     *
     * @param xmlDoc   The XML document to add the section too.
     * @param menuItem The menuItem containing the section.
     */
    private void addSectionElement( XMLBuilder xmlDoc, MenuItemEntity menuItem )
    {
        if ( !menuItem.isSection() )
        {
            return;
        }
        xmlDoc.startElement( "section" );
        xmlDoc.setAttribute( "key", menuItem.getKey() );
        xmlDoc.setAttribute( "menuitemkey", menuItem.getKey() );
        xmlDoc.setAttribute( "menukey", menuItem.getSite().getKey().toString() );
        xmlDoc.setAttribute( "ordered", menuItem.isOrderedSection() ? "true" : "false" );

        xmlDoc.startElement( "contenttypes" );
        Set<ContentTypeEntity> contentTypes = menuItem.getContentTypeFilter();
        for ( ContentTypeEntity contentType : contentTypes )
        {
            xmlDoc.startElement( "contenttype" );
            xmlDoc.setAttribute( "key", contentType.getKey() );

            xmlDoc.startElement( "name" );
            xmlDoc.addContent( contentType.getName() );
            xmlDoc.endElement();

            xmlDoc.endElement();
        }
        xmlDoc.endElement();

        xmlDoc.endElement();
    }

    private void addMenuItemsElement( XMLBuilder xmlDoc, MenuItemEntity menuItem, boolean includeChildren, int menuItemLevelsWalked )
    {

        if ( !setting.includeChildren )
        {
            return;
        }

        xmlDoc.startElement( "menuitems" );
        xmlDoc.setAttribute( "istop", "no" );

        final Collection<MenuItemEntity> children = menuItem.getChildren();
        xmlDoc.setAttribute( "child-count", String.valueOf( countNumberOfAccessibleChildren( children ) ) );

        final boolean stillMoreLevelsToWalk = menuItemLevelsWalked + 1 <= setting.menuItemLevels;
        final boolean walkAllLevels = setting.menuItemLevels == 0;

        if ( includeChildren && ( walkAllLevels || stillMoreLevelsToWalk ) )
        {
            if ( !children.isEmpty() )
            {
                for ( MenuItemEntity child : children )
                {
                    if ( addable( child ) )
                    {
                        doAddMenuItemElement( xmlDoc, child, includeChildren, menuItemLevelsWalked );
                    }
                }
            }
        }
        xmlDoc.endElement();
    }

    private int countNumberOfAccessibleChildren( Collection<MenuItemEntity> children )
    {
        int numberOfAccessibleChildren = 0;
        for ( MenuItemEntity child : children )
        {
            if ( addable( child ) )
            {
                numberOfAccessibleChildren++;
            }
        }
        return numberOfAccessibleChildren;
    }

    protected boolean addable( MenuItemEntity menuItem )
    {
        return ( !menuItem.getHidden() || setting.includeHiddenMenuItems ) && access( menuItem );
    }

    protected boolean access( MenuItemEntity menuItem )
    {
        // Only apply access restrictions if a user exists:
        return ( setting.user == null ) || ( menuItemAccessResolver.hasAccess( setting.user, menuItem, MenuItemAccessType.READ ) );
    }

    private String asEmptyIfNull( final String value )
    {
        return value != null ? value : "";
    }

    public void setIncludeSiteInfo( boolean includeSiteInfo )
    {
        this.includeSiteInfo = includeSiteInfo;
    }

    public void setIncludePathInfo( boolean includePathInfo )
    {
        this.includePathInfo = includePathInfo;
    }

    public void setIncludePageTemplateName( boolean includePageTemplateName )
    {
        this.includePageTemplateName = includePageTemplateName;
    }
}
