/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote.plugin;

import java.util.List;
import java.util.Set;

import com.enonic.cms.core.security.userstore.connector.remote.plugin.RemoteUserStorePlugin;

import com.enonic.cms.domain.user.field.UserFieldType;
import com.enonic.cms.domain.user.remote.RemoteGroup;
import com.enonic.cms.domain.user.remote.RemotePrincipal;
import com.enonic.cms.domain.user.remote.RemoteUser;

public class NopRemoteUserStorePlugin
    implements RemoteUserStorePlugin
{
    private String prop1;

    private int prop2;

    private boolean prop3;

    public String getProp1()
    {
        return prop1;
    }

    public void setProp1( String prop1 )
    {
        this.prop1 = prop1;
    }

    public int getProp2()
    {
        return prop2;
    }

    public void setProp2( int prop2 )
    {
        this.prop2 = prop2;
    }

    public boolean getProp3()
    {
        return prop3;
    }

    public void setProp3( boolean prop3 )
    {
        this.prop3 = prop3;
    }

    public void initialize()
    {
    }

    public Set<UserFieldType> getSupportedFieldTypes()
    {
        return null;
    }

    public boolean authenticate( String userId, String password )
    {
        return false;
    }

    public RemoteUser getUser( String userId )
    {
        return null;
    }

    public List<RemoteUser> getAllUsers()
    {
        return null;
    }

    public RemoteGroup getGroup( String groupId )
    {
        return null;
    }

    public List<RemoteGroup> getAllGroups()
    {
        return null;
    }

    public boolean addPrincipal( RemotePrincipal principal )
    {
        return false;
    }

    public boolean updatePrincipal( RemotePrincipal principal )
    {
        return false;
    }

    public boolean removePrincipal( RemotePrincipal principal )
    {
        return false;
    }

    public boolean changePassword( String userId, String password )
    {
        return false;
    }

    public List<RemotePrincipal> getMembers( RemoteGroup group )
    {
        return null;
    }

    public void addMembers( RemoteGroup group, List<RemotePrincipal> members )
    {
    }

    public void removeMembers( RemoteGroup group, List<RemotePrincipal> members )
    {
    }

    public List<RemoteGroup> getMemberships( RemotePrincipal principal )
    {
        return null;
    }
}
