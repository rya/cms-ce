/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.core.service.AdminService;

public abstract class AbstractBaseXMLBuilder
{
    protected AdminService admin;

    protected VerticalProperties verticalProperties;

    public void setVerticalProperties( VerticalProperties value )
    {
        this.verticalProperties = value;
    }

    public void setAdminService( AdminService value )
    {
        this.admin = value;
    }
}
