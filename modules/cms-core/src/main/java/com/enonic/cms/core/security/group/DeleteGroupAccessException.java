/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 *
 */
public class DeleteGroupAccessException
    extends RuntimeException
{
    private QualifiedUsername deleter;

    private QualifiedGroupname groupToDelete;

    public DeleteGroupAccessException( QualifiedUsername deleter, QualifiedGroupname groupToDelete )
    {
        super( buildMessage( deleter, groupToDelete ) );
        this.deleter = deleter;
        this.groupToDelete = groupToDelete;
    }

    private static String buildMessage( QualifiedUsername deleter, QualifiedGroupname groupToDelete )
    {
        return "User " + deleter + " do not have access to delete group: " + groupToDelete;
    }

    public QualifiedUsername getDeleter()
    {
        return deleter;
    }

    public QualifiedGroupname getGroupToDelete()
    {
        return groupToDelete;
    }
}
