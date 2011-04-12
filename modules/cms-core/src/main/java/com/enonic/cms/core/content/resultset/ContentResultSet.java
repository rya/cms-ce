/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.domain.ResultSet;
import com.enonic.cms.core.content.ContentEntity;

/**
 * This class defines the content result set.
 */
public interface ContentResultSet
    extends ResultSet
{

    /**
     * @param index The location of the key in the ordered result list.
     * @return The content key at given index.
     */
    ContentKey getKey( int index );

    /**
     * @return An ordered list of all the keys.
     */
    List<ContentKey> getKeys();


    /**
     * @param index The location of the content in the ordered result list.
     * @return The content at given index.
     */
    ContentEntity getContent( int index );

    boolean containsContent( ContentKey contentKey );

    /**
     * @return All the content.
     */
    Collection<ContentEntity> getContents();

    ContentResultSet createRandomizedResult( int count );

}
