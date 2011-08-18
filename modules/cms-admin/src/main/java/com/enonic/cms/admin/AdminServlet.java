package com.enonic.cms.admin;

import com.enonic.cms.core.jaxrs.SpringRestServlet;

public final class AdminServlet
    extends SpringRestServlet
{
    public AdminServlet()
    {
        super("com.enonic.cms.admin");
    }
}
