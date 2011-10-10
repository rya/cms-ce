/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * A helpful class for searching thru group memberships. You implement isGroupFound to decide if wanted group is found. The searcher
 * traverses the group memberships breadth-first.
 */
public abstract class GroupMembershipSearcher
{

    public boolean startSearch( GroupEntity parentGroup )
    {

        return traverseRecursively( parentGroup, new HashSet<GroupEntity>() );
    }

    private boolean traverseRecursively( GroupEntity parentGroup, final Set<GroupEntity> checkedGroups )
    {

        checkedGroups.add( parentGroup );

        final Collection<GroupEntity> memberships = parentGroup.getMemberships( false );
        for ( GroupEntity group : memberships )
        {

            if ( isGroupFound( group ) )
            {
                return true;
            }

        }

        for ( GroupEntity group : memberships )
        {
            if ( checkedGroups.contains( group ) )
            {
                continue;
            }
            if ( traverseRecursively( group, checkedGroups ) )
            {
                return true;
            }
        }

        return false;
    }

    public abstract boolean isGroupFound( GroupEntity traversedGroup );

}
