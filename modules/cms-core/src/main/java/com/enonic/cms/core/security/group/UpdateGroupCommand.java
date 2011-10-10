/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.enonic.cms.core.security.user.UserKey;

/**
 * Jun 23, 2009
 */
public class UpdateGroupCommand
{
    private UserKey updater;

    private GroupKey groupKey;

    private String name;

    private Boolean restricted;

    private String description;

    private Set<GroupEntity> members;

    public UpdateGroupCommand( UserKey updater, GroupKey groupKey )
    {
        this.updater = updater;
        this.groupKey = groupKey;
    }

    public GroupKey getGroupKey()
    {
        return groupKey;
    }

    public UserKey getUpdater()
    {
        return updater;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Boolean isRestricted()
    {
        return restricted;
    }

    public void setRestricted( Boolean restricted )
    {
        this.restricted = restricted;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void syncMembers()
    {
        if ( members == null )
        {
            members = new LinkedHashSet<GroupEntity>();
        }
    }

    public void addMember( GroupEntity group )
    {
        if ( members == null )
        {
            members = new LinkedHashSet<GroupEntity>();
        }
        members.add( group );
    }

    public Collection<GroupEntity> getMembers()
    {
        return members;
    }

    public boolean hasMember( GroupKey groupKey )
    {
        for ( GroupEntity member : members )
        {
            if ( member.getGroupKey().equals( groupKey ) )
            {
                return true;
            }
        }
        return false;
    }

}
