/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;

public class ContentOrderHandlerServlet
    extends ContentBaseHandlerServlet
{

    public ContentOrderHandlerServlet()
    {
        super();

        FORM_XSL = "order_form.xsl";

        extraFormXMLFiles.add( "order_statuses.xml" );
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getContentOrderXMLBuilder() );
    }
}
