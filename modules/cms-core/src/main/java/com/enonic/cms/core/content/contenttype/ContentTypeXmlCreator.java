/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.List;

import com.enonic.cms.core.resource.ResourceFolder;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.resource.ResourceKey;

public class ContentTypeXmlCreator
{
    private boolean includeContentCount = false;

    private ResourceFolder resourceRoot = null;

    public void includeContentCountInfo( boolean includeContentCount )
    {
        this.includeContentCount = includeContentCount;
    }

    public void setResourceRoot( ResourceFolder resourceRoot )
    {
        this.resourceRoot = resourceRoot;
    }

    public XMLDocument createContentTypesDocument( List<ContentTypeEntity> contentTypes )
    {
        final Element root = new Element( "contenttypes" );

        for ( ContentTypeEntity contentType : contentTypes )
        {
            root.addContent( doCreateContentTypeElement( contentType ) );
        }
        return XMLDocumentFactory.create( new Document( root ) );
    }

    private Element doCreateContentTypeElement( ContentTypeEntity contentType )
    {
        final Element elem = new Element( "contenttype" );

        setHandlerAttributes( elem, contentType.getHandler() );
        setDefaultCssAttributes( elem, contentType.getDefaultCssKey() );

        elem.setAttribute( "key", String.valueOf( contentType.getKey() ) );
        elem.addContent( new Element( "name" ).setText( contentType.getName() ) );

        if ( includeContentCount )
        {
            /* Not implemented yet
            int count = getContentCountByContentType( entity.getKey() );
            elem.setAttribute( "contentcount", String.valueOf( count ) );
            */
        }

        elem.addContent( doCreateModuelData( contentType ) );

        if ( contentType.getDescription() != null )
        {
            elem.addContent( new Element( "description" ).setText( contentType.getDescription() ) );
        }
        elem.addContent( new Element( "timestamp" ).setText( CalendarUtil.formatTimestamp( contentType.getTimestamp(), true ) ) );

        return elem;
    }

    private void setHandlerAttributes( Element elem, ContentHandlerEntity handler )
    {
        elem.setAttribute( "contenthandlerkey", String.valueOf( handler.getKey() ) );
        elem.setAttribute( "handler", handler.getClassName() );
    }

    private void setDefaultCssAttributes( Element elem, ResourceKey defaultCssKey )
    {
        if ( defaultCssKey != null )
        {
            elem.setAttribute( "csskey", defaultCssKey.toString() );
            if ( resourceRoot != null )
            {
                elem.setAttribute( "csskeyexists", resourceRoot.getFile( defaultCssKey.toString() ) != null ? "true" : "false" );
            }
        }
    }

    private Element doCreateModuelData( ContentTypeEntity contentType )
    {
        Element modulDataEl = new Element( "moduledata" );

        if ( contentType.getData() != null )
        {
            Document dataDoc = contentType.getData().getAsJDOMDocument();
            Element dataRootEl = dataDoc.getRootElement();

            if ( dataRootEl.getName().equals( "module" ) )
            {
                modulDataEl.addContent( dataRootEl.detach() );
            }
            else
            {
                modulDataEl = (Element) dataRootEl.detach();
            }
        }
        return modulDataEl;
    }
}
