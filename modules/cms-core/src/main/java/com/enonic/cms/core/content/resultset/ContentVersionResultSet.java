/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.domain.ResultSet;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;

/**
 * This class defines the content result set.
 */
public interface ContentVersionResultSet
    extends ResultSet
{
    /**
     * @param index The location of the key in the ordered result list.
     * @return The content key at given index.
     */
    ContentVersionKey getKey( int index );

    /**
     * @return An ordered list of all the keys.
     */
    List<ContentVersionKey> getKeys();

    /**
     * @param index The location of the content in the ordered result list.
     * @return The content at given index.
     */
    ContentVersionEntity getContent( int index );

    boolean containsContent( ContentVersionKey contentVersionKey );

    /**
     * @return All the content.
     */
    Collection<ContentVersionEntity> getContents();

}
