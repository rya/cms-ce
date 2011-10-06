/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;


/**
 * A convinient class to hold a content and version.
 */
public class ContentAndVersion
    implements Serializable
{
    private ContentEntity content;

    private ContentVersionEntity version;

    public ContentAndVersion( ContentEntity content, ContentVersionEntity version )
    {
        this.content = content;
        this.version = version;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public ContentVersionEntity getVersion()
    {
        return version;
    }
}
