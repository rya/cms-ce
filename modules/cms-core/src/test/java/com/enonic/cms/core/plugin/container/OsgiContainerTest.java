package com.enonic.cms.core.plugin.container;

import com.google.common.collect.Maps;
import org.junit.Test;
import static org.junit.Assert.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OsgiContainerTest
{
    private class MyContributor
        extends OsgiContributor
    {
        protected int started;
        protected int stopped;
        protected String propValue;

        public MyContributor()
        {
            super(0);
        }

        public void start(final BundleContext context)
        {
            this.started++;
            this.propValue = context.getProperty("propKey");
        }

        public void stop(final BundleContext context)
        {
            this.stopped++;
        }
    }

    @Test
    public void testStartup()
        throws Exception
    {
        final MyContributor contributor = new MyContributor();
        final List<OsgiContributor> contributors = Collections.singletonList((OsgiContributor)contributor);

        final Map<String, String> props = Maps.newHashMap();
        props.put("propKey", "propValue");

        final OsgiContainer container = new OsgiContainer();
        container.setContributors(contributors);
        container.setProperties(props);

        // Start - should start up contributors
        container.start();
        assertEquals(1, contributor.started);
        assertEquals(0, contributor.stopped);
        assertNotNull(contributor.propValue);
        assertEquals("propValue", contributor.propValue);

        // Stop - should stop contributors
        container.stop();
        assertEquals(1, contributor.started);
        assertEquals(1, contributor.stopped);
    }
}
