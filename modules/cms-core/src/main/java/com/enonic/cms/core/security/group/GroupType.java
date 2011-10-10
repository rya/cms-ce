/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.security.user.UserType;

public enum GroupType
{

    /**
     * Only one occurence. Members of this group shall have any rights, regardless of what rights are set for this group.
     */
    ENTERPRISE_ADMINS( 0, "Enterprise Administrators", true, true, true ),

    // any occurence per userstore
    USERSTORE_GROUP( 1, "userStoreGroup", false, false, false ),

    // one occurence per userstore
    USERSTORE_ADMINS( 2, "Userstore Administrators", true, false, false ),

    /**
     * One occurence per userstore. All users except anonymous is implisit member of this group. So remember to do extra security checks on
     * this group.
     */
    AUTHENTICATED_USERS( 3, "Authenticated Users", true, false, false ),

    // any number of occurence
    GLOBAL_GROUP( 4, "globalGroup", false, false, true ),

    // only one occurence
    ADMINS( 5, "Administrators", true, true, true ),

    // one occurence per user
    USER( 6, "userGroup", false, false, false ),

    // only one occurence
    ANONYMOUS( 7, "anonymousGroup", true, true, true ),

    // only one occurence
    CONTRIBUTORS( 8, "Contributors", true, true, true ),

    // only one occurence
    DEVELOPERS( 9, "Developers", true, true, true ),

    // only one occurence
    EXPERT_CONTRIBUTORS( 10, "Expert Contributors", true, true, true );

    private Integer value;

    private String name;

    private boolean builtIn = false;

    private boolean onlyOneGroupOccurance = false;

    private boolean global = false;

    GroupType( int value, String name, boolean builtIn, boolean onlyOneGroupOccurance, boolean global )
    {
        this.value = value;
        this.name = name;
        this.builtIn = builtIn;
        this.onlyOneGroupOccurance = onlyOneGroupOccurance;
        this.global = global;
    }

    public static List<Integer> getIntegerValues( Collection<GroupType> types )
    {

        List<Integer> integers = new ArrayList<Integer>( types.size() );
        for ( GroupType type : types )
        {
            integers.add( type.toInteger() );
        }
        return integers;
    }

    public static List<Integer> getIntegerValues( GroupType[] types )
    {

        List<Integer> integers = new ArrayList<Integer>( types.length );
        for ( GroupType type : types )
        {
            integers.add( type.toInteger() );
        }
        return integers;
    }

    public static Collection<GroupType> getBuiltInTypes()
    {
        List<GroupType> builtIns = new ArrayList<GroupType>();
        for ( GroupType type : values() )
        {
            if ( type.isBuiltIn() )
            {
                builtIns.add( type );
            }
        }
        return builtIns;
    }

    public static List<Integer> getBuiltInTypesAsInteger()
    {

        List<Integer> builtIns = new ArrayList<Integer>();
        GroupType[] types = values();
        for ( GroupType type : types )
        {
            if ( type.isBuiltIn() )
            {
                builtIns.add( type.toInteger() );
            }
        }
        return builtIns;
    }

    public static GroupType get( String groupType )
        throws NumberFormatException
    {

        return get( Integer.parseInt( groupType ) );
    }

    public static GroupType get( int value )
    {

        GroupType[] types = values();
        for ( GroupType type : types )
        {
            if ( type.toInteger() == value )
            {
                return type;
            }
        }

        return null;
    }


    public Integer toInteger()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }

    public boolean isGlobal()
    {
        return global;
    }

    /**
     * @return <code>true</code> if there is allowed only one occurance of this group.
     */
    public boolean isOnlyOneGroupOccurance()
    {
        return onlyOneGroupOccurance;
    }

    /**
     * Two group types are equal if their database values are equal.
     *
     * @param o The other <code>GroupType</code> to compare this to.
     * @return <code>true</code> if the other object is equal.  <code>false</code> otherwise.
     */
    public boolean equals( GroupType o )
    {
        return ( value.equals( o.value ) );
    }

    public String toString()
    {
        return getName() + ", DB value: " + value + ( builtIn ? " (built-in)" : "" );
    }

    public static GroupType resolveAssociate( UserType type )
    {
        if ( type == null )
        {
            return null;
        }

        if ( type == UserType.NORMAL )
        {
            return GroupType.USER;
        }
        else if ( type == UserType.ADMINISTRATOR )
        {
            return GroupType.ENTERPRISE_ADMINS;
        }
        if ( type == UserType.ANONYMOUS )
        {
            return GroupType.ANONYMOUS;
        }
        return null;
    }
}
