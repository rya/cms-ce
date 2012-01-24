package com.enonic.cms.core.content;


import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;

public class GetRelatedContentResult
{
    private ContentResultSet content;

    private RelatedContentResultSet relatedContent;

    public GetRelatedContentResult( ContentResultSet content, RelatedContentResultSet relatedContent )
    {
        this.content = content;
        this.relatedContent = relatedContent;
    }

    public ContentResultSet getContent()
    {
        return content;
    }

    public RelatedContentResultSet getRelatedContent()
    {
        return relatedContent;
    }
}
