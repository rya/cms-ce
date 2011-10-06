/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;


public abstract class AbstractRelatedContent
{
    private ContentEntity content;

    private Set<RelatedContent> relatedChildren = new HashSet<RelatedContent>();

    public AbstractRelatedContent( ContentEntity content )
    {
        this.content = content;
    }

    public ContentEntity getContent()
    {
        return content;
    }

    public void addRelatedChild( RelatedChildContent relatedContent )
    {
        relatedChildren.add( relatedContent );
    }

    public Iterable<RelatedContent> getRelatedChildren()
    {
        return relatedChildren;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "content = " ).append( getContent().getKey() );
        return s.toString();
    }
}
