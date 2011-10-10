/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 *
 */
public class UpdateGroupAccessException
    extends RuntimeException
{
    private QualifiedUsername updater;

    private QualifiedGroupname groupToUpdate;

    public UpdateGroupAccessException( QualifiedUsername updater, QualifiedGroupname groupToUpdate )
    {
        super( buildMessage( updater, groupToUpdate ) );
        this.updater = updater;
        this.groupToUpdate = groupToUpdate;
    }

    private static String buildMessage( QualifiedUsername deleter, QualifiedGroupname groupToUpdate )
    {
        return "User " + deleter + " do not have access to update group: " + groupToUpdate;
    }

    public QualifiedUsername getUpdater()
    {
        return updater;
    }

    public QualifiedGroupname getGroupToUpdate()
    {
        return groupToUpdate;
    }
}