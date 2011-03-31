/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import org.w3c.dom.Document;

import com.enonic.cms.domain.security.user.User;

public class ContentTypeEvent
    extends ContentHandlerEvent
{
    private int contentTypeKey;

    private Document xmlData;

    public ContentTypeEvent( User user, Object source, int contentTypeKey, Document xmlData )
    {
        super( user, source );
        this.contentTypeKey = contentTypeKey;
        this.xmlData = xmlData;
    }

    public int getContentTypeKey()
    {
        return contentTypeKey;
    }

    public Document getXMLData()
    {
        return xmlData;
    }
}
