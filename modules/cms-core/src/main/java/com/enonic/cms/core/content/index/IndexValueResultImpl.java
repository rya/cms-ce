/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.ContentKey;

/**
 * This class implements the index value result.
 */
public final class IndexValueResultImpl
    implements IndexValueResult
{
    /**
     * Content key.
     */
    private final ContentKey contentKey;

    /**
     * Value.
     */
    private final String value;

    /**
     * Construct the result.
     */
    public IndexValueResultImpl( ContentKey contentKey, String value )
    {
        this.contentKey = contentKey;
        this.value = value;
    }

    /**
     * Return the value.
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Return the content key.
     */
    public ContentKey getContentKey()
    {
        return this.contentKey;
    }
}
