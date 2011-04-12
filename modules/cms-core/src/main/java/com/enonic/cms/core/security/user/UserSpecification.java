/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.group.GroupKey;
import org.springframework.core.style.ToStringCreator;

import com.enonic.cms.core.security.userstore.UserStoreKey;

/**
 * Jun 29, 2009
 */
public class UserSpecification
{
    public enum DeletedState
    {
        NOT_DELETED,
        DELETED,
        ANY
    }

    private UserStoreKey userStoreKey;

    private UserKey key;

    private String name;

    private DeletedState deletedState = DeletedState.ANY;

    private String syncValue;

    private UserType type;

    private GroupKey userGroupKey;

    private String email;

    public UserKey getKey()
    {
        return key;
    }

    public void setKey( UserKey key )
    {
        this.key = key;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public void setUserStoreKey( UserStoreKey value )
    {
        this.userStoreKey = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String value )
    {
        this.name = value;
    }

    public DeletedState getDeletedState()
    {
        return deletedState;
    }

    public void setDeletedStateNotDeleted()
    {
        this.deletedState = DeletedState.NOT_DELETED;
    }

    public void setDeletedState( DeletedState value )
    {
        this.deletedState = value;
    }

    public String getSyncValue()
    {
        return syncValue;
    }

    public void setSyncValue( String syncValue )
    {
        this.syncValue = syncValue;
    }

    public UserType getType()
    {
        return type;
    }

    public void setType( UserType type )
    {
        this.type = type;
    }

    public GroupKey getUserGroupKey()
    {
        return userGroupKey;
    }

    public void setUserGroupKey( GroupKey userGroupKey )
    {
        this.userGroupKey = userGroupKey;
    }

    public String toString()
    {
        ToStringCreator builder = new ToStringCreator( this );
        builder.append( "key", key );
        builder.append( "userStoreKey", userStoreKey );
        builder.append( "uid", name );
        builder.append( "deletedState", deletedState );
        builder.append( "syncValue", syncValue );
        builder.append( "type", type );
        builder.append( "userGroupKey", userGroupKey );
        builder.append( "email", email );
        return builder.toString();
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    public boolean isSatisfiedBy( final UserEntity user )
    {
        if ( userStoreKey != null && !userStoreKey.equals( user.getUserStoreKey() ) )
        {
            return false;
        }

        if ( key != null && !key.equals( user.getKey() ) )
        {
            return false;
        }

        if ( name != null && !name.equalsIgnoreCase( user.getName() ) )
        {
            return false;
        }

        if ( deletedState == DeletedState.DELETED && !user.isDeleted() )
        {
            return false;
        }
        if ( deletedState == DeletedState.NOT_DELETED && user.isDeleted() )
        {
            return false;
        }

        if ( syncValue != null && !syncValue.equals( user.getSync() ) )
        {
            return false;
        }

        if ( type != null && type != user.getType() )
        {
            return false;
        }

        if ( userGroupKey != null && !userGroupKey.equals( user.getUserGroupKey() ) )
        {
            return false;
        }

        if ( email != null && !email.equalsIgnoreCase( user.getEmail() ) )
        {
            return false;
        }

        return true;
    }
}


