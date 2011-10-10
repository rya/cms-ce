/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.core.security.user.UserEntity;

public class DeleteGroupCommand
{
    private UserEntity deleter;

    private GroupSpecification specification;

    private boolean respondWithException = false;

    public DeleteGroupCommand( UserEntity deleter, GroupSpecification specification )
    {
        this.deleter = deleter;
        this.specification = specification;
    }

    public UserEntity getDeleter()
    {
        return deleter;
    }

    public GroupSpecification getSpecification()
    {
        return specification;
    }

    public boolean isRespondWithException()
    {
        return respondWithException;
    }

    public void setRespondWithException( boolean respondWithException )
    {
        this.respondWithException = respondWithException;
    }
}