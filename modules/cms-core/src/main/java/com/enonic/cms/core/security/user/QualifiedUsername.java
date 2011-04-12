/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.AbstractQualifiedName;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class QualifiedUsername
    extends AbstractQualifiedName
{

    private String userStoreName;

    private UserStoreKey userStoreKey;

    private String username;

    private static char[] userStoreSeperators = new char[]{'\\', ':'};

    private static char preferedUserStoreSeperator = '\\';

    private static String keyPrefix = "#";


    public static QualifiedUsername parse( String qualifiedUsername )
    {

        if ( qualifiedUsername == null )
        {
            throw new IllegalArgumentException( "Given qualifiedUsername cannot be null" );
        }

        String userStore = "";
        String username = qualifiedUsername;

        int pos = -1;
        for ( int i = 0; i < userStoreSeperators.length && pos == -1; i++ )
        {
            pos = qualifiedUsername.indexOf( userStoreSeperators[i] );
        }
        if ( pos > -1 )
        {
            userStore = qualifiedUsername.substring( 0, pos );
            username = qualifiedUsername.substring( pos + 1 );
        }

        if ( userStore.startsWith( keyPrefix ) )
        {
            return new QualifiedUsername( new UserStoreKey( userStore ), username );
        }
        else
        {
            return new QualifiedUsername( userStore, username );
        }
    }

    public QualifiedUsername( UserStoreKey userStoreKey, String username )
    {

        if ( userStoreKey == null )
        {
            throw new IllegalArgumentException( "Given userStoreKey cannot be null" );
        }
        if ( username == null )
        {
            throw new IllegalArgumentException( "Given username cannot be null" );
        }

        this.userStoreKey = userStoreKey;
        this.username = username;

        this.stringValue = new StringBuffer().append( keyPrefix ).append( this.userStoreKey ).append( preferedUserStoreSeperator ).append(
            this.username ).toString();
    }

    public QualifiedUsername( String userStoreName, String username )
    {

        if ( userStoreName == null )
        {
            throw new IllegalArgumentException( "Given userStoreName cannot be null" );
        }
        if ( username == null )
        {
            throw new IllegalArgumentException( "Given username cannot be null" );
        }

        this.userStoreName = userStoreName;
        this.username = username;

        this.stringValue = new StringBuffer().append( this.userStoreName ).append(
            this.userStoreName.length() > 0 ? preferedUserStoreSeperator : "" ).append( this.username ).toString();
    }

    public QualifiedUsername( String username )
    {

        if ( username == null )
        {
            throw new IllegalArgumentException( "Given username cannot be null" );
        }

        this.username = username;
        this.stringValue = username;
    }


    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public boolean hasUserStoreNameSet()
    {
        if ( getUserStoreName() == null )
        {
            return false;
        }

        return getUserStoreName().length() > 0;
    }

    public boolean hasUserStoreSet()
    {
        return getUserStoreKey() != null || hasUserStoreNameSet();
    }

    public String getUsername()
    {
        return username;
    }

    public String toString()
    {
        return stringValue;
    }
}
