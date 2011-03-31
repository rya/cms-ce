/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.handlers.ContentHandler;

public class ContentVersionProcessor
    implements ElementProcessor
{
    ContentHandler contentHandler;

    public ContentVersionProcessor( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void process( Element elem )
    {
        int contentKey = Integer.parseInt( elem.getAttribute( "key" ) );
        //int versionKey = contentHandler.getCurrentVersionKey(contentKey);

        Document versionsDoc = contentHandler.getContentVersions( contentKey );
        elem.appendChild( elem.getOwnerDocument().importNode( versionsDoc.getDocumentElement(), true ) );

        // Find info for current
        Element[] versions = XMLTool.getElements( versionsDoc.getDocumentElement() );
        for ( int i = 0; i < versions.length; i++ )
        {
            if ( "true".equals( versions[i].getAttribute( "current" ) ) )
            {
                String keyStr = versions[i].getAttribute( "key" );
                if ( keyStr.equals( elem.getAttribute( "versionkey" ) ) == false )
                {
                    elem.setAttribute( "currentstate", versions[i].getAttribute( "state" ) );
                }
            }
        }
    }

}
