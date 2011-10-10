/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 *
 */
public class CreateGroupAccessException
    extends RuntimeException
{
    private QualifiedUsername creator;

    private GroupType groupTypeToCreate;

    public CreateGroupAccessException( QualifiedUsername creator, GroupType groupTypeToCreate )
    {
        super( buildMessage( creator, groupTypeToCreate ) );
        this.creator = creator;
        this.groupTypeToCreate = groupTypeToCreate;
    }

    private static String buildMessage( QualifiedUsername creator, GroupType groupTypeToCreate )
    {
        return "User " + creator + " do not have access to create group of type: " + groupTypeToCreate;
    }

    public QualifiedUsername getCreator()
    {
        return creator;
    }

    public GroupType getGroupTypeToCreate()
    {
        return groupTypeToCreate;
    }
}