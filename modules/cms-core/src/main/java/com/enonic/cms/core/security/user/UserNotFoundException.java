/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.domain.NotFoundErrorType;

public class UserNotFoundException
    extends RuntimeException
    implements NotFoundErrorType
{

    private UserKey userKey;

    private QualifiedUsername qname;

    private String message;

    private boolean isKey = false;

    public UserNotFoundException( QualifiedUsername qname )
    {
        this.qname = qname;
        message = "User not found, qualified user name: '" + qname + "'";
    }

    public UserNotFoundException( UserKey userKey )
    {
        this.userKey = userKey;
        message = "User not found, key: '" + userKey + "'";
        this.isKey = true;
    }

    public UserNotFoundException( UserSpecification userSpec )
    {
        message = "User not found from sprecificetion: " + userSpec.toString();
    }

    public boolean isKey()
    {
        return isKey;
    }

    public UserKey getUserKey()
    {
        return userKey;
    }

    public QualifiedUsername getQualifiedName()
    {
        return qname;
    }

    public String getMessage()
    {
        return message;
    }
}