package com.enonic.cms.core.plugin.installer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.URL;

public class TransformerUrlConnectionTest
{
    private TransformerUrlConnection connection;
    private InputStream mockStream;

    @Before
    public void setUp()
        throws Exception
    {
        // Use a class instead of plugin to mock input stream
        final URL pluginUrl = getClass().getResource("TransformerUrlConnection.class");
        assertNotNull(pluginUrl);

        this.mockStream = Mockito.mock(InputStream.class);

        final BundleTransformer transformer = Mockito.mock(BundleTransformer.class);
        Mockito.when(transformer.transform(Mockito.any(InputStream.class))).thenReturn(this.mockStream);

        this.connection = new TransformerUrlConnection(pluginUrl, transformer);
    }

    @Test
    public void testConnect()
    {
        this.connection.connect();
    }

    @Test
    public void testGetInputStream()
        throws Exception
    {
        final InputStream in = this.connection.getInputStream();

        assertNotNull(in);
        assertSame(this.mockStream, in);
    }
}
