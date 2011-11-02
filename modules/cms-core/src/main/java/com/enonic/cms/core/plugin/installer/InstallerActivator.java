package com.enonic.cms.core.plugin.installer;

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class InstallerActivator
    extends OsgiContributor
{
    public InstallerActivator()
    {
        super(1);
    }

    public void start(final BundleContext context)
        throws Exception
    {
        final ManifestTransformer manifestTransformer = new ManifestTransformerImpl();
        final BundleTransformer bundleTransformer = new BundleTransformerImpl(manifestTransformer);

        final TransformerStreamHandler handler = new TransformerStreamHandler(bundleTransformer);
        final Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(URLConstants.URL_HANDLER_PROTOCOL, new String[] { TransformerStreamHandler.SCHEME });
        context.registerService(URLStreamHandlerService.class.getName(), handler, props);

        final BundleInstaller installer = new BundleInstallerImpl(context);
        context.registerService(BundleInstaller.class.getName(), installer, null);
    }

    public void stop(final BundleContext context)
        throws Exception
    {
        // Do nothing
    }
}
