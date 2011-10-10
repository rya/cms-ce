/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

/**
 * Jul 8, 2009
 */
public class DeleteUserCommand
{
    private UserKey deleter;

    private UserSpecification specification;

    public DeleteUserCommand( UserKey deleter, UserSpecification specification )
    {
        this.deleter = deleter;
        this.specification = specification;
    }

    public UserKey getDeleter()
    {
        return deleter;
    }

    public UserSpecification getSpecification()
    {
        return specification;
    }
}
