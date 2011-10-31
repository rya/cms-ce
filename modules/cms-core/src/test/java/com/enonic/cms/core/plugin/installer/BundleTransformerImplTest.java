package com.enonic.cms.core.plugin.installer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.io.IOException;

public class BundleTransformerImplTest
{
    private BundleTransformerImpl transformer;

    @Before
    public void setUp()
    {
        final ManifestTransformer manifestTransformer = Mockito.mock(ManifestTransformer.class);
        this.transformer = new BundleTransformerImpl(manifestTransformer);
    }

    @Test(expected = IOException.class)
    public void testIllegalPlugin()
        throws Exception
    {
        this.transformer.transform(getClass().getResourceAsStream("BundleTransformerImplTest.class"));
    }
}
