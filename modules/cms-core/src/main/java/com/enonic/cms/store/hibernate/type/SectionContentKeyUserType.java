/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

/**
 *
 */
public class SectionContentKeyUserType
    extends AbstractIntegerBasedUserType<SectionContentKey>
{
    public SectionContentKeyUserType()
    {
        super( SectionContentKey.class );
    }

    public SectionContentKey get( int value )
    {
        return new SectionContentKey( value );
    }

    public Integer getIntegerValue( SectionContentKey value )
    {
        return value.toInt();
    }

    public boolean isMutable()
    {
        return false;
    }
}
