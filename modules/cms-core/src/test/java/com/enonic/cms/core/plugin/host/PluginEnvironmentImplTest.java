package com.enonic.cms.core.plugin.host;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

import static org.junit.Assert.*;

public class PluginEnvironmentImplTest
{
    private PluginEnvironmentImpl env;

    @Before
    public void setUp()
    {
        this.env = new PluginEnvironmentImpl();
    }

    @Test
    public void testSharedObject()
    {
        assertNull(this.env.getSharedObject("key"));

        this.env.setSharedObject("key", "value");
        assertNotNull(this.env.getSharedObject("key"));
        assertEquals("value", this.env.getSharedObject("key"));
    }

    @Test
    public void testSharedObjectNames()
    {
        final Set<String> set1 = this.env.getSharedObjectNames(null);
        assertNotNull(set1);
        assertEquals(0, set1.size());

        this.env.setSharedObject("key", "value");
        this.env.setSharedObject("prefix.key", "value");

        final Set<String> set2 = this.env.getSharedObjectNames(null);
        assertNotNull(set2);
        assertEquals(2, set2.size());

        final Set<String> set3 = this.env.getSharedObjectNames("prefix.");
        assertNotNull(set3);
        assertEquals(1, set3.size());

        final Set<String> set4 = this.env.getSharedObjectNames("unknown.");
        assertNotNull(set4);
        assertEquals(0, set4.size());
    }

    @Test
    public void testCurrentRequest()
    {
        ServletRequestAccessor.setRequest(null);
        assertNull(this.env.getCurrentRequest());

        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        ServletRequestAccessor.setRequest(req);

        assertSame(req, this.env.getCurrentRequest());
    }

    @Test
    public void testCurrentSession()
    {
        ServletRequestAccessor.setRequest(null);
        assertNull(this.env.getCurrentSession());

        final HttpSession session = Mockito.mock(HttpSession.class);
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getSession(true)).thenReturn(session);
        ServletRequestAccessor.setRequest(req);

        assertSame(session, this.env.getCurrentSession());
    }
}
