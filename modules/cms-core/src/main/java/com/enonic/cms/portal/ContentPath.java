/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.content.ContentKey;
import com.google.common.base.Preconditions;

import com.enonic.cms.domain.Path;

/**
 * Jul 24, 2009
 */
public class ContentPath
{
    private ContentKey contentKey;

    private String contentName;

    private Path pathToMenuItem;

    private boolean oldStyleContentPath = false;

    private boolean permaLink = false;

    public ContentPath( ContentKey contentKey, String contentName, Path pathToMenuItem )
    {
        Preconditions.checkNotNull( contentKey );

        this.contentKey = contentKey;
        this.contentName = contentName;
        this.pathToMenuItem = pathToMenuItem;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public String getContentName()
    {
        return contentName;
    }

    public Path getPathToMenuItem()
    {
        return pathToMenuItem;
    }

    public boolean isOldStyleContentPath()
    {
        return oldStyleContentPath;
    }

    public void setOldStyleContentPath( boolean oldStyleContentPath )
    {
        this.oldStyleContentPath = oldStyleContentPath;
    }

    public boolean isPermaLink()
    {
        return permaLink;
    }

    public void setPermaLink( boolean permaLink )
    {
        this.permaLink = permaLink;
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

        ContentPath that = (ContentPath) o;

        if ( !contentKey.equals( that.contentKey ) )
        {
            return false;
        }
        if ( contentName != null ? !contentName.equals( that.contentName ) : that.contentName != null )
        {
            return false;
        }
        if ( pathToMenuItem != null ? !pathToMenuItem.equals( that.pathToMenuItem ) : that.pathToMenuItem != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = contentKey.hashCode();
        result = 31 * result + ( contentName != null ? contentName.hashCode() : 0 );
        result = 31 * result + ( pathToMenuItem != null ? pathToMenuItem.hashCode() : 0 );
        return result;
    }
}
