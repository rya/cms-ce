/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.security.user.UserEntity;

public class RelatedChildrenContentQuery
{
    private UserEntity user;

    private int childrenLevel = 0;

    private Collection<ContentVersionEntity> contentVersions;

    private boolean online;

    private Date onlineCheckDate;

    public RelatedChildrenContentQuery( Date onlineCheckDate )
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

    public int getChildrenLevel()
    {
        return childrenLevel;
    }

    public void setChildrenLevel( int childrenLevel )
    {
        this.childrenLevel = childrenLevel;
    }

    public Collection<ContentVersionEntity> getContentVersions()
    {
        return contentVersions;
    }

    public void setContentVersion( ContentVersionEntity contentVersion )
    {
        List<ContentVersionEntity> list = new ArrayList<ContentVersionEntity>();
        list.add( contentVersion );
        this.contentVersions = list;
    }

    public void setContentVersions( Collection<ContentVersionEntity> contentVersions )
    {
        this.contentVersions = contentVersions;
    }

    public boolean setIncludeOffline()
    {
        return online = false;
    }

    public boolean isOnline()
    {
        return online;
    }

    public Date getOnlineCheckDate()
    {
        return onlineCheckDate;
    }
}
