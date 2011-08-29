package com.enonic.cms.core.jaxrs.filter;

import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public abstract class DynamicFilter
    implements ContainerRequestFilter, ContainerResponseFilter, Comparable<DynamicFilter>
{
    private final int priority;

    public DynamicFilter()
    {
        this(0);
    }

    public DynamicFilter(final int priority)
    {
        this.priority = priority;
    }

    public int compareTo(final DynamicFilter filter)
    {
        return this.priority - filter.priority;
    }
}
