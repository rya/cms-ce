/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuilder;

public abstract class AbstractContentHandlerServlet
    extends AdminHandlerBaseServlet
{

    protected ContentXMLBuilder contentXMLBuilder;

    public void setContentXMLBuilder( ContentXMLBuilder contentXMLBuilder )
    {
        this.contentXMLBuilder = contentXMLBuilder;
    }

    public ContentXMLBuilder getContentXMLBuilder()
    {
        return contentXMLBuilder;
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
    }
}
