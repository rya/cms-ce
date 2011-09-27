/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.accounts;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

public abstract class Account
    implements Comparable<Account>,Serializable
{
    private String id;

    private String name;

    private String displayName;

    private DateTime lastModified;

    private String userStoreName;

    protected Account()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public DateTime getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( DateTime lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public void setUserStoreName( String userStoreName )
    {
        this.userStoreName = userStoreName;
    }

    @Override
    public int compareTo( Account anotherAccount )
    {
        return this.getId().compareTo( anotherAccount.getId() );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Account account = (Account) o;

        if ( id != null ? !id.equals( account.id ) : account.id != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 273;
        final int multiplierNonZeroOddNumber = 637;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( getId() ).toHashCode();
    }
}
