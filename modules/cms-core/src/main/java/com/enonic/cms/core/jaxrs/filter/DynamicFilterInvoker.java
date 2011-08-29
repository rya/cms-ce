package com.enonic.cms.core.jaxrs.filter;

import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public final class DynamicFilterInvoker
    implements ContainerRequestFilter, ContainerResponseFilter
{
    private final List<DynamicFilter> filters;

    public DynamicFilterInvoker()
    {
        this.filters = Lists.newArrayList();
    }

    @Autowired(required = false)
    public void setFilters(final List<DynamicFilter> filters)
    {
        this.filters.clear();
        this.filters.addAll(filters);
        Collections.sort(this.filters);
    }

    public ContainerRequest filter(ContainerRequest request)
    {
        for (final DynamicFilter filter : this.filters) {
            request = filter.filter(request);
        }

        return request;
    }

    public ContainerResponse filter(final ContainerRequest request, ContainerResponse response)
    {
        for (final DynamicFilter filter : this.filters) {
            response = filter.filter(request, response);
        }

        return response;
    }
}
