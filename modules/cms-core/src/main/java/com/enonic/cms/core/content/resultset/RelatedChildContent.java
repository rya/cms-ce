/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class RelatedChildContent
    extends AbstractRelatedContent
    implements RelatedContent
{
    private ContentVersionKey parentVersionKey;

    public RelatedChildContent( ContentVersionKey parentVersionKey, ContentEntity content )
    {
        super( content );
        this.parentVersionKey = parentVersionKey;
    }

    public ContentVersionKey getParentVersionKey()
    {
        return parentVersionKey;
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

        RelatedChildContent that = (RelatedChildContent) o;

        if ( !getContent().equals( that.getContent() ) )
        {
            return false;
        }
        if ( !getParentVersionKey().equals( that.getParentVersionKey() ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 221;
        final int multiplierNonZeroOddNumber = 875;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( getParentVersionKey() ).append(
            getContent() ).toHashCode();
    }

    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "RelatedChildContent[" );
        s.append( "parentVersionKey = " ).append( parentVersionKey );
        s.append( ", content = " ).append( getContent().getKey() );
        s.append( "]" );
        return s.toString();
    }
}
