/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.security.user.User;

/**
 * Jul 18, 2009
 */
public class UserNameXmlCreator
{
    public UserNameXmlCreator()
    {
    }

    public Document createUserNamesDocument( User user )
    {
        List<User> list = new ArrayList<User>();
        list.add( user );
        return createUserNamesDocument( list );
    }

    public Document createUserNamesDocument( Collection<User> users )
    {
        Element usernamesEl = new Element( "usernames" );
        for ( User user : users )
        {
            usernamesEl.addContent( doCreateUserNameElement( user ) );
        }
        return new Document( usernamesEl );
    }

    private Element doCreateUserNameElement( User user )
    {
        Element usernameEl = new Element( "username" );
        usernameEl.setAttribute( "key", user.getKey().toString() );
        usernameEl.setAttribute( "email", user.getEmail() != null ? user.getEmail() : "" );
        if ( user.getUserStoreKey() != null )
        {
            usernameEl.setAttribute( "userstorekey", user.getUserStoreKey().toString() );
        }
        usernameEl.setText( user.getDisplayName() );
        return usernameEl;
    }
}
