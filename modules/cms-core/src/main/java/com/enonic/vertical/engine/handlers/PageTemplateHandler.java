/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.enonic.esl.xml.XMLTool;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;
import com.enonic.cms.domain.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;
import com.enonic.cms.domain.structure.portlet.PortletEntity;

public final class PageTemplateHandler
    extends BaseHandler
{
    public XMLDocument getPageTemplate( PageTemplateKey pageTemplateKey )
    {
        PageTemplateEntity entity = pageTemplateDao.findByKey( pageTemplateKey.toInt() );
        Document doc = createPageTemplatesDocument( entity != null ? Arrays.asList( entity ) : null );
        return XMLDocumentFactory.create( doc );
    }

    public PageTemplateType getPageTemplateType( PageTemplateKey pageTemplateKey )
    {
        PageTemplateEntity entity = pageTemplateDao.findByKey( pageTemplateKey.toInt() );
        if ( entity == null )
        {
            return null;
        }
        return entity.getType();
    }

    private Document createPageTemplatesDocument( Collection<PageTemplateEntity> pageTemplates )
    {
        Document doc = XMLTool.createDocument( "pagetemplates" );
        if ( pageTemplates == null )
        {
            return doc;
        }

        for ( PageTemplateEntity pageTemplate : pageTemplates )
        {
            Element root = doc.getDocumentElement();
            Document ptdDoc = null;

            org.jdom.Document pageTemplateXmlDataAsJdomDoc = pageTemplate.getXmlData();
            if ( pageTemplateXmlDataAsJdomDoc != null )
            {
                ptdDoc = XMLDocumentFactory.create( pageTemplateXmlDataAsJdomDoc ).getAsDOMDocument();
                Element docElem = XMLTool.getElement( ptdDoc.getDocumentElement(), "document" );
                if ( docElem != null )
                {
                    Node firstChild = docElem.getFirstChild();
                    if ( firstChild == null || firstChild.getNodeType() != Node.CDATA_SECTION_NODE )
                    {
                        docElem.setAttribute( "mode", "xhtml" );
                    }
                }
            }

            Element elem = XMLTool.createElement( doc, root, "pagetemplate" );
            elem.setAttribute( "key", String.valueOf( pageTemplate.getKey() ) );
            elem.setAttribute( "menukey", String.valueOf( pageTemplate.getSite().getKey() ) );

            // sub-elements
            XMLTool.createElement( doc, elem, "name", pageTemplate.getName() );
            XMLTool.createElement( doc, elem, "description", pageTemplate.getDescription() );
            Element tmp = XMLTool.createElement( doc, elem, "stylesheet" );
            tmp.setAttribute( "stylesheetkey", pageTemplate.getStyleKey().toString() );
            tmp.setAttribute( "exists", resourceDao.getResourceFile( pageTemplate.getStyleKey() ) != null ? "true" : "false" );

            // element conobjects for pagetemplate
            Document contentobj = getPageTemplateCO( pageTemplate );
            elem.appendChild( doc.importNode( contentobj.getDocumentElement(), true ) );

            // get page template parameters
            Document ptpDoc = getPageTemplParams( pageTemplate );
            Node ptpNode = doc.importNode( ptpDoc.getDocumentElement(), true );
            elem.appendChild( ptpNode );

            // element timestamp
            XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( pageTemplate.getTimestamp(), true ) );

            // element: pagetemplatedata
            if ( ptdDoc != null )
            {
                elem.appendChild( doc.importNode( ptdDoc.getDocumentElement(), true ) );
            }

            // element: css
            ResourceKey cssKey = pageTemplate.getCssKey();
            if ( cssKey != null )
            {
                tmp = XMLTool.createElement( doc, elem, "css" );
                tmp.setAttribute( "stylesheetkey", cssKey.toString() );
                tmp.setAttribute( "exists", resourceDao.getResourceFile( cssKey ) != null ? "true" : "false" );
            }

            // attribute: runAs & defaultRunAsUser
            elem.setAttribute( "runAs", pageTemplate.getRunAs().toString() );
            UserEntity defaultRunAsUser = pageTemplate.getSite().resolveDefaultRunAsUser();
            String defaultRunAsUserName = "NA";
            if ( defaultRunAsUser != null )
            {
                defaultRunAsUserName = defaultRunAsUser.getDisplayName();
            }
            elem.setAttribute( "defaultRunAsUser", defaultRunAsUserName );

            // attribute: type
            elem.setAttribute( "type", pageTemplate.getType().getName() );

            // contenttypes
            int[] contentTypes = getContentTypesByPageTemplate( pageTemplate );
            Document contentTypesDoc = getContentHandler().getContentTypesDocument( contentTypes );
            XMLTool.mergeDocuments( elem, contentTypesDoc, true );
        }

        return doc;
    }

    private Document getPageTemplateCO( PageTemplateEntity pageTemplate )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "contentobjects" );

        final List<PageTemplatePortletEntity> objects = pageTemplate.getPortlets();
        for ( PageTemplatePortletEntity pageTemplateObject : objects )
        {
            final PortletEntity portlet = pageTemplateObject.getPortlet();
            final PageTemplateRegionEntity pageTemplateParam = pageTemplateObject.getPageTemplateRegion();

            Element elem = XMLTool.createElement( doc, root, "contentobject" );
            elem.setAttribute( "pagetemplatekey", String.valueOf( pageTemplate.getKey() ) );
            elem.setAttribute( "conobjkey", String.valueOf( portlet.getKey() ) );

            elem.setAttribute( "parameterkey", String.valueOf( pageTemplateParam.getKey() ) );

            // element: contentobjectdata
            Document contentdata = XMLDocumentFactory.create( portlet.getXmlDataAsJDOMDocument() ).getAsDOMDocument();
            Node xmldata_root = doc.importNode( contentdata.getDocumentElement(), true );
            elem.appendChild( xmldata_root );

            // sub-elements
            XMLTool.createElement( doc, elem, "order", String.valueOf( pageTemplateObject.getOrder() ) );
            XMLTool.createElement( doc, elem, "name", portlet.getName() );
            XMLTool.createElement( doc, elem, "separator", pageTemplateParam.getSeparator() );
            elem = XMLTool.createElement( doc, elem, "parametername", pageTemplateParam.getName() );
            elem.setAttribute( "multiple", String.valueOf( pageTemplateParam.isMultiple() ? "1" : "0" ) );
            elem.setAttribute( "override", String.valueOf( pageTemplateParam.isOverride() ? "1" : "0" ) );
        }

        return doc;
    }

    private Document getPageTemplParams( PageTemplateEntity pageTemplate )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "pagetemplateparameters" );

        if ( pageTemplate != null )
        {
            for ( PageTemplateRegionEntity entity : pageTemplate.getPageTemplateRegions() )
            {
                Element elem = XMLTool.createElement( doc, root, "pagetemplateparameter" );
                elem.setAttribute( "key", String.valueOf( entity.getKey() ) );
                elem.setAttribute( "pagetemplatekey", String.valueOf( entity.getPageTemplate().getKey() ) );
                elem.setAttribute( "multiple", entity.isMultiple() ? "1" : "0" );
                elem.setAttribute( "override", entity.isOverride() ? "1" : "0" );

                // sub-elements
                XMLTool.createElement( doc, elem, "name", entity.getName() );
                XMLTool.createElement( doc, elem, "separator", entity.getSeparator() );
            }
        }

        return doc;
    }

    private int[] getContentTypesByPageTemplate( PageTemplateEntity entity )
    {
        if ( entity != null )
        {
            Set<ContentTypeEntity> list = entity.getContentTypes();
            int[] array = new int[list.size()];
            int pos = 0;

            for ( ContentTypeEntity value : list )
            {
                array[pos] = value.getKey();
                pos++;
            }

            return array;
        }
        else
        {
            return new int[0];
        }
    }
}
