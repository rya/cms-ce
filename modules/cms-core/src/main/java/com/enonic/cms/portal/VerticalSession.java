/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.RegexpUtil;
import com.enonic.esl.xml.XMLTool;

final public class VerticalSession
{
    public static final String VERTICAL_SESSION_OBJECT = "VERTICAL_SESSION_OBJECT";

    final static int DEFAULT_SLOTS = 10;

    private Map attributes = null;

    public VerticalSession()
    {
        this( DEFAULT_SLOTS );
    }

    public VerticalSession( int slots )
    {
        attributes = new HashMap( slots );
    }

    public String[] getAttributeNames()
    {
        return (String[]) this.attributes.keySet().toArray( new String[this.attributes.size()] );
    }

    public void setAttribute( String attributeName, String value )
    {
        // remove those nasty \r
        value = RegexpUtil.substituteAll( "\\r", "", value );
        attributes.put( attributeName, value );
    }

    public void setAttribute( String attributeName, Document xmlDoc )
    {
        attributes.put( attributeName, xmlDoc );
    }


    public void setAttribute( String attributeName, Set set )
    {
        attributes.put( attributeName, set );
    }


    public Object getAttribute( String attributeName )
    {
        return attributes.get( attributeName );
    }

    public void removeAttribute( String name )
    {
        attributes.remove( name );
    }

    public Document toXML()
    {
        Document doc = XMLTool.createDocument( "sessions" );
        toXML( doc.getDocumentElement() );
        return doc;
    }


    public void toXML( Element parent )
    {
        Document doc = parent.getOwnerDocument();
        Element sessionElem = XMLTool.createElement( doc, parent, "session" );

        Set set = attributes.entrySet();
        Iterator iter = set.iterator();
        while ( iter.hasNext() )
        {
            Map.Entry entry = (Map.Entry) iter.next();

            Element attributeElem = XMLTool.createElement( doc, sessionElem, "attribute" );
            attributeElem.setAttribute( "name", (String) entry.getKey() );

            Object value = entry.getValue();
            if ( value instanceof String )
            {
                XMLTool.createTextNode( doc, attributeElem, (String) value );
            }
            else if ( value instanceof Set )
            {
                Iterator iterator = ( (Set) value ).iterator();
                while ( iterator.hasNext() )
                {
                    XMLTool.createElement( doc, attributeElem, "value", iterator.next().toString() );
                }
            }
            else
            {
                // import the xml document into the new document
                Document xmlDoc = (Document) value;
                Element rootElem = xmlDoc.getDocumentElement();
                attributeElem.appendChild( doc.importNode( rootElem, true ) );
            }
        }

    }

    public String toString()
    {
        return XMLTool.documentToString( toXML() );
    }

}