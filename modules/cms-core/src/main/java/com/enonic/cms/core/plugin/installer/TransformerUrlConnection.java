package com.enonic.cms.core.plugin.installer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

final class TransformerUrlConnection
    extends URLConnection
{
    private final BundleTransformer transformer;

    public TransformerUrlConnection(final URL url, final BundleTransformer transformer)
    {
        super(url);
        this.transformer = transformer;
    }

    @Override
    public void connect()
    {
        // Do nothing
    }

    @Override
    public InputStream getInputStream()
        throws IOException
    {
        final InputStream in = this.url.openStream();

        try {
            return this.transformer.transform(in);
        } finally {
            in.close();
        }
    }
}
