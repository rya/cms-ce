/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

/**
 * Jul 22, 2009
 */
public class Executor
{
    private User executor;

    private User loggedInUser;

    public Executor( User executor, User loggedInUser )
    {
        this.executor = executor;
        this.loggedInUser = loggedInUser;
    }

    public User getExecutor()
    {
        return executor;
    }

    public User getLoggedInUser()
    {
        return loggedInUser;
    }
}
