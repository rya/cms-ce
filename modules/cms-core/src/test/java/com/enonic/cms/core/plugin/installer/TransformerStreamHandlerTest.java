package com.enonic.cms.core.plugin.installer;

import org.junit.Test;
import org.mockito.Mockito;
import java.net.URL;
import java.net.URLConnection;
import static org.junit.Assert.*;

public class TransformerStreamHandlerTest
{
    @Test
    public void testOpenConnection()
        throws Exception
    {
        final BundleTransformer transformer = Mockito.mock(BundleTransformer.class);
        final TransformerStreamHandler handler = new TransformerStreamHandler(transformer);

        // Replace "plugin" with "http" scheme to mock
        final URL url = new URL("http:file://dir/file.txt");

        final URLConnection conn = handler.openConnection(url);
        assertNotNull(conn);
        assertSame(TransformerUrlConnection.class, conn.getClass());
        assertEquals("file://dir/file.txt", conn.getURL().toExternalForm());
    }
}
