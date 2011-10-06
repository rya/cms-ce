/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import com.enonic.cms.core.content.ContentEntity;

public interface RelatedContent
{
    ContentEntity getContent();

    void addRelatedChild( RelatedChildContent relatedContent );

    Iterable<RelatedContent> getRelatedChildren();
}
