/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.ContentKey;

/**
 * This interface defines the index value result.
 */
public interface IndexValueResult
{
    /**
     * Return the value.
     */
    public String getValue();

    /**
     * Return the content key.
     */
    public ContentKey getContentKey();
}
