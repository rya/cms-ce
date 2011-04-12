/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.structure.RunAsType;

public class RunAsUserType
    extends AbstractIntegerBasedUserType<RunAsType>
{
    public RunAsUserType()
    {
        super( RunAsType.class );
    }

    public RunAsType get( int value )
    {
        return RunAsType.get(value);
    }

    public Integer getIntegerValue( RunAsType value )
    {
        return value.getKey();
    }

    public boolean isMutable()
    {
        return false;
    }
}