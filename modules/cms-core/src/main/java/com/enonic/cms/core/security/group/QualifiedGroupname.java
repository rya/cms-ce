/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.core.security.AbstractQualifiedName;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class QualifiedGroupname
    extends AbstractQualifiedName
{
    private static String keyPrefix = "#";

    private static char[] userStoreSeperators = new char[]{'\\', ':'};

    private static char preferedUserStoreSeperator = '\\';

    private UserStoreKey userStoreKey;

    private String userStoreName;

    private String groupname;

    private boolean isGlobal = false;

    public static QualifiedGroupname parse( String qualifiedGroupname )
    {

        if ( qualifiedGroupname == null )
        {
            throw new IllegalArgumentException( "Given qualifiedGroupname cannot be null" );
        }

        String userStore = "";
        String groupname = qualifiedGroupname;

        int pos = -1;
        for ( int i = 0; i < userStoreSeperators.length && pos == -1; i++ )
        {
            pos = qualifiedGroupname.indexOf( userStoreSeperators[i] );
        }
        if ( pos > -1 )
        {
            userStore = qualifiedGroupname.substring( 0, pos );
            groupname = qualifiedGroupname.substring( pos + 1 );
        }

        if ( userStore.equals( "" ) )
        {
            return new QualifiedGroupname( true, null, groupname );
        }
        else if ( userStore.startsWith( keyPrefix ) )
        {
            return new QualifiedGroupname( new UserStoreKey( userStore ), groupname );
        }
        else
        {
            return new QualifiedGroupname( false, userStore, groupname );
        }
    }

    public QualifiedGroupname( UserStoreKey userStoreKey, String groupname )
    {

        if ( userStoreKey == null )
        {
            throw new IllegalArgumentException( "Given userStoreKey cannot be null" );
        }
        if ( groupname == null )
        {
            throw new IllegalArgumentException( "Given groupname cannot be null" );
        }

        this.userStoreKey = userStoreKey;
        this.groupname = groupname;
        this.isGlobal = false;

        stringValue =
            new StringBuffer().append( "#" ).append( userStoreKey ).append( preferedUserStoreSeperator ).append( groupname ).toString();
    }

    public QualifiedGroupname( boolean isGlobal, String userStoreName, String groupname )
    {

        if ( userStoreName == null && !isGlobal )
        {
            throw new IllegalArgumentException( "Given userStoreName cannot be null" );
        }
        if ( groupname == null )
        {
            throw new IllegalArgumentException( "Given groupname cannot be null" );
        }

        this.userStoreName = userStoreName;
        this.groupname = groupname;
        this.isGlobal = isGlobal;

        StringBuffer s = new StringBuffer();
        if ( !isGlobal )
        {
            s.append( userStoreName ).append( preferedUserStoreSeperator );
        }
        s.append( groupname );
        stringValue = s.toString();
    }

    public void setUserStoreKey( UserStoreKey value )
    {
        this.userStoreKey = value;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public String getGroupname()
    {
        return groupname;
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public String toString()
    {
        return stringValue;
    }

    public boolean isGlobal()
    {
        return isGlobal;
    }

    public boolean isUserStoreLocal()
    {
        return !isGlobal;
    }
}
