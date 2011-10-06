/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.resultset.ContentResultSet;

/**
 * This interface defines the content index service.
 */
public interface ContentIndexService
{

    /**
     * @param contentKey The key of the ContentEntity that should be deleted.
     * @return The number of entities that has been deleted.
     */
    public int remove( ContentKey contentKey );


    /**
     * Remove contents by category key.
     */
    public void removeByCategory( CategoryKey categoryKey );

    /**
     * Remove contents by content type key.
     */
    public void removeByContentType( ContentTypeKey contentTypeKey );

    /**
     * Index the content.
     *
     * @param doc            All the information that should be indexed.
     * @param deleteExisting If it is known for sure that the content has not been indexed before, set this value to <code>false</code>, in
     *                       order to optimize the indexing process.
     */
    public void index( ContentDocument doc, boolean deleteExisting );

    /**
     * Return true if content is indexed.
     */
    public boolean isIndexed( ContentKey contentKey );

    /**
     * Query the content.
     */
    public ContentResultSet query( ContentIndexQuery query );

    /**
     * Query the index values.
     */
    public IndexValueResultSet query( IndexValueQuery query );

    /**
     * Query the index values.
     */
    public AggregatedResult query( AggregatedQuery query );
}
