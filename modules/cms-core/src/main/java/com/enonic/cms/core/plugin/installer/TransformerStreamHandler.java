package com.enonic.cms.core.plugin.installer;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.osgi.service.url.AbstractURLStreamHandlerService;

final class TransformerStreamHandler
    extends AbstractURLStreamHandlerService
{
    public final static String SCHEME = "plugin";

    private final BundleTransformer transformer;

    public TransformerStreamHandler(final BundleTransformer transformer)
    {
        this.transformer = transformer;
    }

    @Override
    public URLConnection openConnection(final URL url)
        throws IOException
    {
        final URL actualUrl = new URL(url.getPath());
        return new TransformerUrlConnection(actualUrl, this.transformer);
    }
}
