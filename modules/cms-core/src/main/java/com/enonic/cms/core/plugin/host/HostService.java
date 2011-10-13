package com.enonic.cms.core.plugin.host;

import java.util.Set;
import org.osgi.framework.BundleContext;
import com.google.common.collect.Sets;

final class HostService<T>
{
    private final T instance;
    private final Set<String> types;

    private HostService(final T instance)
    {
        this.instance = instance;
        this.types = Sets.newHashSet();
    }

    public HostService<T> type(final Class<? super T> type)
    {
        this.types.add(type.getName());
        return this;
    }

    public void register(final BundleContext context)
    {
        context.registerService(this.types.toArray(new String[this.types.size()]), instance, null );
    }

    public static <T> HostService<T> create(T instance)
    {
        return new HostService<T>(instance);
    }
}
