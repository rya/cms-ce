/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.Arrays;
import java.util.List;

import com.enonic.cms.core.structure.page.PageEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.structure.page.PageWindowEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;

public final class PageHandler
    extends BaseHandler
{

    public XMLDocument getPage( int pageKey, boolean complete )
    {
        PageEntity entity = pageDao.findByKey( pageKey );
        Document doc = createPagesDocument( entity != null ? Arrays.asList( entity ) : null, complete );
        return XMLDocumentFactory.create( doc );
    }

    private Document createPageContentObject( PageEntity entity )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "contentobjects" );

        for ( PageWindowEntity pageWindow : entity.getPageWindows() )
        {
            Element elem = XMLTool.createElement( doc, root, "contentobject" );
            elem.setAttribute( "pagekey", String.valueOf( entity.getKey() ) );
            elem.setAttribute( "conobjkey", String.valueOf( pageWindow.getPortlet().getKey() ) );
            elem.setAttribute( "parameterkey", String.valueOf( pageWindow.getPageTemplateRegion().getKey() ) );

            Document contentdata = XMLDocumentFactory.create( pageWindow.getPortlet().getXmlDataAsJDOMDocument() ).getAsDOMDocument();
            Node xmldata_root = doc.importNode( contentdata.getDocumentElement(), true );
            elem.appendChild( xmldata_root );

            XMLTool.createElement( doc, elem, "order", String.valueOf( pageWindow.getOrder() ) );
            XMLTool.createElement( doc, elem, "name", pageWindow.getPortlet().getName() );
            XMLTool.createElement( doc, elem, "separator", pageWindow.getPageTemplateRegion().getSeparator() );
            elem = XMLTool.createElement( doc, elem, "parametername", pageWindow.getPageTemplateRegion().getName() );
            elem.setAttribute( "multiple", String.valueOf( pageWindow.getPageTemplateRegion().isMultiple() ) );
            elem.setAttribute( "override", String.valueOf( pageWindow.getPageTemplateRegion().isOverride() ) );
        }

        return doc;
    }

    private Document createPagesDocument( List<PageEntity> entities, boolean complete )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "pages" );

        if ( entities != null )
        {
            for ( PageEntity page : entities )
            {
                Element elem = XMLTool.createElement( doc, root, "page" );
                elem.setAttribute( "key", String.valueOf( page.getKey() ) );
                elem.setAttribute( "pagetemplatekey", String.valueOf( page.getTemplate().getKey() ) );

                org.jdom.Document pageXmlDataAsJdomDoc = page.getXmlDataAsDocument();
                if ( pageXmlDataAsJdomDoc != null )
                {
                    Document pageDataDocument = XMLDocumentFactory.create( pageXmlDataAsJdomDoc ).getAsDOMDocument();
                    elem.appendChild( doc.importNode( pageDataDocument.getDocumentElement(), true ) );
                }
                else
                {
                    XMLTool.createElement( doc, elem, "pagedata" );
                }

                if ( complete )
                {
                    Document contentobj = createPageContentObject( page );
                    Node contentobjects_root = doc.importNode( contentobj.getDocumentElement(), true );
                    elem.appendChild( contentobjects_root );
                }
            }
        }

        return doc;
    }

    public PageTemplateKey getPageTemplateKey( int pageKey )
    {
        PageEntity entity = pageDao.findByKey( pageKey );
        PageTemplateEntity templateEntity = entity != null ? entity.getTemplate() : null;
        int pageTemplateKey = templateEntity != null ? templateEntity.getKey() : -1;
        return new PageTemplateKey( pageTemplateKey );
    }

}
