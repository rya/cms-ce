/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.Date;

import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.user.UserEntity;

public class RelatedContentQuery
{
    private UserEntity user;

    private ContentResultSet contentResultSet;

    private int parentLevel = 0;

    private int childrenLevel = 0;

    private int parentChildrenLevel = 0;

    private boolean includeOnlyMainVersions = true;

    private boolean contentOnline = true;

    private Date onlineCheckDate = null;

    public RelatedContentQuery( RelatedContentQuery source )
    {
        this( source.onlineCheckDate );

        this.user = source.user;
        this.contentResultSet = source.contentResultSet;
        this.parentLevel = source.parentLevel;
        this.childrenLevel = source.childrenLevel;
        this.parentChildrenLevel = source.parentChildrenLevel;
        this.includeOnlyMainVersions = source.includeOnlyMainVersions;
        this.contentOnline = source.contentOnline;
        this.onlineCheckDate = source.onlineCheckDate;
    }

    public RelatedContentQuery( Date onlineCheckDate )
    {
        this.onlineCheckDate = onlineCheckDate;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public ContentResultSet getContentResultSet()
    {
        return contentResultSet;
    }

    public void setContentResultSet( ContentResultSet contentResultSet )
    {
        this.contentResultSet = contentResultSet;
    }

    public int getParentLevel()
    {
        return parentLevel;
    }

    public void setParentLevel( int parentLevel )
    {
        this.parentLevel = parentLevel;
    }

    public int getChildrenLevel()
    {
        return childrenLevel;
    }

    public void setChildrenLevel( int childrenLevel )
    {
        this.childrenLevel = childrenLevel;
    }

    public int getParentChildrenLevel()
    {
        return parentChildrenLevel;
    }

    public void setParentChildrenLevel( int parentChildrenLevel )
    {
        this.parentChildrenLevel = parentChildrenLevel;
    }

    public boolean includeOnlyMainVersions()
    {
        return includeOnlyMainVersions;
    }

    public void setIncludeOnlyMainVersions( boolean includeOnlyMainVersions )
    {
        this.includeOnlyMainVersions = includeOnlyMainVersions;
    }

    /**
     * Generates a filter that will only include content that is online at a given date. This date will normally be now.
     *
     * @param onlineAt The date when the content was online.
     */
    public void setFilterContentOnlineAt( Date onlineAt )
    {
        this.contentOnline = true;
        this.onlineCheckDate = onlineAt;
    }

    /**
     * Opens up the content filter, so that all content, online or offline will be searched.
     */
    public void setFilterIncludeOfflineContent()
    {
        this.contentOnline = false;
    }

    public boolean isFilterContentOnline()
    {
        return this.contentOnline;
    }

    public void setOnlineCheckDate( Date onlineCheckDate )
    {
        this.onlineCheckDate = onlineCheckDate;
    }

    public Date getOnlineCheckDate()
    {
        return this.onlineCheckDate;
    }
}


