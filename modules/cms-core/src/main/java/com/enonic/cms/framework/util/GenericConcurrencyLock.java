/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GenericConcurrencyLock<T>
{
    private final Map<T, WeakReference<Lock>> lockMap = new HashMap<T, WeakReference<Lock>>();

    public static <T> GenericConcurrencyLock<T> create()
    {
        return new GenericConcurrencyLock<T>();
    }

    public Lock getLock( T key )
    {
        Lock lock;
        synchronized ( lockMap )
        {
            lock = getLockFromMap( key );
            if ( lock == null )
            {
                lock = new ReentrantLock();
                WeakReference<Lock> value = new WeakReference<Lock>( lock );
                lockMap.put( key, value );
            }
        }

        return lock;
    }

    private Lock getLockFromMap( T key )
    {
        WeakReference<Lock> weakReference = lockMap.get( key );
        if ( weakReference == null )
        {
            return null;
        }
        return weakReference.get();
    }
}

