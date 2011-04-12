/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.Collection;
import java.util.Set;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionKey;

/**
 * Sep 30, 2010
 */
public interface RelatedContentResultSet
{
    int size();

    boolean isEmpty();

    Collection<RelatedContent> getDistinctCollectionOfRelatedContent();

    Set<ContentEntity> getDinstinctSetOfContent();

    Collection<ContentKey> getContentKeys();

    Iterable<RelatedParentContent> getRootRelatedParents( ContentEntity content );

    Iterable<RelatedChildContent> getRootRelatedChildren( ContentVersionEntity contentVersion );

    RelatedContent getRelatedContent( ContentKey contentKey );

    void overwriteRootRelatedChild( RelatedChildContent overwritingRCC );

    /**
     * Adds related content from given set to this set, overwrites those who already exists.
     */
    void overwrite( RelatedContentResultSet overwritingSet );

    void retainRelatedRootChildren( ContentVersionKey parent, Collection<ContentKey> children );
}
