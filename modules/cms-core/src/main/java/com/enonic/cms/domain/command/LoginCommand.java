/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.command;

import java.io.Serializable;

public class LoginCommand
    implements Serializable
{
    private final static long serialVersionUID = 1L;

    /**
     * Property: uid
     */
    private String uid;

    /**
     * Property: password
     */
    private String password;

    /**
     * Get property: uid
     *
     * @return uid
     */
    public String getUid()
    {
        return uid;
    }

    /**
     * Set property: uid
     *
     * @param uid an uid
     */
    public void setUid( String uid )
    {
        this.uid = uid;
    }

    /**
     * Get property: password
     *
     * @return password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Set property: password
     *
     * @param password
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * Returns true if the login is valid.
     *
     * @return if true, both uid and password is set
     */
    public boolean hasLogin()
    {
        return uid != null && password != null;
    }

    /**
     * Returns the login command string representation.
     *
     * @return uid
     */
    public String toString()
    {
        return uid;
    }
}
