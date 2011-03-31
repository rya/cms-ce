/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.enonic.vertical.adminweb.handlers.xmlbuilders.ContentXMLBuildersSpringManagedBeansBridge;

public class ContentLeadsHandlerServlet
    extends ContentBaseHandlerServlet
{

    public ContentLeadsHandlerServlet()
    {
        FORM_XSL = "leads_form.xsl";

        extraFormXMLFiles.add( "countries.xml" );
    }

    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );
        setContentXMLBuilder( ContentXMLBuildersSpringManagedBeansBridge.getContentLeadsXMLBuilder() );
    }

}
