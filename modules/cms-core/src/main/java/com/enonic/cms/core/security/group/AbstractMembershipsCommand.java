/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractMembershipsCommand
{
    private Set<GroupKey> memberships = new HashSet<GroupKey>();

    public void addMembership( final GroupKey key )
    {
        memberships.add( key );
    }

    public boolean removeMemberships( final List<GroupKey> keys )
    {
        return memberships.removeAll( keys );
    }

    public Collection<GroupKey> getMemberships()
    {
        return memberships;
    }
}
