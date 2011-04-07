/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;

/**
 *
 */
public interface IndexService
{
    /**
     * Removes the specified content from the search index.
     *
     * @param content The content to be removed.
     */
    void removeContent( ContentEntity content );

    public void regenerateIndex( List<ContentKey> contentKeys );


    void index( ContentEntity content );

    void index( ContentEntity content, boolean deleteExisting );
}
