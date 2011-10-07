/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.security.group.GroupKey;


public class GroupKeyUserType
    extends AbstractStringBasedUserType<GroupKey>
{
    public GroupKeyUserType()
    {
        super( GroupKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public GroupKey get( final String value )
    {
        return new GroupKey( value );
    }


    public String getStringValue( final GroupKey value )
    {
        return value.toString();
    }

}