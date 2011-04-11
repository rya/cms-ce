/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import com.enonic.cms.domain.InvalidKeyException;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogEntryKey
    implements Serializable
{

    private String key;

    public LogEntryKey( String value )
    {
        if ( value == null )
        {
            throw new InvalidKeyException( value, this.getClass() );
        }

        this.key = value;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        LogEntryKey logKey = (LogEntryKey) o;

        return this.key.equals( logKey.key );
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 675;
        final int multiplierNonZeroOddNumber = 349;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( key ).toHashCode();
    }

    public static Collection<LogEntryKey> convertToList( String key )
    {
        Collection<LogEntryKey> list = new ArrayList<LogEntryKey>( 1 );
        list.add( new LogEntryKey( key ) );
        return list;
    }

    public static Collection<LogEntryKey> convertToList( String[] array )
    {
        if ( array == null || array.length == 0 )
        {
            return null;
        }

        Collection<LogEntryKey> list = new ArrayList<LogEntryKey>( array.length );
        for ( String value : array )
        {
            list.add( new LogEntryKey( value ) );

        }
        return list;
    }

    public static List<LogEntryKey> convertToList( Collection<String> collection )
    {
        if ( collection == null || collection.size() == 0 )
        {
            return null;
        }

        List<LogEntryKey> list = new ArrayList<LogEntryKey>( collection.size() );
        for ( String value : collection )
        {
            list.add( new LogEntryKey( value ) );

        }
        return list;
    }

    public String toString()
    {
        return key;
    }

}
