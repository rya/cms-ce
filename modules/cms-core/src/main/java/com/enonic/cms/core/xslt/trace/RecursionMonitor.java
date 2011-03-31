/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.trace;

import java.net.URLDecoder;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.saxon.trace.InstructionInfo;

/**
 * This class implements the recursion monitor.
 */
final class RecursionMonitor
{
    /**
     * Dummy prefix.
     */
    private final static String DUMMY_PREFIX = "dummy:/";

    /**
     * Max depth.
     */
    private final int maxDepth;

    /**
     * Stack count of instructions.
     */
    private final Map<InstructionInfo, AtomicInteger> stackCount;

    /**
     * Constructs the listener.
     */
    public RecursionMonitor( int maxDepth )
    {
        this.maxDepth = maxDepth;
        this.stackCount = new IdentityHashMap<InstructionInfo, AtomicInteger>();
    }

    /**
     * Return the counter.
     */
    private AtomicInteger getCounter( InstructionInfo info )
    {
        AtomicInteger counter = this.stackCount.get( info );
        if ( counter == null )
        {
            counter = new AtomicInteger();
            this.stackCount.put( info, counter );
        }

        return counter;
    }

    /**
     * Increment instruction count.
     */
    public void enter( InstructionInfo info )
    {
        AtomicInteger counter = getCounter( info );
        int value = counter.incrementAndGet();

        if ( value >= this.maxDepth )
        {
            throw new RecursionException(
                "Max recursion depth of [" + this.maxDepth + "] reached for [" + getSystemId( info ) + "]. Cannot proceed." );
        }
    }

    /**
     * Decrement instruction count.
     */
    public void leave( InstructionInfo info )
    {
        getCounter( info ).decrementAndGet();
    }

    /**
     * Return the system id.
     */
    private String getSystemId( InstructionInfo info )
    {
        String id = info.getSystemId();
        id = id != null ? id : "unknown";

        if ( id.startsWith( DUMMY_PREFIX ) )
        {
            id = id.substring( DUMMY_PREFIX.length() );
        }

        return decode( id );
    }

    /**
     * Decode string.
     */
    private String decode( String value )
    {
        try
        {
            return URLDecoder.decode( value, "UTF-8" );
        }
        catch ( Exception e )
        {
            return value;
        }
    }
}
