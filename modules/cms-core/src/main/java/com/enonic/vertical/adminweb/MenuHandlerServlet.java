/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.servlet.http.HttpServletRequestWrapper;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.LanguageEntity;
import com.enonic.cms.core.LanguageKey;
import com.enonic.cms.core.LanguageResolver;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.resolver.ResolverContext;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.DefaultSiteAccessRightAccumulator;
import com.enonic.cms.core.structure.DefaultSiteAccumulatedAccessRights;
import com.enonic.cms.core.structure.RunAsType;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteXmlCreator;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessResolver;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessRightAccumulator;
import com.enonic.cms.core.structure.menuitem.MenuItemAccumulatedAccessRights;
import com.enonic.cms.core.structure.menuitem.MenuItemAndUserAccessRights;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemRequestParameter;
import com.enonic.cms.core.structure.menuitem.MenuItemSpecification;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.menuitem.MenuItemXMLCreatorSetting;
import com.enonic.cms.core.structure.menuitem.MenuItemXmlCreator;
import com.enonic.cms.core.structure.page.PageEntity;
import com.enonic.cms.core.structure.page.PageSpecification;
import com.enonic.cms.core.structure.page.PageWindowEntity;
import com.enonic.cms.core.structure.page.PageWindowKey;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.core.structure.portlet.PortletEntity;

import com.enonic.cms.business.DeploymentPathResolver;
import com.enonic.cms.business.portal.cache.PageCacheService;
import com.enonic.cms.business.portal.datasource.processor.ContentProcessor;
import com.enonic.cms.business.portal.datasource.processor.DataSourceProcessor;
import com.enonic.cms.business.portal.datasource.processor.MenuItemProcessor;
import com.enonic.cms.business.portal.rendering.PageRenderer;
import com.enonic.cms.business.portal.rendering.PageRendererContext;
import com.enonic.cms.business.portal.rendering.RegionsResolver;
import com.enonic.cms.business.preview.MenuItemPreviewContext;
import com.enonic.cms.business.preview.PreviewContext;

import com.enonic.cms.domain.admin.MenuItemsAcrossSitesModel;
import com.enonic.cms.domain.admin.MenuItemsAcrossSitesXmlCreator;
import com.enonic.cms.domain.portal.PageRequestType;
import com.enonic.cms.domain.portal.PrettyPathNameCreator;
import com.enonic.cms.domain.portal.rendering.RenderedPageResult;

public class MenuHandlerServlet
    extends AdminHandlerBaseServlet
{
    private final static int TYPE_NEWSLETTER = 4;

    private static final int ADMIN_PAGE_KEY = 850;

    private static final String ELEMENT_NAME_MENU_NAME = "menu-name";

    private static final String FORM_ITEM_MENU_NAME = "menu-name";

    private static final String FORM_ITEM_DISPLAY_NAME = "displayname";

    private static final String ELEMENT_NAME_DISPLAY_NAME = "displayname";

    private void buildPageXML( ExtendedMap formItems, String xmlParams, Element menuItemElem )
        throws VerticalAdminException
    {

        Document doc = menuItemElem.getOwnerDocument();
        Element page = XMLTool.createElement( doc, menuItemElem, "page" );

        if ( formItems.containsKey( "pagekey" ) )
        {
            page.setAttribute( "key", formItems.getString( "pagekey" ) );
        }

        page.setAttribute( "pagetemplatekey", formItems.getString( "pagetemplatekey" ) );

        // page data
        XMLTool.createElement( doc, page, "pagedata" );

        Element contentObjects = XMLTool.createElement( doc, page, "contentobjects" );

        // Connect contentobjects
        Document paramsDoc = XMLTool.domparse( xmlParams );
        NodeList nodes = paramsDoc.getElementsByTagName( "pagetemplateparameter" );
        for ( int i = 0; i < nodes.getLength(); i++ )
        {
            Element parameter = (Element) nodes.item( i );
            String key = parameter.getAttribute( "key" );

            String paramName = XMLTool.getElementText( parameter, "name" );
            String separator = XMLTool.getElementText( parameter, "separator" );
            if ( !isArrayFormItem( formItems, paramName ) )
            {
                if ( formItems.containsKey( paramName ) )
                {
                    createContentObjectXML( doc, contentObjects, paramName, formItems.getString( paramName ),
                                            formItems.getString( "pagekey", "" ), "0", key, separator );
                }
            }
            else
            {
                String[] keyArray = (String[]) formItems.get( paramName );

                for ( int j = 0; j < keyArray.length; j++ )
                {
                    if ( keyArray[j] != null && keyArray[j].trim().length() != 0 )
                    {
                        createContentObjectXML( doc, contentObjects, paramName, keyArray[j], formItems.getString( "pagekey", "" ),
                                                String.valueOf( j ), key, separator );
                    }
                }
            }
        }
    }

    private void createContentObjectXML( Document doc, Element contentObjects, String paramName, String key, String pageKey, String order,
                                         String paramKey, String separator )
    {

        Element contentObject = XMLTool.createElement( doc, contentObjects, "contentobject" );
        contentObject.setAttribute( "pagekey", pageKey );
        contentObject.setAttribute( "conobjkey", key );
        contentObject.setAttribute( "parameterkey", paramKey );
        XMLTool.createElement( doc, contentObject, "parametername", paramName );
        XMLTool.createElement( doc, contentObject, "separator", separator );

        XMLTool.createElement( doc, contentObject, "order", order );
    }

    private void createContentXML( ExtendedMap formItems, Element menuItemElem )
        throws VerticalAdminException
    {

        Document doc = menuItemElem.getOwnerDocument();

        // Create content element
        Element documentElem = XMLTool.createElement( doc, menuItemElem, "document" );

        if ( verticalProperties.isStoreXHTMLOn() )
        {
            XMLTool.createXHTMLNodes( doc, documentElem, formItems.getString( "contentdata_body", "" ), true );
        }
        else
        {
            XMLTool.createCDATASection( doc, documentElem, formItems.getString( "contentdata_body", "" ) );
        }
    }

    private void buildSectionXML( Element menuItemElem, ExtendedMap formItems )
    {
        Element sectionElem = XMLTool.createElement( menuItemElem, "section" );

        // ordered
        boolean ordered = "true".equals( formItems.getString( "section_ordered" ) );
        sectionElem.setAttribute( "ordered", String.valueOf( ordered ) );

        // Content types
        Element ctyElem = XMLTool.createElement( sectionElem, "contenttypes" );
        String[] contentTypeKeys = formItems.getStringArray( "contenttypekey" );
        for ( String contentTypeKey : contentTypeKeys )
        {
            XMLTool.createElement( ctyElem, "contenttype" ).setAttribute( "key", contentTypeKey );
        }
    }

    private void buildShortcutXML( Element menuItemElem, ExtendedMap formItems )
    {
        Element shortcutElem = XMLTool.createElement( menuItemElem, "shortcut" );
        int shortcut = formItems.getInt( "shortcut" );
        shortcutElem.setAttribute( "key", String.valueOf( shortcut ) );
        boolean forwardShortcut = formItems.getBoolean( "forward_shortcut", false );
        shortcutElem.setAttribute( "forward", String.valueOf( !forwardShortcut ) );
    }

    private void createURLXML( Element menuitem_elem, ExtendedMap formItems )
    {
        Element url_elem = XMLTool.createElement( menuitem_elem.getOwnerDocument(), menuitem_elem, "url", (String) formItems.get( "url" ) );

        url_elem.setAttribute( "newwindow", (String) formItems.get( "newwindow" ) );
        url_elem.setAttribute( "local", (String) formItems.get( "islocalurl" ) );
    }

    private Document formItemsToMenuItem( AdminService admin, Element menuItemElem, ExtendedMap formItems, int menuKey, int menuItemKey )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        int order;
        String owner;
        int parentKey = -1;

        if ( menuItemElem != null )
        {
            // update operation
            order = Integer.parseInt( menuItemElem.getAttribute( "order" ) );
            owner = menuItemElem.getAttribute( "owner" );
            String tmp = menuItemElem.getAttribute( "parent" );
            if ( tmp.length() > 0 )
            {
                parentKey = Integer.parseInt( tmp );
            }
        }
        else
        {
            // create operation
            order = -1;
            owner = user.getKey().toString();
            parentKey = formItems.getInt( "insertbelow", -1 );
        }

        Document newDoc = XMLTool.createDocument( "menuitems" );
        Element menuItemsElement = newDoc.getDocumentElement();
        Element menuItemElement = XMLTool.createElement( newDoc, menuItemsElement, "menuitem" );

        if ( menuItemKey != -1 )
        {
            menuItemElement.setAttribute( "key", String.valueOf( menuItemKey ) );
        }
        if ( parentKey != -1 )
        {
            menuItemElement.setAttribute( "parent", String.valueOf( parentKey ) );
        }
        if ( order != -1 )
        {
            menuItemElement.setAttribute( "order", String.valueOf( order ) );
        }
        menuItemElement.setAttribute( "menukey", String.valueOf( menuKey ) );

        // owner
        menuItemElement.setAttribute( "owner", owner );

        // modifier
        menuItemElement.setAttribute( "modifier", String.valueOf( user.getKey() ) );

        // // create default xml
        String type = (String) formItems.get( "type" );
        // automatically treat a form as a page
        boolean hasForm = false;
        if ( type.equals( "form" ) )
        {
            hasForm = true;
            type = "content";
        }
        else if ( type.equals( "sectionpage" ) )
        {
            type = "content";
        }
        else if ( type.equals( "newsletter" ) )
        {
            type = "content";
        }
        else if ( type.equals( "shortcut" ) )
        {
            type = "shortcut";
        }
        else if ( "localurl".equals( type ) || "externalurl".equals( type ) )
        {
            type = "url";
        }

        // typechange?
        if ( menuItemElem != null )
        {
            String tmp2 = menuItemElem.getAttribute( "type" );
            if ( !type.equals( tmp2 ) && tmp2.length() > 0 )
            {
                menuItemElement.setAttribute( "typechanged", tmp2 );
            }
        }

        // mark it as modified:
        menuItemElement.setAttribute( "modified", "modified" );

        // set type
        menuItemElement.setAttribute( "type", type );

        // set runAs
        menuItemElement.setAttribute( "runAs", formItems.getString( "runAs", "" ) );

        // Find name-items
        String menuItemName = formItems.getString( "name", "" );
        String displayName = formItems.getString( FORM_ITEM_DISPLAY_NAME );
        String menuName = formItems.getString( FORM_ITEM_MENU_NAME, null );

        menuItemName = ensureOrGenerateMenuItemName( menuItemName, displayName, menuName );

        // set menuItemName
        XMLTool.createElement( newDoc, menuItemElement, "name", menuItemName );

        // set displayname
        XMLTool.createElement( newDoc, menuItemElement, ELEMENT_NAME_DISPLAY_NAME, displayName );

        // set menu-name
        if ( StringUtils.isNotEmpty( menuName ) )
        {
            XMLTool.createElement( newDoc, menuItemElement, ELEMENT_NAME_MENU_NAME, menuName );
        }
        // set description
        String description = formItems.getString( "description", "" );
        XMLTool.createElement( newDoc, menuItemElement, "description", description );

        // set keywords
        String keywords = formItems.getString( "keywords", "" );
        if ( keywords.length() > 0 )
        {
            XMLTool.createElement( newDoc, menuItemElement, "keywords", keywords );
        }

        // set language
        String lanKey = formItems.getString( "languagekey", null );
        if ( lanKey != null )
        {
            menuItemElement.setAttribute( "languagekey", lanKey );
        }

        // set visibility:
        if ( "on".equals( formItems.getString( "visibility", null ) ) )
        {
            menuItemElement.setAttribute( "visible", "yes" );
        }
        else
        {
            menuItemElement.setAttribute( "visible", "no" );
        }

        if ( isArrayFormItem( formItems, "paramname" ) )
        {
            // there are multiple parameters
            String[] paramNames = (String[]) formItems.get( "paramname" );
            String[] paramVals = (String[]) formItems.get( "paramval" );
            String[] paramOverrides = (String[]) formItems.get( "paramoverride" );

            Element paramElem = XMLTool.createElement( newDoc, menuItemElement, "parameters" );
            for ( int i = 0; i < paramNames.length; i++ )
            {
                if ( paramNames[i].length() == 0 || paramVals[i].length() == 0 )
                {
                    continue;
                }
                Element elem = XMLTool.createElement( newDoc, paramElem, "parameter", paramVals[i] );
                elem.setAttribute( "name", paramNames[i] );
                elem.setAttribute( "override", paramOverrides[i] );
            }
        }
        else
        {
            // there is only one (or zero) parameter
            String paramName = formItems.getString( "paramname", null );
            String paramVal = formItems.getString( "paramval", null );
            String paramOverride = formItems.getString( "paramoverride", null );

            Element paramElem = XMLTool.createElement( newDoc, menuItemElement, "parameters" );
            if ( paramName != null || paramVal != null )
            {
                Element elem = XMLTool.createElement( newDoc, paramElem, "parameter", paramVal );
                elem.setAttribute( "name", paramName );
                elem.setAttribute( "override", paramOverride );
            }
        }

        Element dataElement = XMLTool.createElement( newDoc, menuItemElement, "data" );

        // create XML DOM for each menu item type:
        if ( "url".equals( type ) || "localurl".equals( type ) )
        {
            createURLXML( menuItemElement, formItems );
        }
        else if ( "section".equals( type ) )
        {
            buildSectionXML( menuItemElement, formItems );
        }
        else if ( "shortcut".equals( type ) )
        {
            buildShortcutXML( menuItemElement, formItems );
        }
        else if ( "page".equals( type ) )
        {
            int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
            String xmlParams = admin.getPageTemplParams( pageTemplateKey );

            buildPageXML( formItems, xmlParams, menuItemElement );
        }
        else if ( "content".equals( type ) )
        {
            int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
            PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey );
            PageTemplateType pageTemplateType = pageTemplate.getType();

            if ( pageTemplateType == PageTemplateType.SECTIONPAGE || pageTemplateType == PageTemplateType.NEWSLETTER )
            {
                buildSectionXML( menuItemElement, formItems );
            }

            if ( formItems.containsKey( "contentkey" ) )
            {
                menuItemElement.setAttribute( "contentkey", String.valueOf( formItems.getInt( "contentkey" ) ) );
            }

            createContentXML( formItems, menuItemElement );

            String xmlParams;
            xmlParams = admin.getPageTemplParams( formItems.getInt( "pagetemplatekey" ) );

            buildPageXML( formItems, xmlParams, menuItemElement );

            if ( hasForm )
            {
                buildFormXML( formItems, dataElement );
            }
        }

        // caching
        String cacheType = formItems.getString( "cachetype" );
        if ( !"off".equals( cacheType ) )
        {
            dataElement.setAttribute( "cachedisabled", "false" );

            // cache type
            dataElement.setAttribute( "cachetype", cacheType );

            if ( cacheType.equals( "specified" ) )
            {
                dataElement.setAttribute( "mincachetime", formItems.getString( "mincachetime" ) );
            }
        }
        else
        {
            dataElement.setAttribute( "cachedisabled", "true" );
        }

        return newDoc;
    }


    /**
     * RMY 25/01-2010
     * <p/>
     * This is _nasty_ !
     * <p/>
     * To prevent last-minute bugs, this is just copied from formItemsToMenuItem (which is used for update and create menuItem)
     * This should rewritten be a separate builder for previewMenuItemXml, and remove all old-style stuff
     */
    private Document createPreviewMenuItemXml( AdminService admin, Element menuItemElem, ExtendedMap formItems, int menuKey,
                                               int menuItemKey )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        int order;
        String owner;
        int parentKey = -1;

        if ( menuItemElem != null )
        {
            // update operation
            order = Integer.parseInt( menuItemElem.getAttribute( "order" ) );
            owner = menuItemElem.getAttribute( "owner" );
            String tmp = menuItemElem.getAttribute( "parent" );
            if ( tmp.length() > 0 )
            {
                parentKey = Integer.parseInt( tmp );
            }
        }
        else
        {
            // create operation
            order = -1;
            owner = user.getKey().toString();
            parentKey = formItems.getInt( "insertbelow", -1 );
        }

        Document newDoc = XMLTool.createDocument( "menuitems" );
        Element menuItemsElement = newDoc.getDocumentElement();
        Element menuItemElement = XMLTool.createElement( newDoc, menuItemsElement, "menuitem" );

        if ( menuItemKey != -1 )
        {
            menuItemElement.setAttribute( "key", String.valueOf( menuItemKey ) );
        }
        if ( parentKey != -1 )
        {
            menuItemElement.setAttribute( "parent", String.valueOf( parentKey ) );
        }
        if ( order != -1 )
        {
            menuItemElement.setAttribute( "order", String.valueOf( order ) );
        }
        menuItemElement.setAttribute( "menukey", String.valueOf( menuKey ) );

        // owner
        menuItemElement.setAttribute( "owner", owner );

        // modifier
        menuItemElement.setAttribute( "modifier", String.valueOf( user.getKey() ) );

        // // create default xml
        String type = (String) formItems.get( "type" );
        // automatically treat a form as a page
        boolean hasForm = false;
        if ( type.equals( "form" ) )
        {
            hasForm = true;
            type = "content";
        }
        else if ( type.equals( "sectionpage" ) )
        {
            type = "content";
        }
        else if ( type.equals( "newsletter" ) )
        {
            type = "content";
        }
        else if ( type.equals( "shortcut" ) )
        {
            type = "shortcut";
        }
        else if ( "localurl".equals( type ) || "externalurl".equals( type ) )
        {
            type = "url";
        }

        // typechange?
        if ( menuItemElem != null )
        {
            String tmp2 = menuItemElem.getAttribute( "type" );
            if ( !type.equals( tmp2 ) && tmp2.length() > 0 )
            {
                menuItemElement.setAttribute( "typechanged", tmp2 );
            }
        }

        // mark it as modified:
        menuItemElement.setAttribute( "modified", "modified" );

        // set type
        menuItemElement.setAttribute( "type", type );

        // set runAs
        menuItemElement.setAttribute( "runAs", formItems.getString( "runAs", "" ) );

        // Find name-items
        String menuItemName = formItems.getString( "name", "" );
        String displayName = formItems.getString( FORM_ITEM_DISPLAY_NAME );
        String menuName = formItems.getString( FORM_ITEM_MENU_NAME, null );

        menuItemName = ensureOrGenerateMenuItemName( menuItemName, displayName, menuName );

        // set menuItemName
        XMLTool.createElement( newDoc, menuItemElement, "name", menuItemName );

        // set displayname
        // This is an element thats different named internally and in the portal, the root to this change
        XMLTool.createElement( newDoc, menuItemElement, "display-name", displayName );

        // set menu-name
        if ( StringUtils.isNotEmpty( menuName ) )
        {
            XMLTool.createElement( newDoc, menuItemElement, ELEMENT_NAME_MENU_NAME, menuName );
        }
        // set description
        String description = formItems.getString( "description", "" );
        XMLTool.createElement( newDoc, menuItemElement, "description", description );

        // set keywords
        String keywords = formItems.getString( "keywords", "" );
        if ( keywords.length() > 0 )
        {
            XMLTool.createElement( newDoc, menuItemElement, "keywords", keywords );
        }

        // set language
        String lanKey = formItems.getString( "languagekey", null );
        if ( lanKey != null )
        {
            menuItemElement.setAttribute( "languagekey", lanKey );
        }

        // set visibility:
        if ( "on".equals( formItems.getString( "visibility", null ) ) )
        {
            menuItemElement.setAttribute( "visible", "yes" );
        }
        else
        {
            menuItemElement.setAttribute( "visible", "no" );
        }

        if ( isArrayFormItem( formItems, "paramname" ) )
        {
            // there are multiple parameters
            String[] paramNames = (String[]) formItems.get( "paramname" );
            String[] paramVals = (String[]) formItems.get( "paramval" );
            String[] paramOverrides = (String[]) formItems.get( "paramoverride" );

            Element paramElem = XMLTool.createElement( newDoc, menuItemElement, "parameters" );
            for ( int i = 0; i < paramNames.length; i++ )
            {
                if ( paramNames[i].length() == 0 || paramVals[i].length() == 0 )
                {
                    continue;
                }
                Element elem = XMLTool.createElement( newDoc, paramElem, "parameter", paramVals[i] );
                elem.setAttribute( "name", paramNames[i] );
                elem.setAttribute( "override", paramOverrides[i] );
            }
        }
        else
        {
            // there is only one (or zero) parameter
            String paramName = formItems.getString( "paramname", null );
            String paramVal = formItems.getString( "paramval", null );
            String paramOverride = formItems.getString( "paramoverride", null );

            Element paramElem = XMLTool.createElement( newDoc, menuItemElement, "parameters" );
            if ( paramName != null || paramVal != null )
            {
                Element elem = XMLTool.createElement( newDoc, paramElem, "parameter", paramVal );
                elem.setAttribute( "name", paramName );
                elem.setAttribute( "override", paramOverride );
            }
        }

        Element dataElement = XMLTool.createElement( newDoc, menuItemElement, "data" );

        // create XML DOM for each menu item type:
        if ( "url".equals( type ) || "localurl".equals( type ) )
        {
            createURLXML( menuItemElement, formItems );
        }
        else if ( "section".equals( type ) )
        {
            buildSectionXML( menuItemElement, formItems );
        }
        else if ( "shortcut".equals( type ) )
        {
            buildShortcutXML( menuItemElement, formItems );
        }
        else if ( "page".equals( type ) )
        {
            int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
            String xmlParams = admin.getPageTemplParams( pageTemplateKey );

            buildPageXML( formItems, xmlParams, menuItemElement );
        }
        else if ( "content".equals( type ) )
        {
            int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
            PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey );
            PageTemplateType pageTemplateType = pageTemplate.getType();
            if ( pageTemplateType == PageTemplateType.SECTIONPAGE || pageTemplateType == PageTemplateType.NEWSLETTER )
            {
                buildSectionXML( menuItemElement, formItems );
            }

            if ( formItems.containsKey( "contentkey" ) )
            {
                menuItemElement.setAttribute( "contentkey", String.valueOf( formItems.getInt( "contentkey" ) ) );
            }

            createContentXML( formItems, menuItemElement );

            String xmlParams;
            xmlParams = admin.getPageTemplParams( formItems.getInt( "pagetemplatekey" ) );

            buildPageXML( formItems, xmlParams, menuItemElement );

            if ( hasForm )
            {
                buildFormXML( formItems, dataElement );
            }
        }

        // caching
        String cacheType = formItems.getString( "cachetype" );
        if ( !"off".equals( cacheType ) )
        {
            dataElement.setAttribute( "cachedisabled", "false" );

            // cache type
            dataElement.setAttribute( "cachetype", cacheType );

            if ( cacheType.equals( "specified" ) )
            {
                dataElement.setAttribute( "mincachetime", formItems.getString( "mincachetime" ) );
            }
        }
        else
        {
            dataElement.setAttribute( "cachedisabled", "true" );
        }

        return newDoc;
    }
    // wrapper class for field data. contains the data in a string array, and a counter.

    private final static class FieldData
    {
        String[] data = null;

        int counter = 0;

        FieldData( String[] data )
        {
            this.data = data != null ? data : new String[0];
        }

        String popData()
        {
            if ( this.counter < this.data.length )
            {
                return this.data[this.counter++];
            }
            else
            {
                return "";
            }
        }
    }

    private void buildFormXML( ExtendedMap formItems, Element menuItemElement )
        throws VerticalAdminException
    {

        Document doc = menuItemElement.getOwnerDocument();
        Element formElement = XMLTool.createElement( doc, menuItemElement, "form" );

        XMLTool.createElement( doc, formElement, "title", formItems.getString( "form_title" ) );

        Element confirmationElement = XMLTool.createElement( doc, formElement, "confirmation" );
        XMLTool.createXHTMLNodes( doc, confirmationElement, formItems.getString( "form_confirmation", "" ), true );

        // category key:
        if ( formItems.containsKey( "category_key" ) )
        {
            formElement.setAttribute( "categorykey", formItems.getString( "category_key" ) );
        }

        // recipient
        if ( formItems.containsKey( "form_sendto" ) )
        {
            String[] recipients = formItems.getStringArray( "form_sendto" );

            Element recipientsElement = XMLTool.createElement( doc, formElement, "recipients" );
            for ( String recipient : recipients )
            {
                if ( recipient.trim().length() > 0 )
                {
                    XMLTool.createElement( doc, recipientsElement, "e-mail", recipient );
                }
            }
        }

        // receipt

        Element receiptElement = XMLTool.createElement( doc, formElement, "receipt" );

        if ( formItems.containsKey( "receiptSendEmail" ) )
        {
            XMLTool.createElement( doc, receiptElement, "sendreceipt", "yes" );
        }
        else
        {
            XMLTool.createElement( doc, receiptElement, "sendreceipt", "no" );
        }

        String name = formItems.getString( "receiptFromName", "" );
        XMLTool.createElement( doc, receiptElement, "name", name );

        String email = formItems.getString( "receiptFromAddress", "" );
        XMLTool.createElement( doc, receiptElement, "email", email );

        String subject = formItems.getString( "receiptSubject", "" );
        XMLTool.createElement( doc, receiptElement, "subject", subject );

        String message = formItems.getString( "receiptMessage", "" );
        XMLTool.createElement( doc, receiptElement, "message", message );

        if ( formItems.containsKey( "receiptIncludeSubmittedFormData" ) )
        {
            XMLTool.createElement( doc, receiptElement, "includeform", "yes" );
        }
        else
        {
            XMLTool.createElement( doc, receiptElement, "includeform", "no" );
        }

        // build form XML
        FieldData labels = new FieldData( formItems.getStringArray( "field_label" ) );
        FieldData helpTexts = new FieldData( formItems.getStringArray( "field_helptext" ) );
        FieldData types = new FieldData( formItems.getStringArray( "field_type" ) );

        FieldData required = new FieldData( formItems.getStringArray( "field_required" ) );
        FieldData width = new FieldData( formItems.getStringArray( "field_width" ) );
        FieldData height = new FieldData( formItems.getStringArray( "field_height" ) );
        FieldData defaultValue = new FieldData( formItems.getStringArray( "field_defaultvalue" ) );
        FieldData regexp = new FieldData( formItems.getStringArray( "field_regexp" ) );
        FieldData validationType = new FieldData( formItems.getStringArray( "field_validation" ) );
        FieldData valueCount = new FieldData( formItems.getStringArray( "field_count" ) );
        FieldData checkedIndex = new FieldData( formItems.getStringArray( "field_checkedindex" ) );
        FieldData value = new FieldData( formItems.getStringArray( "field_value" ) );

        FieldData defaultChk = new FieldData( formItems.getStringArray( "field_defaultchk" ) );

        for ( int i = 0; i < types.data.length; i++ )
        {
            String type = types.popData();

            Element itemElement = XMLTool.createElement( doc, formElement, "item" );
            itemElement.setAttribute( "type", type );

            String tmp;
            if ( ( tmp = labels.popData() ).length() != 0 )
            {
                itemElement.setAttribute( "label", tmp );
            }

            if ( ( tmp = helpTexts.popData() ).length() != 0 )
            {
                XMLTool.createElement( doc, itemElement, "help", tmp );
            }

            // data common for text, textarea and checkbox
            if ( "text".equals( type ) || "textarea".equals( type ) || "fileattachment".equals( type ) || "checkbox".equals( type ) )
            {
                // required
                itemElement.setAttribute( "required", required.popData() );
            }

            // data common for text and textarea
            if ( "text".equals( type ) || "textarea".equals( type ) || "fileattachment".equals( type ) )
            {
                // width
                if ( !( "fileattachment".equals( type ) ) && ( ( tmp = width.popData() ).length() > 0 ) )
                {
                    itemElement.setAttribute( "width", tmp );
                }
            }

            // textarea specific data:
            if ( "textarea".equals( type ) )
            {
                // height
                if ( ( tmp = height.popData() ).length() > 0 )
                {
                    itemElement.setAttribute( "height", tmp );
                }
            }

            // text specific data:
            else if ( "text".equals( type ) )
            {
                // default value
                if ( ( tmp = defaultValue.popData() ).length() > 0 )
                {
                    XMLTool.createElement( doc, itemElement, "data", tmp );
                }

                // regexp validation
                String validation_type = validationType.popData();
                if ( ( tmp = regexp.popData() ).length() > 0 )
                {
                    itemElement.setAttribute( "validation", tmp );
                    itemElement.setAttribute( "validationtype", validation_type );
                }

                String label = itemElement.getAttribute( "label" );
                if ( label.equals( formItems.getString( "field_form_title", null ) ) )
                {
                    itemElement.setAttribute( "title", "true" );
                }

                if ( label.equals( formItems.getString( "field_form_fromname", null ) ) )
                {
                    itemElement.setAttribute( "fromname", "true" );
                }

                if ( label.equals( formItems.getString( "field_form_fromemail", null ) ) )
                {
                    itemElement.setAttribute( "fromemail", "true" );
                }
            }

            // fileattachment specific data:
            else if ( "fileattachment".equals( type ) )
            {
                String label = itemElement.getAttribute( "label" );
                if ( label.equals( formItems.getString( "field_form_title", null ) ) )
                {
                    itemElement.setAttribute( "title", "true" );
                }
            }

            // checkbox specific data:
            else if ( "checkbox".equals( type ) )
            {
                // default value
                if ( defaultValue.popData().equals( "checked" ) )
                {
                    itemElement.setAttribute( "default", "checked" );
                }
            }

            // radiobuttons and dropdown specific data:
            else if ( "radiobuttons".equals( type ) || "dropdown".equals( type ) )
            {
                // required
                itemElement.setAttribute( "required", required.popData() );

                Element dataElement = XMLTool.createElement( doc, itemElement, "data" );
                int count = Integer.parseInt( valueCount.popData() );
                int checkedIdx = Integer.parseInt( checkedIndex.popData() );

                for ( int j = 0; j < count; j++ )
                {
                    String val = value.popData();

                    if ( val.length() > 0 )
                    {
                        Element optionElement = XMLTool.createElement( doc, dataElement, "option" );
                        optionElement.setAttribute( "value", val );

                        if ( j == checkedIdx )
                        {
                            optionElement.setAttribute( "default", "true" );
                        }
                    }
                }
            }

            // checkboxes specific data:
            else if ( "checkboxes".equals( type ) )
            {
                Element dataElement = XMLTool.createElement( doc, itemElement, "data" );
                int count = Integer.parseInt( valueCount.popData() );

                for ( int j = 0; j < count; j++ )
                {
                    String val = value.popData();

                    if ( val.length() > 0 )
                    {
                        Element optionElement = XMLTool.createElement( doc, dataElement, "option" );
                        optionElement.setAttribute( "value", val );

                        tmp = defaultChk.popData();
                        if ( tmp.equals( "1" ) )
                        {
                            optionElement.setAttribute( "default", "true" );
                        }
                    }
                    else
                    {
                        defaultChk.popData();
                    }

                }
            }

            // from email specific data:
            else if ( "fromemail".equals( type ) )
            {
                // default value
                if ( ( tmp = defaultValue.popData() ).length() > 0 )
                {
                    XMLTool.createElement( doc, itemElement, "data", tmp );
                }

                // email validation
                itemElement.setAttribute( "validation", "^.+@.+..+$" );
                itemElement.setAttribute( "validationtype", "email" );

                String label = itemElement.getAttribute( "label" );
                if ( label.equals( formItems.getString( "field_form_title", null ) ) )
                {
                    itemElement.setAttribute( "title", "true" );
                }
            }

        }
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        UserEntity userEntity = securityService.getUser( user );

        int menuItemKeyInt = formItems.getInt( "parentmi", -1 );

        MenuItemKey menuItemKey = menuItemKeyInt >= 0 ? new MenuItemKey( menuItemKeyInt ) : null;
        MenuItemEntity menuItem = menuItemKey != null ? menuItemDao.findByKey( menuItemKey ) : null;

        int menuKey = formItems.getInt( "menukey", -1 );
        if ( menuItem != null && menuKey == -1 )
        {
            menuKey = menuItem.getSite().getKey().toInt();
        }

        if ( menuItemKey == null )
        {
            // do nothing?
        }
        else if ( formItems.containsKey( "browsemode" ) )
        {
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, "browsemode" + menuItemKey, formItems.getString( "browsemode" ), -1, deploymentPath );
        }
        else
        {
            Cookie c = CookieUtil.getCookie( request, "browsemode" + menuItemKey );
            // if (c != null && "section".equals(c.getValue())) {
            if ( c == null || !"menuitem".equals( c.getValue() ) )
            {
                if ( menuItem.isSection() )
                {
                    redirectToSectionBrowse( request, response, menuKey, menuItemKey, formItems.getBoolean( "reload", false ) );
                }
            }
        }

        Document doc;
        String keepXML = request.getParameter( "keepxml" );
        String menuXML;

        if ( "yes".equals( keepXML ) )
        {
            menuXML = (String) session.getAttribute( "menuxml" );
            doc = XMLTool.domparse( menuXML );
        }
        else
        {
            XMLDocument xmlDocument = buildModelForBrowse( userEntity, menuKey, menuItemKey );
            doc = xmlDocument.getAsDOMDocument();
            menuXML = xmlDocument.getAsString();
            session.setAttribute( "menuxml", menuXML );
        }

        // Parameters
        ExtendedMap parameters = formItems;
        addCommonParameters( admin, user, request, parameters, -1, menuKey );
        if ( "yes".equals( keepXML ) )
        {
            parameters.put( "changed", "yep" );
        }

        parameters.put( "debugpath", getSiteUrl( request, menuKey ) );
        transformXML( request, response, doc, "menu_view.xsl", parameters );
    }

    private XMLDocument buildModelForBrowse( UserEntity user, int menuKey, MenuItemKey menuItemKey )
    {
        SiteKey siteKey = new SiteKey( menuKey );
        MenuBrowseModelFactory menuBrowseModelFactory =
            new MenuBrowseModelFactory( securityService, siteDao, menuItemDao, sitePropertiesService );
        MenuBrowseMenuItemsModel model = menuBrowseModelFactory.createMenuItemModel( user, siteKey, menuItemKey );
        return model.toXML();
    }

    public static String getSiteUrl( HttpServletRequest request, int menuKey )
    {
        String sitePath = AdminHelper.getDebugPath( request, new SiteKey( menuKey ) );

        if ( !sitePath.endsWith( "/" ) )
        {
            sitePath = sitePath + "/";
        }

        return sitePath;
    }

    public void handlerCustom( javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response,
                               HttpSession session, AdminService admin, ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( operation.equals( "edit" ) )
        {
            menuItemForm( request, response, session, admin, formItems );
        }
        else if ( operation.equals( "insert" ) )
        {
            insertMenuItem( request, response, session, admin, formItems );
        }
        else if ( operation.equals( "moveup" ) )
        {
            moveMenuItemUp( request, response, session, formItems );
        }
        else if ( operation.equals( "movedown" ) )
        {
            moveMenuItemDown( request, response, session, formItems );
        }
        else if ( operation.equals( "removeitem" ) )
        {
            handlerRemoveItem( request, response, admin, formItems );
        }
        else if ( operation.equals( "movebelow" ) )
        {
            moveMenuItemBelow( request, response, session, formItems );
        }
        else if ( operation.equals( "selectnewparent" ) )
        {
            selectNewParent( request, response, session, admin, formItems );
        }
        else if ( operation.equals( "menuitem_selector_multisite" ) )
        {
            handlerMultiSitePicker( request, response, session, admin, formItems );
        }
        else if ( operation.equals( "setup" ) )
        {
            handlerSetup( request, response, session, admin, formItems );
        }
        else if ( "propagateaccessrights".equals( operation ) )
        {
            handlerPropagateAccessRights( request, response, admin, formItems );
        }
        else if ( "formbuilder".equals( operation ) )
        {
            handlerFormBuilder( response, session, formItems );
        }
        else if ( "menuitem_selector_across_sites".equals( operation ) )
        {
            handlerMenuItemSelectorAcrossSites( request, response, session, admin, formItems );
        }
        else
        {
            String message = "Unknown operation: %0";
            VerticalAdminLogger.errorAdmin(message, null );
        }
    }

    private void moveMenuItemBelow( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException
    {

        int menuItemKeyInt = formItems.getInt( "key" );
        MenuItemEntity menuItemToMove = menuItemDao.findByKey( menuItemKeyInt );
        String str_oldParentKey = "-1";
        if ( menuItemToMove.getParent() != null )
        {
            str_oldParentKey = menuItemToMove.getParent().getMenuItemKey().toString();
        }

        int parentKey = formItems.getInt( "belowkey" );

        MenuItemKey menuItemParentToMoveBelow = null;
        if ( parentKey != -1 )
        {
            menuItemParentToMoveBelow = new MenuItemKey( parentKey );
        }

        User user = securityService.getLoggedInAdminConsoleUser();
        UserEntity userEntity = securityService.getUser( user.getKey() );

        SiteKey siteKey = new SiteKey( formItems.getString( "menukey" ) );
        MenuBrowseModelFactory menuBrowseModelFactory =
            new MenuBrowseModelFactory( securityService, siteDao, menuItemDao, sitePropertiesService );
        MenuBrowseMenuItemsModel model = menuBrowseModelFactory.createMenuItemModel( userEntity, siteKey, menuItemParentToMoveBelow );

        // add the moving menuitem as a bottom child of the new parent
        List<MenuItemAndUserAccessRights> menuItemsToListWithMovingMenuItemAdded =
            new ArrayList<MenuItemAndUserAccessRights>( model.getMenuItemsToList() );
        MenuItemAccessRightAccumulator menuItemAccessRightAccumulator = new MenuItemAccessRightAccumulator( securityService );
        MenuItemAccumulatedAccessRights menuItemAccessRightsForAnonymousUser =
            menuItemAccessRightAccumulator.getAccessRightsAccumulated( menuItemToMove,
                                                                       securityService.getUser( securityService.getAnonymousUserKey() ) );
        MenuItemAccumulatedAccessRights menuItemAccessRightsForUser =
            menuItemAccessRightAccumulator.getAccessRightsAccumulated( menuItemToMove, userEntity );
        MenuItemAndUserAccessRights menuItemAndUserAccessRightsForMenuItemToMove =
            new MenuItemAndUserAccessRights( menuItemToMove, menuItemAccessRightsForUser, menuItemAccessRightsForAnonymousUser );
        menuItemsToListWithMovingMenuItemAdded.add( menuItemAndUserAccessRightsForMenuItemToMove );
        model.setMenuItemsToList( menuItemsToListWithMovingMenuItemAdded );

        session.setAttribute( "menuxml", model.toXML().getAsString() );

        ExtendedMap params = new ExtendedMap();
        params.put( "page", formItems.get( "page" ) );
        params.put( "op", "browse" );
        params.put( "keepxml", "yes" );
        params.put( "highlight", formItems.get( "key" ) );
        params.put( "subop", "movebelow" );
        params.put( "move_menuitem", formItems.get( "key" ) );
        params.put( "move_from_parent", str_oldParentKey );
        params.putInt( "move_to_parent", parentKey );
        params.put( "menukey", formItems.get( "menukey" ) );
        params.put( "parentmi", formItems.get( "belowkey" ) );
        params.put( "browsemode", "menuitem" );
        redirectClientToAdminPath( "adminpage", params, request, response );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        menuItemForm( request, response, session, admin, formItems );
    }

    private void handlerRemoveItem( HttpServletRequest request, HttpServletResponse response, AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        MenuItemKey menuItemKey = new MenuItemKey( request.getParameter( "key" ) );

        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey.toInt() );

        MenuItemKey parentMenuItemKey = menuItem.getParent() != null ? menuItem.getParent().getMenuItemKey() : null;
        SiteKey siteKey = menuItem.getSite().getKey();

        admin.removeMenuItem( user, menuItemKey.toInt() );

        PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
        pageCacheService.removeEntriesByMenuItem( menuItemKey );

        formItems.put( "page", ADMIN_PAGE_KEY );
        formItems.put( "insertbelow", parentMenuItemKey != null ? parentMenuItemKey.toInt() : "-1" );
        formItems.put( "reload", true );
        redirectToBrowse( request, response, formItems );
    }

    public void handlerMenuItemSelectorAcrossSites( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                                    AdminService admin, ExtendedMap formItems )
    {
        User user = securityService.getLoggedInAdminConsoleUser();

        try
        {
            MenuItemType menuItemTypeRestriction = null;
            if ( StringUtils.isNotBlank( formItems.getString( "menuItemTypeRestriction", null ) ) )
            {
                menuItemTypeRestriction = MenuItemType.get( formItems.getInt( "menuItemTypeRestriction" ) );
            }
            PageTemplateType pageTemplateTypeRestriction = null;
            if ( StringUtils.isNotBlank( formItems.getString( "menuItemPageTemplateTypeRestriction", null ) ) )
            {
                pageTemplateTypeRestriction = PageTemplateType.get( formItems.getInt( "menuItemPageTemplateTypeRestriction" ) );
            }

            PageTemplateSpecification pageTemplateSpecification = new PageTemplateSpecification();
            pageTemplateSpecification.setType( pageTemplateTypeRestriction );
            PageSpecification pageSpecification = new PageSpecification();
            pageSpecification.setTemplateSpecification( pageTemplateSpecification );
            MenuItemSpecification menuItemSpecification = new MenuItemSpecification();
            menuItemSpecification.setType( menuItemTypeRestriction );
            menuItemSpecification.setPageSpecification( pageSpecification );
            List<MenuItemEntity> menuItemsAcrossSites = menuItemDao.findBySpecification( menuItemSpecification );

            MenuItemsAcrossSitesModel menuItemsAcrossSitesModel = new MenuItemsAcrossSitesModel();
            menuItemsAcrossSitesModel.addMenuItems( menuItemsAcrossSites );
            MenuItemsAcrossSitesXmlCreator menuItemsAcrossSitesXmlCreator = new MenuItemsAcrossSitesXmlCreator();

            XMLDocument menuitemsAcrossSitesXMLDocument =
                XMLDocumentFactory.create( menuItemsAcrossSitesXmlCreator.createXmlDocument( menuItemsAcrossSitesModel ) );

            Map<String, Object> parameters = new HashMap<String, Object>();

            String tmp = formItems.getString( "callback", "" );
            if ( tmp != null && tmp.length() > 0 )
            {
                parameters.put( "callback", tmp );
            }
            else
            {
                parameters.put( "callback", "false" );
            }

            addCommonParameters( admin, user, request, parameters, -1, -1 );

            DOMSource xmlResource = new DOMSource( menuitemsAcrossSitesXMLDocument.getAsDOMDocument() );

            Source xslResource = AdminStore.getStylesheet( session, "menuitem_selector_across_sites.xsl" );

            transformXML( session, response.getWriter(), xmlResource, xslResource, parameters );
        }
        catch ( IOException ioe )
        {
            String message = "I/O error: %t";
            VerticalAdminLogger.error(message, ioe );
        }
        catch ( TransformerException te )
        {
            String message = "XSL transformer error: %t";
            VerticalAdminLogger.error(message, te );
        }
    }

    public boolean handlerSelect( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        try
        {

            int menuKey = formItems.getInt( "menukey" );

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put( "returnkey", formItems.getString( "returnkey" ) );
            parameters.put( "returnview", formItems.getString( "returnview" ) );
            String returnrow = formItems.getString( "returnrow", "" );
            if ( returnrow != null && returnrow.length() > 0 )
            {
                parameters.put( "returnrow", returnrow );
            }
            String tmp = formItems.getString( "callback", "" );
            if ( tmp != null && tmp.length() > 0 )
            {
                parameters.put( "callback", tmp );
            }
            else
            {
                parameters.put( "callback", "false" );
            }

            tmp = formItems.getString( "filter", null );
            if ( tmp != null && tmp.length() > 0 )
            {
                parameters.put( "filter", tmp );
            }

            parameters.put( "referer", formItems.getString( "referer", "" ) );

            addCommonParameters( admin, user, request, parameters, -1, menuKey );

            Document menuDocTemp = XMLTool.domparse( admin.getAdminMenu( user, menuKey ) );
            Element menuElemTemp = XMLTool.selectElement( menuDocTemp.getDocumentElement(), "menu[@key = '" + menuKey + "']" );
            Element[] menuItemElems = XMLTool.getElements( menuElemTemp );
            Document menuDoc = XMLTool.createDocument( "menutop" );
            Element menuTop = menuDoc.getDocumentElement();
            for ( int i = 0; i < menuItemElems.length; i++ )
            {
                menuTop.appendChild( menuDoc.importNode( menuItemElems[i], true ) );
            }

            DOMSource xmlSource = new DOMSource( menuDoc );
            Source xslSource = AdminStore.getStylesheet( session, "menuitem_selector.xsl" );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( IOException ioe )
        {
            String message = "I/O error: %t";
            VerticalAdminLogger.error(message, ioe );
        }
        catch ( TransformerException te )
        {
            String message = "XSL transformer error: %t";
            VerticalAdminLogger.error(message, te );
        }

        return true;
    }

    private void handlerSetup( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        UserEntity userEntity = securityService.getUser( user );
        int menuKey = formItems.getInt( "menukey" );

        String subOp = formItems.getString( "subop", null );
        if ( "save".equals( subOp ) )
        {
            int frontPageKey = formItems.getInt( "frontpage_key", -1 );
            int loginPageKey = formItems.getInt( "loginpage_key", -1 );
            int errorPageKey = formItems.getInt( "errorpage_key", -1 );
            int defaultPageTemplateKey = formItems.getInt( "patkey", -1 );
            admin.updateMenuDetails( menuKey, frontPageKey, loginPageKey, errorPageKey, defaultPageTemplateKey );

            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "op", "browse" );
            queryParams.put( "parentmi", -1 );
            queryParams.put( "menukey", menuKey );
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
        else
        {

            Document modelAsDOMDoc = buildModelForSetup( userEntity, menuKey );

            StreamSource xmlSource = new StreamSource( new StringReader( XMLTool.documentToString( modelAsDOMDoc ) ) );
            Source xslSource = AdminStore.getStylesheet( session, "menu_setup.xsl" );

            // Parameters
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            parameters.put( "menukey", String.valueOf( menuKey ) );

            try
            {
                transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
            }
            catch ( TransformerException te )
            {
                String message = "Transformer error while transforming XSL: %t";
                VerticalAdminLogger.errorAdmin(message, te );
            }
            catch ( IOException ioe )
            {
                String message = "I/O error: %t";
                VerticalAdminLogger.errorAdmin(message, ioe );
            }
        }
    }

    private Document buildModelForSetup( UserEntity user, int menuKey )
    {
        SiteEntity site = siteDao.findByKey( menuKey );

        org.jdom.Element modelEl = new org.jdom.Element( "model" );

        addSelectedMenuElement( modelEl, user, site );

        MenuItemXMLCreatorSetting miXmlCreatorSetting = new MenuItemXMLCreatorSetting();
        miXmlCreatorSetting.includeTypeSpecificXML = true;
        miXmlCreatorSetting.includeParents = false;
        miXmlCreatorSetting.includeChildren = false;
        miXmlCreatorSetting.user = user;

        MenuItemAccessResolver menuItemAccessResolver = new MenuItemAccessResolver( groupDao );
        MenuItemXmlCreator menuItemXmlCreator = new MenuItemXmlCreator( miXmlCreatorSetting, menuItemAccessResolver );

        final org.jdom.Element frontPageEl = new org.jdom.Element( "front-page" );
        modelEl.addContent( frontPageEl );
        if ( site.getFrontPage() != null )
        {
            frontPageEl.addContent( menuItemXmlCreator.createMenuItemElement( site.getFrontPage() ) );
        }

        final org.jdom.Element errorPageEl = new org.jdom.Element( "error-page" );
        modelEl.addContent( errorPageEl );
        if ( site.getErrorPage() != null )
        {
            errorPageEl.addContent( menuItemXmlCreator.createMenuItemElement( site.getErrorPage() ) );
        }

        final org.jdom.Element loginPageEl = new org.jdom.Element( "login-page" );
        modelEl.addContent( loginPageEl );
        if ( site.getLoginPage() != null )
        {
            loginPageEl.addContent( menuItemXmlCreator.createMenuItemElement( site.getLoginPage() ) );
        }

        XMLDocument modelAsXMLDocument = XMLDocumentFactory.create( new org.jdom.Document( modelEl ) );
        Document modelAsDOMDocument = modelAsXMLDocument.getAsDOMDocument();

        int[] excludeTypeKeys = new int[]{TYPE_NEWSLETTER};
        String patXML = adminService.getPageTemplatesByMenu( menuKey, excludeTypeKeys );
        Document patDoc = XMLTool.domparse( patXML );
        XMLTool.mergeDocuments( modelAsDOMDocument, patDoc, true );

        return modelAsDOMDocument;
    }

    private void addSelectedMenuElement( org.jdom.Element modelEl, UserEntity user, SiteEntity site )
    {
        DefaultSiteAccessRightAccumulator defaultSiteAccessRightAccumulator = new DefaultSiteAccessRightAccumulator( securityService );
        DefaultSiteAccumulatedAccessRights defaultSiteAccessRightsAccumulated =
            defaultSiteAccessRightAccumulator.getAccessRightsAccumulated( site, user );

        SiteXmlCreator siteXmlCreator = new SiteXmlCreator( null );
        org.jdom.Element selectedMenuEl = siteXmlCreator.createMenuElement( site, sitePropertiesService.getSiteProperties( site.getKey() ),
                                                                            defaultSiteAccessRightsAccumulated );
        modelEl.addContent( new org.jdom.Element( "selected-menu" ).addContent( selectedMenuEl ) );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        String menuXML = (String) session.getAttribute( "menuxml" );
        Document menuDoc = XMLTool.domparse( menuXML );
        String subop = formItems.getString( "subop", "" );

        int menuKey = formItems.getInt( "menukey" );
        int moveMenuItemKey = formItems.getInt( "move_menuitem", -1 );
        if ( moveMenuItemKey != -1 )
        {
            final Element[] menuItemElements = XMLTool.getElements( menuDoc, "/model/menuitems-to-list/menuitem" );

            MenuItemEntity moveMenuItem = menuItemDao.findByKey( moveMenuItemKey );

            int fromParentKey = moveMenuItem.getParent() != null ? moveMenuItem.getParent().getKey() : -1;
            int toParentKey = formItems.getInt( "move_to_parent", -1 );

            MenuItemEntity toParentMenuItem = menuItemDao.findByKey( new MenuItemKey( toParentKey ) );

            verifyNotMovingToOwnChild( moveMenuItemKey, toParentMenuItem );

            admin.moveMenuItem( user, menuItemElements, moveMenuItemKey, menuKey, fromParentKey, menuKey, toParentKey );
            admin.shiftMenuItems( user, menuItemElements, menuKey, toParentKey );
        }

        if ( "shiftmenuitems".equals( subop ) )
        {
            int parentMenuItemKey = formItems.getInt( "parentmi", -1 );

            final Element[] menuItemElements = XMLTool.getElements( menuDoc, "/model/menuitems-to-list/menuitem" );
            admin.shiftMenuItems( user, menuItemElements, menuKey, parentMenuItemKey );
        }

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "parentmi", formItems.get( "parentmi", "-1" ) );
        queryParams.put( "menukey", formItems.get( "menukey", "" ) );
        if ( formItems.containsKey( "reload" ) )
        {
            queryParams.put( "reload", formItems.get( "reload" ) );
        }
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void verifyNotMovingToOwnChild( final int menuKeyToMove, final MenuItemEntity moveToMenuItem )
    {
        MenuItemEntity menuItemToCheck = moveToMenuItem;

        while ( menuItemToCheck != null )
        {
            if ( menuItemToCheck.getMenuItemKey().equals( new MenuItemKey( menuKeyToMove ) ) )
            {
                throw new VerticalAdminException( "Not allowed to move menuitem to self or own descendant" );
            }
            else
            {
                menuItemToCheck = menuItemToCheck.getParent();
            }
        }
    }


    private void handlerMultiSitePicker( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                         ExtendedMap formItems )
        throws VerticalAdminException
    {

        String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
        User user = securityService.getLoggedInAdminConsoleUser();

        try
        {
            List<SiteEntity> allSites = siteDao.findAll();

            Cookie cookie = CookieUtil.getCookie( request, "multiSitePagePickerSelectedSite" );

            int selectedSiteKeyParam = formItems.getInt( "menukey", -1 );
            int siteKeyToSelect = selectedSiteKeyParam;
            int cookieSiteKey = -1;

            if ( cookie != null )
            {
                cookieSiteKey = Integer.parseInt( cookie.getValue() );
            }

            boolean useCookieSiteKey = selectedSiteKeyParam < 0 && cookieSiteKey > -1;

            if ( siteKeyToSelect < 0 )
            {
                siteKeyToSelect = allSites.get( 0 ) != null ? allSites.get( 0 ).getKey().toInt() : -1;
            }

            if ( useCookieSiteKey )
            {
                siteKeyToSelect = cookieSiteKey;
            }

            Document doc = XMLTool.createDocument();
            XMLTool.createRootElement( doc, "multisiteselector" );

            addSitePickerXML( doc.getDocumentElement(), allSites );

            addMenusXML( admin, user, siteKeyToSelect, doc );

            DOMSource xmlSource = new DOMSource( doc );

            Map<String, Object> parameters = new HashMap<String, Object>();

            addCommonParameters( admin, user, request, parameters, -1, siteKeyToSelect );
            parameters.put( "page", formItems.get( "page" ) );
            //parameters.put( "key", menuItemKey );
            //parameters.put( "cur_parent_key", formItems.get( "cur_parent_key" ) );

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, "multisite_page_selector.xsl" );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
    }

    private void addMenusXML( AdminService admin, User user, int selectedSiteParameter, Document doc )
    {
        Document menusDoc = XMLTool.domparse( admin.getAdminMenuIncludeReadOnlyAccessRights( user, selectedSiteParameter ) );

        Element[] menuTops = XMLTool.selectElements( menusDoc.getDocumentElement(), "menu" );

        for ( Element menuTop : menuTops )
        {
            menuTop = XMLTool.renameElement( menuTop, "menutop" );
        }

        Node newMenusDocRootNode = doc.importNode( menusDoc.getDocumentElement(), true );
        doc.getDocumentElement().appendChild( newMenusDocRootNode );
    }

    private void addSitePickerXML( Element root, List<SiteEntity> allSites )
    {
        Element sitesRoot = XMLTool.createElement( root, "sites" );

        for ( SiteEntity site : allSites )
        {
            Element siteElement = XMLTool.createElement( sitesRoot, "site" );
            siteElement.setAttribute( "name", site.getName() );
            siteElement.setAttribute( "key", site.getKey().toString() );
        }
    }

    private void selectNewParent( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        try
        {
            DOMSource xmlSource;
            Map<String, Object> parameters = new HashMap<String, Object>();

            int menuKey = formItems.getInt( "menukey" );

            Document menuDoc = XMLTool.domparse( admin.getAdminMenu( user, menuKey ) );
            int menuItemKey = formItems.getInt( "key" );
            Element menuElem = XMLTool.selectElement( menuDoc.getDocumentElement(), "menu[@key = '" + menuKey + "']" );
            String menuItemName =
                XMLTool.selectElement( menuDoc.getDocumentElement(), "//menuitem[@key = " + menuItemKey + " ]" ).getAttribute( "name" );
            menuElem = XMLTool.renameElement( menuElem, "menutop" );
            menuElem.removeAttribute( "key" );
            menuDoc = XMLTool.createDocument( menuElem );

            xmlSource = new DOMSource( menuDoc );

            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            parameters.put( "page", formItems.get( "page" ) );
            parameters.put( "key", menuItemKey );
            parameters.put( "cur_parent_key", formItems.get( "cur_parent_key" ) );
            parameters.put( "menuitemname", menuItemName );

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, "menuitem_move.xsl" );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("I/O error: %t", e );
        }
        catch ( TransformerException e )
        {
            VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
        }
    }

    private void insertMenuItem( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                 ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        try
        {

            SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
            // Hack
            if ( "none".equals( formItems.getString( "key", "" ) ) )
            {
                formItems.remove( "key" );
            }

            Element menuItemElem = null;
            int menuItemKey = formItems.getInt( "key", -1 );
            if ( menuItemKey != -1 )
            {
                Document menuItemDoc = admin.getMenuItem( user, menuItemKey, false ).getAsDOMDocument();
                menuItemElem = (Element) menuItemDoc.getDocumentElement().getFirstChild();
            }

            String menuItemXML =
                XMLTool.documentToString( formItemsToMenuItem( admin, menuItemElem, formItems, siteKey.toInt(), menuItemKey ) );

            if ( menuItemKey != -1 )
            {
                admin.updateMenuItem( user, menuItemXML );

                PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
                pageCacheService.removeEntriesByMenuItem( new MenuItemKey( menuItemKey ) );
            }
            else
            {
                menuItemKey = admin.createMenuItem( user, menuItemXML );
            }

            // Lagre rettigheter til innholdet
            // Oppdaterer rettigheter bare hvis brukeren ikke har valgt � propagere
            if ( formItems.containsKey( "updateaccessrights" ) && !formItems.getString( "propagate", "" ).equals( "true" ) )
            {
                admin.updateAccessRights( user, buildAccessRightsXML( String.valueOf( menuItemKey ), formItems, AccessRight.MENUITEM ) );
            }

            // Redirect to propagate page
            if ( "true".equals( formItems.getString( "propagate" ) ) )
            {
                handlerPropagateAccessRightsPage( request, response, session, admin, formItems );
            }
            else
            {
                redirectToBrowse( request, response, formItems );
            }
        }
        catch ( VerticalCreateException e )
        {
            VerticalAdminLogger.errorAdmin("Error creating menuitem: %t", e );
        }
        catch ( VerticalUpdateException e )
        {
            VerticalAdminLogger.errorAdmin("Error updating menuitem: %t", e );
        }
        catch ( VerticalSecurityException e )
        {
            VerticalAdminLogger.errorAdmin("Access denied: %t", e );
        }
    }

    public void handlerPreview( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems )
        throws VerticalAdminException
    {
        MenuItemAccessResolver menuItemAccessResolver = new MenuItemAccessResolver( groupDao );
        String type = formItems.getString( "type", "" );
        if ( type.equals( "shortcut" ) )
        {
            int shortCut = formItems.getInt( "shortcut", -1 );
            MenuItemEntity menuItemEntity = menuItemDao.findByKey( shortCut );
            formItems.remove( "key" );
            formItems.remove( "type" );
            formItems.putInt( "key", menuItemEntity.getKey() );
            formItems.putString( "type", menuItemEntity.getType().getName() );
            handlerPreview( request, response, session, admin, formItems );
        }

        User oldUser = securityService.getLoggedInAdminConsoleUser();
        UserEntity requester = securityService.getUser( oldUser );
        try
        {
            SiteKey siteKey = new SiteKey( formItems.getInt( "menukey" ) );
            int parentKey = formItems.getInt( "parentkey", -1 );
            Element oldMenuItemElem = null;
            int menuItemKeyInt = -1;
            String tmp = formItems.getString( "key" );
            if ( !"none".equals( tmp ) )
            {
                menuItemKeyInt = formItems.getInt( "key", -1 );
                if ( menuItemKeyInt != -1 )
                {
                    MenuItemXMLCreatorSetting setting = new MenuItemXMLCreatorSetting();
                    setting.user = requester;
                    MenuItemXmlCreator xmlCreator = new MenuItemXmlCreator( setting, menuItemAccessResolver );

                    List<MenuItemEntity> menuItems = new ArrayList<MenuItemEntity>();
                    menuItems.add( menuItemDao.findByKey( menuItemKeyInt ) );
                    Document menuItemDoc = JDOMUtil.toW3CDocument( xmlCreator.createMenuItemsDocument( menuItems, "menuitems" ) );
                    oldMenuItemElem = (Element) menuItemDoc.getDocumentElement().getFirstChild();
                }
            }

            MenuItemKey menuItemKey = new MenuItemKey( menuItemKeyInt );
            MenuItemEntity persistedMenuItem = menuItemDao.findByKey( menuItemKey.toInt() );
            MenuItemEntity modifiedMenuItem;
            if ( persistedMenuItem == null )
            {
                modifiedMenuItem = new MenuItemEntity();
                PageEntity newPage = new PageEntity();
                int pageTemplateKey = formItems.getInt( "pagetemplatekey" );
                PageTemplateEntity pageTemplate = pageTemplateDao.findByKey( pageTemplateKey );
                newPage.setTemplate( pageTemplate );
                modifiedMenuItem.setPage( newPage );
                SiteEntity site = siteDao.findByKey( siteKey );
                modifiedMenuItem.setSite( site );
                modifiedMenuItem.setKey( -1 );

                modifiedMenuItem.setParent( menuItemDao.findByKey( parentKey ) );
            }
            else
            {
                modifiedMenuItem = new MenuItemEntity( persistedMenuItem );
            }

            // create new menuitem with same values and modify with values from request
            modifiedMenuItem = modifyMenuItemForPreview( formItems, modifiedMenuItem );

            Document menuitemsDoc = createPreviewMenuItemXml( admin, oldMenuItemElem, formItems, siteKey.toInt(), menuItemKey.toInt() );

            Element menuitemsElem = menuitemsDoc.getDocumentElement();
            Element newMenuItemElem = XMLTool.getElement( menuitemsElem, "menuitem" );

            final PageTemplateEntity pageTemplate = modifiedMenuItem.getPage().getTemplate();
            final Regions regionsInPage =
                RegionsResolver.resolveRegionsForPageRequest( modifiedMenuItem, pageTemplate, PageRequestType.MENUITEM );

            // vertical context
            request.setAttribute( Attribute.PREVIEW_ENABLED, "true" );
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper( request );
            wrappedRequest.setParameter( "id", menuItemKey.toString() );
            wrappedRequest.setServletPath( "/site" );
            ServletRequestAccessor.setRequest( wrappedRequest );

            for ( MenuItemRequestParameter menuItemRequestParameter : modifiedMenuItem.getRequestParameters().values() )
            {
                wrappedRequest.setParameter( menuItemRequestParameter.getName(), menuItemRequestParameter.getValue() );
            }

            SiteEntity site = modifiedMenuItem.getSite();
            SiteXmlCreator siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
            siteXmlCreator.setUserXmlAsAdminConsoleStyle( false );
            siteXmlCreator.setIncludeDeviceClassResolverInfo( false );
            siteXmlCreator.setUser( requester );
            if ( parentKey == -1 )
            {
                siteXmlCreator.setMenuItemLevels( 0 );
            }
            else
            {
                siteXmlCreator.setMenuItemLevels( modifiedMenuItem.getLevel() + 1 );
            }
            siteXmlCreator.setBranchStartLevel( 0 );
            siteXmlCreator.setMenuItemInBranch( modifiedMenuItem );
            siteXmlCreator.setActiveMenuItem( menuItemDao.findByKey( modifiedMenuItem.getKey() ) );
            siteXmlCreator.setIncludeTopLevel( true );

            XMLDocument menusXmlDocument = siteXmlCreator.createLegacyGetMenuBranch( site );

            org.jdom.Element menusEl = new org.jdom.Element( "menus" );
            org.jdom.Element menuEl = new org.jdom.Element( "menu" );
            menuEl.addContent( menusXmlDocument.getAsJDOMDocument().getRootElement().detach() );
            menusEl.addContent( menuEl );
            org.jdom.Document menusDoc = new org.jdom.Document( menusEl );

            Element menusElem = JDOMUtil.toW3CDocument( menusDoc ).getDocumentElement();

            ContentProcessor contentProcessor = null;
            // content
            if ( modifiedMenuItem.getContent() != null )
            {
                ContentXMLCreator contentXMLCreator = new ContentXMLCreator();
                XMLDocument contentDoc =
                    contentXMLCreator.createContentsDocument( requester, modifiedMenuItem.getContent().getMainVersion(), null );
                contentProcessor = new ContentProcessor( contentDoc.getAsDOMDocument() );
            }

            int indexCount = contentProcessor == null ? 1 : 2;

            MenuItemPreviewContext menuItemPreviewContext = new MenuItemPreviewContext( modifiedMenuItem );
            PreviewContext previewContext = new PreviewContext( menuItemPreviewContext );
            previewService.setPreviewContext( previewContext );

            // prepare data source result processors
            DataSourceProcessor[] dsrProcessors = new DataSourceProcessor[indexCount];
            dsrProcessors[0] = new MenuItemProcessor( menusElem, newMenuItemElem );
            if ( contentProcessor != null )
            {
                dsrProcessors[1] = contentProcessor;
            }

            SitePath sitePath = new SitePath( site.getKey(), modifiedMenuItem.getPath() );
            sitePath.addParam( "id", menuItemKey.toString() );
            request.setAttribute( Attribute.ORIGINAL_SITEPATH, sitePath );

            // Resolve run as user
            UserEntity runAsUser = modifiedMenuItem.resolveRunAsUser( requester, true );
            if ( runAsUser == null )
            {
                runAsUser = requester;
            }

            LanguageEntity language = LanguageResolver.resolve( site, modifiedMenuItem );

            ResolverContext resolverContext = new ResolverContext( wrappedRequest, site, modifiedMenuItem, language );
            resolverContext.setUser( requester );

            final Locale locale = localeResolverService.getLocale( resolverContext );
            final String deviceClass = deviceClassResolverService.getDeviceClass( resolverContext );

            // render page
            PageRendererContext pageRendererContext = new PageRendererContext();
            pageRendererContext.setDeviceClass( deviceClass );
            pageRendererContext.setForceNoCacheUsage( true );
            pageRendererContext.setHttpRequest( wrappedRequest );
            pageRendererContext.setLanguage( language );
            pageRendererContext.setLocale( locale );
            pageRendererContext.setMenuItem( modifiedMenuItem );
            pageRendererContext.setOriginalSitePath( sitePath );
            pageRendererContext.setPageRequestType( PageRequestType.MENUITEM );
            pageRendererContext.setPreviewContext( previewContext );
            pageRendererContext.setProcessors( dsrProcessors );
            pageRendererContext.setRegionsInPage( regionsInPage );
            pageRendererContext.setRenderer( requester );
            pageRendererContext.setRequestTime( new DateTime() );
            pageRendererContext.setRunAsUser( runAsUser );
            pageRendererContext.setTicketId( request.getSession().getId() );
            pageRendererContext.setSite( site );
            pageRendererContext.setSitePath( sitePath );

            PageRenderer renderer = pageRendererFactory.createPageRenderer( pageRendererContext );
            RenderedPageResult result = renderer.renderPage( pageTemplate );

            PrintWriter writer = response.getWriter();
            writer.write( result.getContent() );
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin("Failed to get response writer: %t", e );
        }
        catch ( JDOMException e )
        {
            VerticalAdminLogger.errorAdmin("Failed to convert jdom to w3c document: %t", e );
        }
    }


    private MenuItemEntity modifyMenuItemForPreview( ExtendedMap formItems, MenuItemEntity menuItem )
        throws VerticalAdminException
    {

        menuItem.removeRequestParameters();

        if ( isArrayFormItem( formItems, "paramname" ) )
        {
            // there are multiple parameters
            String[] paramNames = (String[]) formItems.get( "paramname" );
            String[] paramVals = (String[]) formItems.get( "paramval" );
            String[] paramOverrides = (String[]) formItems.get( "paramoverride" );

            for ( int i = 0; i < paramNames.length; i++ )
            {
                String paramName = paramNames[i];
                String paramValue = paramVals[i];
                if ( paramName.length() == 0 || paramValue.length() == 0 )
                {
                    continue;
                }

                String paramOverride = paramOverrides[i];
                menuItem.addRequestParameter( paramName, paramValue, paramOverride );
            }
        }
        else
        {
            // there is only one (or zero) parameter
            String paramName = formItems.getString( "paramname", null );
            String paramVal = formItems.getString( "paramval", null );
            String paramOverride = formItems.getString( "paramoverride", null );

            if ( paramName != null || paramVal != null )
            {
                menuItem.addRequestParameter( paramName, paramVal, paramOverride );
            }
        }

        menuItem.setXmlData( menuItem.getMenuDataJDOMDocument() );

        // name
        String menuItemName = formItems.getString( "name", null ) != null ? formItems.getString( "name" ) : menuItem.getName();

        // display-name
        String displayName = formItems.getString( FORM_ITEM_DISPLAY_NAME, null );

        String menuName = formItems.getString( FORM_ITEM_MENU_NAME, null );

        menuItemName = ensureOrGenerateMenuItemName( menuItemName, displayName, menuName );

        menuItem.setName( menuItemName );

        menuItem.setDisplayName( displayName != null ? formItems.getString( FORM_ITEM_DISPLAY_NAME ) : menuItem.getDisplayName() );

        menuItem.setMenuName( menuName != null ? formItems.getString( FORM_ITEM_MENU_NAME ) : menuItem.getMenuName() );

        // type
        String type = formItems.getString( "type", "" );
        MenuItemType menuItemType = null;
        if ( type.equals( "form" ) )
        {
            menuItemType = MenuItemType.CONTENT;
        }
        else if ( type.equals( "section" ) )
        {
            menuItemType = MenuItemType.SECTION;
        }
        else if ( type.equals( "page" ) )
        {
            menuItemType = MenuItemType.PAGE;
        }
        else if ( type.equals( "sectionpage" ) )
        {
            menuItemType = MenuItemType.PAGE;
        }
        else if ( type.equals( "content" ) )
        {
            menuItemType = MenuItemType.CONTENT;
        }
        else if ( type.equals( "newsletter" ) )
        {
            menuItemType = MenuItemType.PAGE;
        }
        else if ( type.equals( "label" ) )
        {
            menuItemType = MenuItemType.LABEL;
        }
        else if ( type.equals( "shortcut" ) )
        {
            menuItemType = MenuItemType.SHORTCUT;
        }
        else if ( "localurl".equals( type ) || "externalurl".equals( type ) )
        {
            menuItemType = MenuItemType.URL;
        }
        if ( menuItemType != null )
        {
            menuItem.setType( menuItemType );
        }

        // set runAs
        String runAs = formItems.getString( "runAs", "" );
        RunAsType runAsType = RunAsType.INHERIT;
        // automatically treat a form as a page
        if ( runAs.equals( "DEFAULT_USER" ) )
        {
            runAsType = RunAsType.DEFAULT_USER;
        }
        else if ( runAs.equals( "INHERIT" ) )
        {
            runAsType = RunAsType.INHERIT;
        }
        else if ( runAs.equals( "PERSONALIZED" ) )
        {
            runAsType = RunAsType.PERSONALIZED;
        }
        menuItem.setRunAs( runAsType );

        // set description
        menuItem.setDescription(
            formItems.getString( "description", null ) != null ? formItems.getString( "description" ) : menuItem.getDescription() );

        // set keywords
        menuItem.setKeywords(
            formItems.getString( "keywords", null ) != null ? formItems.getString( "keywords" ) : menuItem.getKeywords() );

        // set language
        if ( formItems.getString( "languagekey", null ) != null )
        {
            String languageKeyStr = formItems.getString( "languagekey" );
            LanguageKey languageKey = new LanguageKey( languageKeyStr );
            LanguageEntity language = languageDao.findByKey( languageKey );
            menuItem.setLanguage( language );
        }

        // set visibility:
        if ( "on".equals( formItems.getString( "visibility", null ) ) )
        {
            menuItem.setHidden( false );
        }
        else
        {
            menuItem.setHidden( true );
        }

        // timestamp
        menuItem.setTimestamp( new Date() );

        // content
        if ( formItems.containsKey( "_selected_content" ) && menuItem.getRequestParameterValue( "key" ) == null )
        {
            int contentKey = formItems.getInt( "_selected_content" );
            ContentEntity contentEntity = contentDao.findByKey( new ContentKey( contentKey ) );
            menuItem.setContent( contentEntity );
        }
        else if ( menuItem.getRequestParameterValue( "key" ) != null )
        {
            int contentKey = Integer.valueOf( menuItem.getRequestParameterValue( "key" ) );
            ContentEntity contentEntity = contentDao.findByKey( new ContentKey( contentKey ) );
            menuItem.setContent( contentEntity );
        }

        //page windows
        if ( menuItem.getPage() != null )
        {
            PageEntity modifiedPage = new PageEntity( menuItem.getPage() );
            menuItem.setPage( modifiedPage );
            modifiedPage.removeAllPortletPlacements();

            PageTemplateEntity pageTemplate = modifiedPage.getTemplate();
            Set<PageTemplateRegionEntity> pageTemplateRegions = pageTemplate.getPageTemplateRegions();
            for ( PageTemplateRegionEntity pageTemplateRegion : pageTemplateRegions )
            {
                String[] portletKeys = formItems.getStringArray( pageTemplateRegion.getName() );
                if ( portletKeys.length > 0 )
                {
                    for ( String portletKeyStr : portletKeys )
                    {
                        if ( StringUtils.isBlank( portletKeyStr ) )
                        {
                            continue;
                        }

                        PortletEntity portlet = portletDao.findByKey( Integer.valueOf( portletKeyStr ) );

                        PageWindowEntity pageWindow = new PageWindowEntity();
                        pageWindow.setKey( new PageWindowKey( modifiedPage.getKey(), portlet.getKey() ) );
                        pageWindow.setPage( modifiedPage );
                        pageWindow.setPageTemplateRegion( pageTemplateRegion );
                        pageWindow.setTimestamp( new Date() );
                        pageWindow.setPortlet( portlet );
                        modifiedPage.addPortletPlacement( pageWindow );
                    }
                }
            }
        }

        return menuItem;
    }

    private String ensureOrGenerateMenuItemName( String menuItemName, String displayName, String menuName )
    {
        // Generate name for preview if none given
        if ( StringUtils.isEmpty( menuItemName ) )
        {
            String suggestedName = menuName;
            if ( StringUtils.isEmpty( suggestedName ) )
            {
                suggestedName = displayName;
            }

            menuItemName = PrettyPathNameCreator.generatePrettyPathName( suggestedName );
        }
        return menuItemName;
    }

    private void handlerPropagateAccessRightsPage( HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                                   AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        try
        {

            User user = securityService.getLoggedInAdminConsoleUser();
            int menuItemKey = formItems.getInt( "key", -1 );
            int menuKey = formItems.getInt( "menukey", -1 );

            Document doc = XMLTool.createDocument( "data" );
            Document changedAccessRights = buildChangedAccessRightsXML( formItems );
            Document currentAccessRights = XMLTool.domparse( buildAccessRightsXML( formItems ) );

            Document menuItems = XMLTool.domparse( admin.getAdminMenu( user, menuKey ) );

            XMLTool.mergeDocuments( doc, menuItems, true );
            XMLTool.mergeDocuments( doc, changedAccessRights, true );
            XMLTool.mergeDocuments( doc, currentAccessRights, true );

            DOMSource xmlSource = new DOMSource( doc );
            Source xslSource = AdminStore.getStylesheet( session, "menu_item_propagateaccessrights.xsl" );

            // Parameters
            Map<String, Object> parameters = new HashMap<String, Object>();
            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            addAccessLevelParameters( user, parameters );
            parameters.put( "menuitemkey", menuItemKey );
            parameters.put( "page", formItems.get( "page" ) );
            parameters.put( "menuitemname", formItems.getString( "name", "" ) );
            parameters.put( "insertbelow", formItems.getString( "insertbelow", "" ) );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }
        catch ( TransformerException e )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
        catch ( IOException e )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin(message, e );
        }
    }

    private void handlerPropagateAccessRights( HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // Propagate
        String subop = formItems.getString( "subop", "" );
        if ( "propagate".equals( subop ) )
        {

            String applyOnlyChanges = formItems.getString( "applyonlychanges", "off" );

            if ( "on".equals( applyOnlyChanges ) )
            {
                // Prepare for apply only changes..
                HashMap<String, ExtendedMap> removedMenuItemAccessRights = new HashMap<String, ExtendedMap>();
                HashMap<String, ExtendedMap> addedMenuItemAccessRights = new HashMap<String, ExtendedMap>();
                HashMap<String, ExtendedMap> modifiedMenuItemAccessRights = new HashMap<String, ExtendedMap>();

                for ( Object paramKey : formItems.keySet() )
                {
                    String paramName = (String) paramKey;
                    if ( paramName.startsWith( "arc[key=" ) )
                    {
                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        String paramValue = formItems.getString( paramName );
                        ExtendedMap categoryAccessRight = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );
                        String diffinfo = categoryAccessRight.getString( "diffinfo" );
                        if ( "removed".equals( diffinfo ) )
                        {
                            removedMenuItemAccessRights.put( paramsInName.getString( "key" ), categoryAccessRight );
                        }
                        else if ( "added".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            addedMenuItemAccessRights.put( groupKey, categoryAccessRight );
                        }
                        else if ( "modified".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            modifiedMenuItemAccessRights.put( groupKey, categoryAccessRight );
                        }
                    }
                }

                // Run through each (selected) menuitem...
                for ( Object paramKey : formItems.keySet() )
                {

                    String paramName = (String) paramKey;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        int curMenuItemKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply, only changes
                        if ( "on".equals( applyOnlyChanges ) )
                        {

                            // Henter ud eksisterende accessrights
                            Document docCurrentCategoryAR =
                                XMLTool.domparse( admin.getAccessRights( user, AccessRight.MENUITEM, curMenuItemKey, false ) );

                            // Påfører endringer
                            Document docChangedCategoryAR =
                                applyChangesInAccessRights( docCurrentCategoryAR, removedMenuItemAccessRights, modifiedMenuItemAccessRights,
                                                            addedMenuItemAccessRights );

                            // Lagrer
                            admin.updateAccessRights( user, XMLTool.documentToString( docChangedCategoryAR ) );
                        }
                    }
                }
            }
            // Apply accessright as whole
            else
            {
                // Prepare for overwrite accessrights
                Document docNewCategoryAccessRights = buildAccessRightsXML( null, null, formItems, AccessRight.MENUITEM );

                // Run through each (selected) menuitem...
                for ( Object paramKey : formItems.keySet() )
                {

                    String paramName = (String) paramKey;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );

                        int curMenuItemKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Apply on current category
                        Element categoryAccessrighs = docNewCategoryAccessRights.getDocumentElement();
                        categoryAccessrighs.setAttribute( "key", String.valueOf( curMenuItemKey ) );

                        admin.updateAccessRights( user, XMLTool.documentToString( docNewCategoryAccessRights ) );
                    }
                }
            }
        }
        // Ikke propager, bare lagre accessrights p� valgte categori
        else
        {

            int menuItemKey = formItems.getInt( "menuitemkey" );

            String accessRightsXML = buildAccessRightsXML( String.valueOf( menuItemKey ), formItems, AccessRight.MENUITEM );

            // Oppdaterer i db
            admin.updateAccessRights( user, accessRightsXML );
        }

        // Redirect...
        redirectToBrowse( request, response, formItems );
    }

    private void redirectToSectionBrowse( HttpServletRequest request, HttpServletResponse response, int menuKey, MenuItemKey menuItemKey,
                                          boolean reload )
        throws VerticalAdminException
    {

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", 950 );
        queryParams.put( "op", "browse" );
        queryParams.put( "menuitemkey", menuItemKey.toString() );
        queryParams.put( "menukey", String.valueOf( menuKey ) );
        queryParams.put( "reload", String.valueOf( reload ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void redirectToBrowse( HttpServletRequest request, HttpServletResponse response, ExtendedMap formItems )
        throws VerticalAdminException
    {

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "parentmi", formItems.get( "insertbelow", "-1" ) );
        queryParams.put( "menukey", formItems.get( "menukey", "" ) );
        queryParams.put( "reload", formItems.get( "reload", "" ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void menuItemForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        int menuKey = formItems.getInt( "menukey" );

        User user = securityService.getLoggedInAdminConsoleUser();

        Document doc1 = XMLTool.createDocument( "menus" );
        MenuItemKey menuItemParent = null;

        boolean forwardData = formItems.getBoolean( "forward_data", false );
        boolean create;

        Document menuItemXML = null;
        String categoryXML = null;
        String pageTemplatesXML;
        String pageTemplateParamsXML = null;
        String defaultAccessRightXML = null;

        // menuitem key:
        String key = formItems.getString( "key", null );

        try
        {
            if ( key != null && !key.startsWith( "f" ) && !key.equals( "none" ) )
            {
                create = false;
                menuItemXML = admin.getMenuItem( user, Integer.parseInt( key ), false, true ).getAsDOMDocument();

                menuItemParent = new MenuItemKey( Integer.parseInt( key ) );

                XMLTool.mergeDocuments( doc1, menuItemXML, true );
            }
            else
            {
                create = true;
                String insertBelow = formItems.getString( "insertbelow", null );
                if ( insertBelow != null && !"-1".equals( insertBelow ) )
                {
                    defaultAccessRightXML = admin.getAccessRights( user, AccessRight.MENUITEM, Integer.parseInt( insertBelow ), true );

                    menuItemParent = new MenuItemKey( Integer.parseInt( insertBelow ) );

                    Document insertBelowMenuXML = admin.getMenuItem( user, Integer.parseInt( insertBelow ), false, true ).getAsDOMDocument();
                    XMLTool.mergeDocuments( doc1, insertBelowMenuXML, true );
                }
                else
                {
                    defaultAccessRightXML = admin.getAccessRights( user, AccessRight.MENUITEM_DEFAULT, menuKey, true );
                }
            }

            MenuBrowseModelFactory menuBrowseModelFactory =
                new MenuBrowseModelFactory( securityService, siteDao, menuItemDao, sitePropertiesService );
            SiteKey siteKey = new SiteKey( menuKey );
            UserEntity userEntity = securityService.getUser( user );
            MenuItemFormModel model = menuBrowseModelFactory.createMenuItemFormModel( userEntity, siteKey, menuItemParent );
            XMLTool.mergeDocuments( doc1, model.toXML().getAsDOMDocument(), false );

            int[] excludeTypeKeys = null; // before we excluded page-templates of type newsletter, but not anymore.
            pageTemplatesXML = admin.getPageTemplatesByMenu( menuKey, excludeTypeKeys );

            int pageTemplateKey = formItems.getInt( "selpagetemplatekey", -1 );
            if ( pageTemplateKey < 0 && create )
            {
                // selpagetemplatekey has not been set. set it to parent or default pagetemplatekey ( if applicable )
                pageTemplateKey = model.findParentPageTemplateKey();
                formItems.putInt( "selpagetemplatekey", pageTemplateKey );
            }

            if ( pageTemplateKey >= 0 )
            {
                pageTemplateParamsXML = admin.getPageTemplParams( pageTemplateKey );
            }
            else if ( menuItemXML != null && XMLTool.getElementText( menuItemXML, "//page/@pagetemplatekey" ) != null )
            {
                pageTemplateKey = Integer.parseInt( XMLTool.getElementText( menuItemXML, "//page/@pagetemplatekey" ) );
                pageTemplateParamsXML = admin.getPageTemplParams( pageTemplateKey );
            }
            else
            {
                SiteEntity site = siteDao.findByKey( menuKey );
                PageTemplateEntity pageTemplate = site.getPageTemplate();
                if ( pageTemplate != null )
                {
                    pageTemplateKey = pageTemplate.getKey();
                    pageTemplateParamsXML = admin.getPageTemplParams( pageTemplateKey );
                }
            }

            // insert correct menuitem XML:
            Element menuitemElem = null;
            if ( menuItemXML != null )
            {
                Document miDoc = menuItemXML;
                Element tmpElem = miDoc.getDocumentElement();
                tmpElem = XMLTool.getFirstElement( tmpElem );

                if ( tmpElem != null )
                {
                    // add read count
                    // int readCount = admin.getReadCount(LogHandler.TABLE_TMENUITEM, Integer.parseInt(key));
                    // Element elem = XMLTool.createElement(miDoc, tmpElem, "logentries");
                    // elem.setAttribute("totalread", String.valueOf(readCount));

                    if ( tmpElem != null )
                    {
                        String xpath = "//menuitem[@key = '" + key + "']";
                        Element oldElem = (Element) XMLTool.selectNode( doc1.getDocumentElement(), xpath );
                        if ( oldElem != null )
                        {
                            menuitemElem = (Element) doc1.importNode( tmpElem, true );
                        }
                    }
                }
            }

            Document pageTemplateDoc = XMLTool.domparse( pageTemplatesXML );

            XMLTool.mergeDocuments( doc1, pageTemplateDoc, true );

            if ( defaultAccessRightXML != null )
            {
                XMLTool.mergeDocuments( doc1, XMLTool.domparse( defaultAccessRightXML ), true );
            }

            ExtendedMap parameters = new ExtendedMap();

            if ( forwardData )
            {
                // get accessrights element
                Node nodeAccessRights;
                if ( create )
                {
                    nodeAccessRights = XMLTool.selectNode( doc1.getDocumentElement(), "/menus/accessrights" );
                }
                else
                {
                    nodeAccessRights = XMLTool.selectNode( doc1.getDocumentElement(), "//menuitem[@key=" + key + "]/accessrights" );
                }

                // get new accessrights xml from parameters
                String xmlAccessRights = buildAccessRightsXML( formItems );
                if ( xmlAccessRights != null )
                {
                    Document docAccessRights = XMLTool.domparse( xmlAccessRights );

                    if ( docAccessRights.getDocumentElement().hasChildNodes() )
                    // replace accessrights element with the generated accessrights
                    {
                        nodeAccessRights.getParentNode().replaceChild( doc1.importNode( docAccessRights.getDocumentElement(), true ),
                                                                       nodeAccessRights );
                    }
                }

                // get custom parameters
                // get parameters element
                Node nodeParameters;
                if ( create )
                {
                    Node nodeMenu = XMLTool.selectNode( doc1.getDocumentElement(), "/menus" );
                    nodeParameters = XMLTool.createElement( doc1, (Element) nodeMenu, "parameters" );
                    nodeMenu.appendChild( nodeParameters );
                }
                else
                {
                    nodeParameters = XMLTool.selectNode( doc1.getDocumentElement(), "//menuitem[@key=" + key + "]/parameters" );
                }

                XMLTool.removeChildNodes( (Element) nodeParameters, false );

                if ( isArrayFormItem( formItems, "paramname" ) )
                {
                    String[] paramName = (String[]) formItems.get( "paramname" );
                    String[] paramValue = (String[]) formItems.get( "paramval" );
                    for ( int i = 0; i < paramName.length; i++ )
                    {
                        final String currParamName = paramName[i];
                        if ( currParamName == null || !currParamName.trim().equals( "" ) )
                        {
                            Element newElem = XMLTool.createElement( doc1, (Element) nodeParameters, "parameter", paramValue[i] );
                            newElem.setAttribute( "name", currParamName );
                            nodeParameters.appendChild( newElem );
                        }
                    }
                }
                else
                {
                    // ingen sideparametre finnes, vi lager en
                    String paramName = formItems.getString( "paramname", "" );
                    String paramValue = formItems.getString( "paramval", "" );
                    if ( paramName.length() > 0 )
                    {
                        Element newElem = XMLTool.createElement( doc1, (Element) nodeParameters, "parameter", paramValue );
                        newElem.setAttribute( "name", paramName );
                        nodeParameters.appendChild( newElem );
                    }
                }
                parameters.put( "referer", formItems.getString( "referer" ) );

            }

            if ( pageTemplateParamsXML == null )
            {
                Element nameElem = (Element) XMLTool.selectNode( doc1, "//menuitem[@key = '" + key + "']/page" );

                if ( nameElem != null )
                {
                    pageTemplateKey = Integer.parseInt( nameElem.getAttribute( "pagetemplatekey" ) );
                    pageTemplateParamsXML = admin.getPageTemplParams( pageTemplateKey );
                }
            }

            Document doc8;
            if ( pageTemplateParamsXML != null )
            {
                doc8 = XMLTool.domparse( pageTemplateParamsXML );
                XMLTool.mergeDocuments( doc1, doc8, true );
            }

            String xpath = "/pagetemplates/pagetemplate[@key=" + pageTemplateKey + "]";
            Element pagetemplateElem = (Element) XMLTool.selectNode( pageTemplateDoc, xpath );
            if ( pagetemplateElem != null )
            {
                String pageTemplateType = pagetemplateElem.getAttribute( "type" );
                if ( "form".equals( pageTemplateType ) )
                {
                    Element dataElem = XMLTool.getElement( menuitemElem, "data" );
                    Element formElem = XMLTool.getElement( dataElem, "form" );
                    if ( formElem != null )
                    {
                        String keyStr = formElem.getAttribute( "categorykey" );
                        if ( keyStr.length() > 0 )
                        {
                            int categoryKey = Integer.parseInt( keyStr );
                            categoryXML = admin.getCategoryNameXML( categoryKey );
                        }
                    }
                }
            }

            if ( categoryXML != null )
            {
                Document doc5 = XMLTool.domparse( categoryXML );
                XMLTool.mergeDocuments( doc1, doc5, true );
            }

            // Get content types for this site
            XMLTool.mergeDocuments( doc1, admin.getContentTypes( false ).getAsDOMDocument(), true );

            // Append languages
            Document langs = admin.getLanguages().getAsDOMDocument();
            XMLTool.mergeDocuments( doc1, langs, true );

            DOMSource xmlSource = new DOMSource( doc1 );

            // Stylesheet
            Source xslSource = AdminStore.getStylesheet( session, "menu_item_form.xsl" );

            // Parameters
            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            parameters.put( "page", String.valueOf( request.getParameter( "page" ) ) );
            if ( ( key == null || key.length() == 0 || key.equals( "none" ) ) )
            {
                parameters.put( "key", "none" );
            }
            else
            {
                parameters.put( "key", key );
            }

            String type = formItems.getString( "type", null );
            if ( ( type == null || "page".equals( type ) ) && pageTemplateKey >= 0 )
            {
                if ( pagetemplateElem != null )
                {
                    type = pagetemplateElem.getAttribute( "type" );
                }
            }

            if ( "document".equals( type ) || "form".equals( type ) )
            {
                type = "content";
            }

            parameters.put( "type", type );
            parameters.put( "insertbelow", formItems.getString( "insertbelow", null ) );
            parameters.put( "selpagetemplatekey", String.valueOf( pageTemplateKey ) );
            parameters.put( "name", formItems.getString( "name", null ) );

            if ( "on".equals( formItems.getString( "visibility", null ) ) )
            {
                parameters.put( "visibility", "on" );
            }

            parameters.put( "forward_data", formItems.get( "forward_data", Boolean.FALSE ) );
            parameters.put( "menu-name", formItems.getString( "menu-name", null ) );
            parameters.put( "displayname", formItems.getString( "displayname", null ) );
            parameters.put( "description", formItems.getString( "description", null ) );
            parameters.put( "keywords", formItems.getString( "keywords", null ) );
            parameters.put( "alias", formItems.getString( "alias", null ) );
            parameters.put( "document", formItems.getString( "contentdata_body", null ) );
            if ( formItems.getString( "noauth", null ) != null )
            {
                if ( "on".equals( formItems.getString( "noauth", null ) ) )
                {
                    parameters.put( "noauth", "false" );
                }
                else
                {
                    parameters.put( "noauth", "true" );
                }
            }
            parameters.put( "catselkey", formItems.getString( "catsel", null ) );
            parameters.put( "catkey", formItems.getString( "category_key", null ) );
            parameters.put( "catname", formItems.getString( "viewcategory_key", null ) );
            parameters.put( "menukey", String.valueOf( menuKey ) );

            // find content title if contentkey is specified
            if ( menuitemElem != null )
            {
                String contentKeyStr = menuitemElem.getAttribute( "contentkey" );
                if ( contentKeyStr.length() > 0 )
                {
                    final ContentKey contentKey = new ContentKey( Integer.parseInt( contentKeyStr ) );
                    final ContentEntity content = contentDao.findByKey( contentKey );
                    final String contentTitle = content.getMainVersion().getTitle();
                    parameters.put( "contenttitle", contentTitle );
                }
            }

            // Adds accessrights values, so that these can be remembered until next page
            for ( Object formItemKey : formItems.keySet() )
            {
                String paramName = (String) formItemKey;
                if ( paramName.startsWith( "accessright[key=" ) )
                {
                    parameters.put( paramName, formItems.getString( paramName ) );
                }
            }
            parameters.put( "selectedtabpageid", formItems.getString( "selectedtabpageid", null ) );

            // Get form categories
            int[] contentTypeKeys =
                admin.getContentTypesByHandlerClass( "com.enonic.vertical.adminweb.handlers.ContentFormHandlerServlet" );
            if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
            {
                StringBuffer contentTypeString = new StringBuffer();
                for ( int i = 0; i < contentTypeKeys.length; i++ )
                {
                    if ( i > 0 )
                    {
                        contentTypeString.append( ',' );
                    }
                    contentTypeString.append( contentTypeKeys[i] );
                }
                parameters.put( "contenttypestring", contentTypeString.toString() );
            }
            else
            {
                parameters.put( "contenttypestring", "99999" );
            }

            addAccessLevelParameters( user, parameters );

            UserEntity defaultRunAsUser = siteDao.findByKey( formItems.getInt( "menukey" ) ).resolveDefaultRunAsUser();
            String defaultRunAsUserName = "NA";
            if ( defaultRunAsUser != null )
            {
                defaultRunAsUserName = defaultRunAsUser.getDisplayName();
            }
            parameters.put( "defaultRunAsUser", defaultRunAsUserName );

            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );

        }
        catch ( IOException ioe )
        {
            String msg = "I/O error: %t";
            VerticalAdminLogger.errorAdmin(msg, ioe );
        }
        catch ( TransformerException te )
        {
            String msg = "XSL transformer error: %t";
            VerticalAdminLogger.errorAdmin(msg, te );
        }
    }

    private void moveMenuItemDown( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException
    {

        String menuXML = (String) session.getAttribute( "menuxml" );
        Document doc = XMLTool.domparse( menuXML );

        String xpath = "/model/menuitems-to-list//menuitem[@key = '" + formItems.getString( "key" ) + "']";
        Element movingMenuItemElement = (Element) XMLTool.selectNode( doc, xpath );

        Element parentElement = (Element) movingMenuItemElement.getParentNode();
        Node nextSiblingElement = movingMenuItemElement.getNextSibling();

        movingMenuItemElement = (Element) parentElement.removeChild( movingMenuItemElement );
        doc.importNode( movingMenuItemElement, true );

        if ( nextSiblingElement != null )
        {
            // spool forward...
            for ( nextSiblingElement = nextSiblingElement.getNextSibling();
                  ( nextSiblingElement != null && nextSiblingElement.getNodeType() != Node.ELEMENT_NODE );
                  nextSiblingElement = nextSiblingElement.getNextSibling() )
            {

            }

            if ( nextSiblingElement != null )
            {
                parentElement.insertBefore( movingMenuItemElement, nextSiblingElement );
            }
            else
            {
                parentElement.appendChild( movingMenuItemElement );
            }
        }
        else
        {
            // This is the bottom element, move it to the top
            parentElement.insertBefore( movingMenuItemElement, parentElement.getFirstChild() );
        }

        session.setAttribute( "menuxml", XMLTool.documentToString( doc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "keepxml", "yes" );
        queryParams.put( "highlight", formItems.get( "key" ) );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "parentmi", formItems.get( "parentmi" ) );
        queryParams.put( "subop", "shiftmenuitems" );

        queryParams.put( "move_menuitem", formItems.getString( "move_menuitem", "" ) );
        queryParams.put( "move_from_parent", formItems.getString( "move_from_parent", "" ) );
        queryParams.put( "move_to_parent", formItems.getString( "move_to_parent", "" ) );

        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void moveMenuItemUp( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException
    {

        String menuXML = (String) session.getAttribute( "menuxml" );
        Document doc = XMLTool.domparse( menuXML );

        String xpath = "//menuitem[@key = '" + formItems.getString( "key" ) + "']";
        Element elem = (Element) XMLTool.selectNode( doc, xpath );

        Element parent = (Element) elem.getParentNode();
        Element previousSibling = (Element) elem.getPreviousSibling();
        elem = (Element) parent.removeChild( elem );
        doc.importNode( elem, true );

        if ( previousSibling == null )
        // This is the top element, move it to the bottom
        {
            parent.appendChild( elem );
        }
        else
        {
            parent.insertBefore( elem, previousSibling );
        }

        session.setAttribute( "menuxml", XMLTool.documentToString( doc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "keepxml", "yes" );
        queryParams.put( "highlight", formItems.get( "key" ) );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "parentmi", formItems.get( "parentmi" ) );
        queryParams.put( "subop", "shiftmenuitems" );

        queryParams.put( "move_menuitem", formItems.getString( "move_menuitem", "" ) );
        queryParams.put( "move_from_parent", formItems.getString( "move_from_parent", "" ) );
        queryParams.put( "move_to_parent", formItems.getString( "move_to_parent", "" ) );

        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void handlerFormBuilder( HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException
    {

        String subOperation = formItems.getString( "subop" );
        if ( "typeselector".equals( subOperation ) )
        {
            try
            {
                // Set parameters and transform XSL for field type popup:
                Source xmlSource = new DOMSource( XMLTool.createDocument( "empty" ) );

                String type = formItems.getString( "type", "none" );
                String fileName = type + ".xsl";

                Source xslSource = AdminStore.getStylesheet( session, "formbuilder/" + fileName );

                Map<String, Object> xslParams = new HashMap<String, Object>();
                xslParams.put( "page", formItems.getString( "page" ) );
                xslParams.put( "type", type );
                xslParams.put( "row", formItems.getString( "row", "" ) );
                transformXML( session, response.getWriter(), xmlSource, xslSource, xslParams );
            }
            catch ( IOException e )
            {
                VerticalAdminLogger.errorAdmin("I/O error: %t", e );
            }
            catch ( TransformerException e )
            {
                VerticalAdminLogger.errorAdmin("XSLT error: %t", e );
            }
        }
    }
}
