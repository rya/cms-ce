package com.enonic.cms.core.plugin.manager;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ExtensionListener;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.List;

import static org.junit.Assert.*;

public class ExtensionHolderTest
{
    private class MyListener
        implements ExtensionListener
    {
        protected Extension added;
        protected Extension removed;

        public void extensionAdded(final Extension ext)
        {
            this.added = ext;
        }

        public void extensionRemoved(final Extension ext)
        {
            this.removed = ext;
        }
    }

    private ExtensionHolder holder;
    private BundleContext context;

    @Before
    public void setUp()
    {
        this.context = Mockito.mock(BundleContext.class);
        this.holder = new ExtensionHolder();
    }

    @Test
    public void testEmpty()
    {
        final List<Extension> list1 = this.holder.getAll();
        assertNotNull(list1);
        assertEquals(0, list1.size());
    }

    @Test
    public void testAddRemove()
    {
        final Extension ext = Mockito.mock(Extension.class);
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        Mockito.when(this.context.getService(ref)).thenReturn(ext);

        this.holder.add(ref, ext);
        final List<Extension> list1 = this.holder.getAll();
        assertNotNull(list1);
        assertEquals(1, list1.size());

        this.holder.remove(ref);
        final List<Extension> list2 = this.holder.getAll();
        assertNotNull(list2);
        assertEquals(0, list2.size());
    }

    @Test
    public void testRemoveNotFound()
    {
        final ServiceReference ref = Mockito.mock(ServiceReference.class);

        this.holder.remove(ref);
        final List<Extension> list = this.holder.getAll();
        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testListener()
    {
        final Extension ext = Mockito.mock(Extension.class);
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        Mockito.when(this.context.getService(ref)).thenReturn(ext);

        final MyListener listener = new MyListener();
        final List<ExtensionListener> list = Lists.newArrayList((ExtensionListener)listener);
        this.holder.setListeners(list);

        this.holder.add(ref, ext);
        assertSame(ext, listener.added);

        this.holder.remove(ref);
        assertSame(ext, listener.removed);
    }

    @Test
    public void testGetAllForBundle()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = Mockito.mock(Extension.class);

        final Bundle bundle1 = Mockito.mock(Bundle.class);
        Mockito.when(bundle1.getBundleId()).thenReturn(1L);

        final Bundle bundle2 = Mockito.mock(Bundle.class);
        Mockito.when(bundle2.getBundleId()).thenReturn(2L);

        final ServiceReference ref1 = Mockito.mock(ServiceReference.class);
        Mockito.when(ref1.getBundle()).thenReturn(bundle1);

        final ServiceReference ref2 = Mockito.mock(ServiceReference.class);
        Mockito.when(ref2.getBundle()).thenReturn(bundle2);

        Mockito.when(this.context.getService(ref1)).thenReturn(ext1);
        Mockito.when(this.context.getService(ref2)).thenReturn(ext2);

        final List<Extension> list1 = this.holder.getAllForBundle(bundle1);
        assertNotNull(list1);
        assertEquals(0, list1.size());

        this.holder.add(ref1, ext1);
        this.holder.add(ref2, ext2);

        final List<Extension> list2 = this.holder.getAllForBundle(bundle1);
        assertNotNull(list2);
        assertEquals(1, list2.size());
        assertSame(ext1, list2.get(0));
    }
}
