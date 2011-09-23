/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.log.LogEntryKey;

/**
 *
 */
public class LogEntryKeyUserType
    extends AbstractStringBasedUserType<LogEntryKey>
{
    public LogEntryKeyUserType()
    {
        super( LogEntryKey.class );
    }

    public LogEntryKey get( String value )
    {
        return new LogEntryKey( value );
    }

    public String getStringValue( LogEntryKey value )
    {
        return value.toString();
    }

    public boolean isMutable()
    {
        return false;
    }
}
