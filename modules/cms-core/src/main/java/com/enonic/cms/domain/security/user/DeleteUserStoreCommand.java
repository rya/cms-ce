/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.user;

import com.enonic.cms.domain.security.userstore.UserStoreKey;

/**
 * Created by rmy - Date: Jul 17, 2009
 */
public class DeleteUserStoreCommand
{

    UserKey deleter;

    UserStoreKey key;

    public UserKey getDeleter()
    {
        return deleter;
    }

    public void setDeleter( UserKey deleter )
    {
        this.deleter = deleter;
    }

    public UserStoreKey getKey()
    {
        return key;
    }

    public void setKey( UserStoreKey key )
    {
        this.key = key;
    }
}
