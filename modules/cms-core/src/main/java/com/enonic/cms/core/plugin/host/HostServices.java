package com.enonic.cms.core.plugin.host;

import java.util.Iterator;
import java.util.List;
import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.plugin.PluginEnvironment;
import com.google.common.collect.Lists;

final class HostServices
    implements Iterable<HostService>
{
    private List<HostService> services;

    public HostServices()
    {
        this.services = Lists.newArrayList();
    }

    public void register(final LocalClient service)
    {
        service(service).type(LocalClient.class).type(Client.class);
    }

    public void register(final PluginEnvironment service)
    {
        service(service).type(PluginEnvironment.class);
    }

    private <T> HostService<T> service(final T instance)
    {
        final HostService<T> service = HostService.create(instance);
        this.services.add(service);
        return service;
    }

    public Iterator<HostService> iterator()
    {
        return this.services.iterator();
    }
}
