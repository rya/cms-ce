package com.enonic.cms.core.plugin.manager;

import com.enonic.cms.api.plugin.ext.Extension;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import static org.junit.Assert.*;
import java.util.List;

public class ExtensionTrackerTest
{
    private ExtensionTracker tracker;
    private ExtensionHolder holder;
    private BundleContext context;

    @Before
    public void setUp()
    {
        this.context = Mockito.mock(BundleContext.class);
        this.holder = new ExtensionHolder();
        this.tracker = new ExtensionTracker(this.context, this.holder);
    }

    @Test
    public void testAddRemove()
    {
        final Extension ext = Mockito.mock(Extension.class);
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        Mockito.when(this.context.getService(ref)).thenReturn(ext);

        this.tracker.addingService(ref);
        final List<Extension> list1 = this.holder.getAll();
        assertNotNull(list1);
        assertEquals(1, list1.size());

        this.tracker.removedService(ref, ext);
        final List<Extension> list2 = this.holder.getAll();
        assertNotNull(list2);
        assertEquals(0, list2.size());
    }
}
