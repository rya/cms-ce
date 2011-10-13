package com.enonic.cms.core.plugin.container;

import org.junit.Test;
import static org.junit.Assert.*;
import org.osgi.framework.BundleContext;

public class OsgiContributorTest
{
    @Test
    public void testCompare()
    {
        final OsgiContributor c1 = newContributor(0);
        final OsgiContributor c2 = newContributor(1);
        final OsgiContributor c3 = newContributor(2);

        assertEquals(0, c1.compareTo(c1));
        assertEquals(1, c2.compareTo(c1));
        assertEquals(2, c3.compareTo(c1));
        assertEquals(-1, c1.compareTo(c2));
    }

    private OsgiContributor newContributor(final int rank)
    {
        return new OsgiContributor(rank)
        {
            public void start(final BundleContext context)
            {
            }

            public void stop(final BundleContext context)
            {
            }
        };
    }
}
