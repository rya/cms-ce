/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.enonic.cms.framework.xml.IllegalCharacterCleaner;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.store.dao.ContentIndexDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentIndexEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.index.ContentIndexConstants;

/**
 * This class is used for admin to build xml for extended info such as index values and content data.
 */
public final class ContentSourceXmlCreator
    implements ContentIndexConstants
{

    private IllegalCharacterCleaner xmlCleaner = new IllegalCharacterCleaner();

    private final ContentIndexDao contentIndexDao;


    public ContentSourceXmlCreator( ContentIndexDao contentIndexDao )
    {
        this.contentIndexDao = contentIndexDao;
    }

    public XMLDocument createSourceDocument( ContentVersionEntity version )
    {
        Element root = new Element( "source" );
        addContentDataElem( root, version );
        addRelatedChildrenElem( root, version );
        if ( version.getKey().equals( version.getContent().getMainVersion().getKey() ) )
        {
            // list only indexes when version is the main version
            addIndexesElem( root, version.getContent().getKey() );
        }
        return XMLDocumentFactory.create( new Document( root ) );
    }

    private void addContentDataElem( Element parent, ContentVersionEntity version )
    {
        Element elem = new Element( "data" );
        elem.setText( serializeDocument( version.getContentDataAsJDomDocument() ) );
        parent.addContent( elem );
    }

    private void addIndexesElem( Element parent, ContentKey key )
    {
        Element elem = new Element( "indexes" );
        parent.addContent( elem );

        for ( Map.Entry<String, List<String>> entry : getIndexValues( key ).entrySet() )
        {
            elem.addContent( createIndexElem( entry.getKey(), entry.getValue() ) );
        }
    }

    private void addRelatedChildrenElem( Element parent, ContentVersionEntity version )
    {
        Element childrenEl = new Element( "related-children" );
        parent.addContent( childrenEl );
        for ( ContentEntity child : version.getRelatedChildren( true ) )
        {
            childrenEl.addContent( createRelatedChildElem( child ) );
        }
    }

    private Element createRelatedChildElem( ContentEntity child )
    {
        Element childEl = new Element( "content" );
        childEl.setAttribute( "key", child.getKey().toString() );
        childEl.setAttribute( "deleted", Boolean.toString( child.isDeleted() ) );
        childEl.addContent( new Element( "repositorypath" ).setText( child.getPathAsString() ) );
        childEl.addContent( new Element( "title" ).setText( child.getMainVersion().getTitle() ) );
        return childEl;
    }

    private Element createIndexElem( String name, List<String> values )
    {
        Element elem = new Element( "index" );
        elem.setAttribute( "name", name );
        elem.setAttribute( "internal", String.valueOf( !name.startsWith( "data/" ) ) );

        for ( String value : values )
        {
            Element valueElem = new Element( "value" );
            valueElem.setAttribute( "length", String.valueOf( value.length() ) );
            valueElem.setText( xmlCleaner.cleanXml( value ) );
            elem.addContent( valueElem );
        }

        return elem;
    }

    private Map<String, List<String>> getIndexValues( ContentKey key )
    {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        for ( ContentIndexEntity entity : contentIndexDao.findByContentKey( key ) )
        {
            String name = translatePath( entity.getPath() );

            List<String> values = map.get( name );
            if ( values == null )
            {
                values = new ArrayList<String>();
                map.put( name, values );
            }

            values.add( translateValue( name, entity.getValue() ) );
        }

        return map;
    }

    private String translatePath( String path )
    {
        return path.replace( '#', '/' );
    }

    private String translateValue( String name, String value )
    {
        if ( value.equals( BLANK_REPLACER ) )
        {
            return "";
        }
        else if ( M_PUBLISH_TO.equals( name ) && value.equals( BLANK_PUBLISH_TO_REPLACER ) )
        {
            return "";
        }
        else if ( M_PUBLISH_FROM.equals( name ) && value.equals( BLANK_PUBLISH_FROM_REPLACER ) )
        {
            return "";
        }
        else
        {
            return value;
        }
    }

    private String serializeDocument( Document doc )
    {
        XMLOutputter out = new XMLOutputter( Format.getPrettyFormat().setOmitDeclaration( true ) );
        return out.outputString( doc );
    }
}
