/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;

/**
 * Nov 16, 2010
 */
public class GetContentResult
{
    private ContentResultSet contentResultSet;

    private RelatedContentResultSet relatedContentResultSet;

    public GetContentResult( ContentResultSet contentResultSet, RelatedContentResultSet relatedContentResultSet )
    {
        this.contentResultSet = contentResultSet;
        this.relatedContentResultSet = relatedContentResultSet;
    }

    public ContentResultSet getContentResultSet()
    {
        return contentResultSet;
    }

    public RelatedContentResultSet getRelatedContentResultSet()
    {
        return relatedContentResultSet;
    }
}
