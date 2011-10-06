/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;


public class RelatedParentContent
    extends AbstractRelatedContent
    implements RelatedContent
{
    private ContentKey childContentKey;

    private Set<RelatedContent> relatedParents = new HashSet<RelatedContent>();

    public RelatedParentContent( ContentKey childContentKey, ContentEntity parent )
    {
        super( parent );
        this.childContentKey = childContentKey;
    }

    public ContentKey getChildContentKey()
    {
        return childContentKey;
    }

    public void addRelatedParent( RelatedParentContent related )
    {
        relatedParents.add( related );
    }

    public Iterable<RelatedContent> getRelatedParents()
    {
        return relatedParents;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        RelatedParentContent that = (RelatedParentContent) o;

        if ( !getChildContentKey().equals( that.getChildContentKey() ) )
        {
            return false;
        }
        if ( !getContent().equals( that.getContent() ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 175;
        final int multiplierNonZeroOddNumber = 663;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( getChildContentKey() ).append(
            getContent() ).toHashCode();
    }

    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "RelatedContentParent[" );
        s.append( "childContentKey = " ).append( childContentKey );
        s.append( ", content = " ).append( getContent().getKey() );
        s.append( "]" );
        return s.toString();
    }
}
