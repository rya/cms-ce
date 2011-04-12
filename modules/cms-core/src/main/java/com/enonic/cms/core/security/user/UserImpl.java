/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.joda.time.DateTime;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.user.UserInfo;

/**
 * Jul 11, 2009
 */
public class UserImpl
    implements User
{
    private UserKey key;

    private GroupKey userGroupKey;

    private String uid;

    private QualifiedUsername qualifiedName;

    private String password;

    private DateTime timestamp;

    private boolean builtIn;

    private boolean root = false;

    private boolean enterpriseAdmin = false;

    private UserStoreKey userStoreKey;

    private UserType type = UserType.NORMAL;

    private String displayName;

    private String email;

    private Boolean deleted;

    private UserInfo userInfo;

    public String getPassword()
    {
        return password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setUserStoreKey( UserStoreKey userStoreKey )
    {
        this.userStoreKey = userStoreKey;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getName()
    {
        return uid;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }

    public void setBuiltIn( boolean value )
    {
        this.builtIn = value;
    }

    public boolean isRoot()
    {
        return root;
    }

    public void setRoot( boolean value )
    {
        this.root = value;
    }

    public boolean isEnterpriseAdmin()
    {
        return enterpriseAdmin;
    }

    public void setPassword( String pwd )
    {
        password = pwd;
    }

    public void setDisplayName( final String value )
    {
        displayName = value;
    }

    public void setUID( String uid )
    {
        this.uid = uid;
    }

    public boolean isAnonymous()
    {
        return type == UserType.ANONYMOUS;
    }

    public DateTime getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( DateTime value )
    {
        this.timestamp = value;
    }

    public void setEnterpriseAdmin( boolean ea )
    {
        enterpriseAdmin = ea;
        if ( ea )
        {
            type = UserType.ADMINISTRATOR;
        }
    }

    public UserKey getKey()
    {
        return key;
    }

    public void setKey( UserKey userKey )
    {
        this.key = userKey;
    }

    public void setType( UserType type )
    {
        this.type = type;
    }

    public UserType getType()
    {
        return type;
    }

    public QualifiedUsername getQualifiedName()
    {
        return qualifiedName;
    }

    public void setQualifiedName( QualifiedUsername value )
    {
        this.qualifiedName = value;
    }

    public GroupKey getUserGroupKey()
    {
        return userGroupKey;
    }

    public boolean hasUserGroup()
    {
        return userGroupKey != null;
    }

    public void setUserGroupKey( GroupKey value )
    {
        this.userGroupKey = value;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( Boolean deleted )
    {
        this.deleted = deleted;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( final UserInfo value )
    {
        userInfo = value;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof User ) )
        {
            return false;
        }

        User user = (User) o;

        if ( !key.equals( user.getKey() ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return key.hashCode();
    }

    public static UserImpl createFrom( UserEntity userEntity )
    {
        final UserStoreEntity userStore = userEntity.getUserStore();

        final UserImpl user = new UserImpl();
        user.setType( userEntity.getType() );
        user.setEmail( userEntity.getEmail() );
        user.setBuiltIn( userEntity.isBuiltIn() );
        user.setRoot( userEntity.isRoot() );
        user.setEnterpriseAdmin( userEntity.isEnterpriseAdmin() );
        user.setDisplayName( userEntity.getDisplayName() );
        user.setUID( userEntity.getName() );
        user.setPassword( userEntity.getPassword() ); // is this one necessary?
        user.setKey( userEntity.getKey() );
        user.setQualifiedName( userEntity.getQualifiedName() );
        user.setTimestamp( new DateTime( userEntity.getTimestamp() ) );
        user.setUserInfo( userEntity.getUserInfo() );

        if ( userEntity.getUserGroup() != null )
        {
            user.setUserGroupKey( userEntity.getUserGroup().getGroupKey() );
        }

        if ( userStore != null )
        {
            user.setUserStoreKey( userStore.getKey() );
        }

        return user;
    }
}

