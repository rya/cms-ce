/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.mail;

import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 7, 2010
 * Time: 10:18:56 AM
 */
public class MailRecipient
{
    String name;

    String email;

    public MailRecipient( String name, String email )
    {
        this.name = name;
        this.email = email;
    }

    public MailRecipient( UserEntity user )
    {
        this.name = user.getDisplayName();
        this.email = user.getEmail();
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
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

        MailRecipient that = (MailRecipient) o;

        if ( email != null ? !email.equals( that.email ) : that.email != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        return result;
    }
}
