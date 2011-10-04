package com.enonic.cms.core.image.cache;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageResponse;
import com.enonic.cms.framework.cache.CacheFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class WrappedImageCacheTest
{
    private ImageCache cache;
    private CacheFacade facade;

    @Before
    public void setUp()
    {
        this.facade = Mockito.mock(CacheFacade.class);
        this.cache = new WrappedImageCache(this.facade);
    }

    @Test
    public void testNotFound()
    {
        final ImageRequest req = new ImageRequest();
        req.setBlobKey("0123");

        final ImageResponse res = this.cache.get(req);
        assertNull(res);
    }

    @Test
    public void testFound()
    {
        Mockito.when(this.facade.get(null, "325fdd3a5ee3a92b5f420247771375c733638dee.png")).thenReturn(new byte[0]);

        final ImageRequest req = new ImageRequest();
        req.setBlobKey("0123");

        final ImageResponse res = this.cache.get(req);
        assertNotNull(res);
    }

    @Test
    public void testPut()
    {
        final ImageRequest req = new ImageRequest();
        req.setBlobKey("0123");

        final ImageResponse res = new ImageResponse("name", new byte[0], "png");
        this.cache.put(req, res);

        Mockito.verify(this.facade, Mockito.times(1))
                .put(null, "325fdd3a5ee3a92b5f420247771375c733638dee.png", new byte[0]);
    }
}
