/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.springframework.core.style.ToStringCreator;

/**
 * Jun 29, 2009
 */
public class GroupSpecification
{
    public enum DeletedState
    {
        NOT_DELETED,
        DELETED
    }

    private UserStoreKey userStoreKey;

    private GroupKey key;

    private String name;

    private DeletedState deletedState;

    private String syncValue;

    private GroupType type;


    public GroupKey getKey()
    {
        return key;
    }

    public void setKey( GroupKey key )
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

    public void setName( String name )
    {
        this.name = name;
    }

    public DeletedState getDeletedState()
    {
        return deletedState;
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

    public GroupType getType()
    {
        return type;
    }

    public void setType( GroupType type )
    {
        this.type = type;
    }

    public String toString()
    {
        ToStringCreator builder = new ToStringCreator( this );
        builder.append( "key", key );
        builder.append( "userStoreKey", userStoreKey );
        builder.append( "name", name );
        builder.append( "deletedState", deletedState );
        builder.append( "syncValue", syncValue );
        builder.append( "type", type );
        return builder.toString();
    }

    public boolean isSatisfiedBy( final GroupEntity group )
    {
        if ( userStoreKey != null && !userStoreKey.equals( group.getUserStoreKey() ) )
        {
            return false;
        }

        if ( key != null && !key.equals( group.getGroupKey() ) )
        {
            return false;
        }

        if ( name != null && !name.equals( group.getName() ) )
        {
            return false;
        }

        if ( deletedState != null )
        {
            if ( deletedState == DeletedState.DELETED && !group.isDeleted() )
            {
                return false;
            }
            if ( deletedState == DeletedState.NOT_DELETED && group.isDeleted() )
            {
                return false;
            }
        }

        if ( syncValue != null && !syncValue.equals( group.getSyncValue() ) )
        {
            return false;
        }

        if ( type != null && type != group.getType() )
        {
            return false;
        }

        return true;
    }
}
