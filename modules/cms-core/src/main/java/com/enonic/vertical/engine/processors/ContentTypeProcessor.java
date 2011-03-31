/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import org.w3c.dom.Element;

import com.enonic.vertical.engine.handlers.ContentHandler;

public class ContentTypeProcessor
    implements ElementProcessor
{
    ContentHandler contentHandler;

    public ContentTypeProcessor( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void process( Element elem )
    {
        int contentTypeKey = Integer.parseInt( elem.getAttribute( "contenttypekey" ) );

        String contentType = contentHandler.getContentTypeName( contentTypeKey );
        elem.setAttribute( "contenttype", contentType );
    }

}
