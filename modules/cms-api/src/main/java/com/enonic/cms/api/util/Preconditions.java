/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.util;

import java.text.MessageFormat;

public final class Preconditions
{
    private Preconditions()
    {
    }

    public static void checkArgument( final boolean expression )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument( final boolean expression, final Object message )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException( String.valueOf( message ) );
        }
    }

    public static void checkArgument( final boolean expression, final String message, final Object... args )
    {
        if ( !expression )
        {
            throw new IllegalArgumentException( MessageFormat.format( message, args ) );
        }
    }

    public static void checkState( final boolean expression )
    {
        if ( !expression )
        {
            throw new IllegalStateException();
        }
    }

    public static void checkState( final boolean expression, final Object message )
    {
        if ( !expression )
        {
            throw new IllegalStateException( String.valueOf( message ) );
        }
    }

    public static void checkState( final boolean expression, final String message, final Object... args )
    {
        if ( !expression )
        {
            throw new IllegalStateException( MessageFormat.format( message, args ) );
        }
    }

    public static <T> T checkNotNull( final T ref )
    {
        if ( ref == null )
        {
            throw new NullPointerException();
        }

        return ref;
    }

    public static <T> T checkNotNull( final T ref, final Object message )
    {
        if ( ref == null )
        {
            throw new NullPointerException( String.valueOf( message ) );
        }

        return ref;
    }

    public static <T> T checkNotNull( final T ref, final String message, final Object... args )
    {
        if ( ref == null )
        {
            throw new NullPointerException( MessageFormat.format( message, args ) );
        }

        return ref;
    }
}
