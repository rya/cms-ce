/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

/**
 * This class implements the function trace info.
 */
public final class FunctionTraceInfo
    extends TraceInfo
{
    /**
     * Name of function.
     */
    private final String name;

    /**
     * Construct the info.
     */
    public FunctionTraceInfo( String name )
    {
        this.name = name;
    }

    /**
     * Return the name.
     */
    public String getName()
    {
        return this.name;
    }
}
