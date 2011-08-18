package com.enonic.cms.portal;

import com.enonic.cms.core.jaxrs.SpringRestServlet;

public final class PortalServlet
    extends SpringRestServlet
{
    public PortalServlet()
    {
        super("com.enonic.cms.portal.v2");
    }
}
