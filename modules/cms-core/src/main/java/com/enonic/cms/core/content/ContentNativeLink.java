/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

/**
 * May 8, 2009
 */
public class ContentNativeLink
{
    private ContentKey contentKey;

    private String link;

    private ContentNativeLinkType type;

    public ContentNativeLink( ContentKey contentKey, String link, ContentNativeLinkType type )
    {
        this.contentKey = contentKey;
        this.link = link;
        this.type = type;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public String getLink()
    {
        return link;
    }

    public ContentNativeLinkType getType()
    {
        return type;
    }
}
