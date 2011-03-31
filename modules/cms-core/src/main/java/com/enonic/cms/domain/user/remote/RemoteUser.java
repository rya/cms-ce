/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.user.remote;

import com.enonic.cms.domain.user.field.UserFieldMap;

public final class RemoteUser
    extends RemotePrincipal
{
    private String email;

    private final UserFieldMap userFields;

    public RemoteUser( String id )
    {
        super( id );
        this.userFields = new UserFieldMap( false );
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public UserFieldMap getUserFields()
    {
        return this.userFields;
    }

    public int hashCode()
    {
        return this.getId().hashCode();
    }

    public boolean equals( Object o )
    {
        return ( o instanceof RemoteUser ) && ( (RemoteUser) o ).getId().equals( getId() );
    }
}
