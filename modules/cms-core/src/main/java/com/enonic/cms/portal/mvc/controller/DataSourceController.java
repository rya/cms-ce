/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.enonic.cms.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.PageTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.RenderTraceInfo;
import com.enonic.cms.domain.structure.portlet.PortletKey;

/**
 * This class implements the data source controller.
 */
public final class DataSourceController
    extends AbstractController
{

    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        String infoKey = request.getParameter( "key" );
        PortletKey portletKey = null;

        if ( infoKey == null )
        {
            return null;
        }

        int index = infoKey.indexOf( ':' );
        if ( index > -1 )
        {
            portletKey = new PortletKey( infoKey.substring( index + 1 ) );
            infoKey = infoKey.substring( 0, index );
        }

        RenderTraceInfo traceInfo = RenderTrace.getRenderTraceInfo( infoKey );
        if ( traceInfo != null )
        {
            serializeDataSource( response, traceInfo.getPageInfo(), portletKey );
        }

        return null;
    }

    private void serializeXml( HttpServletResponse response, Document doc )
        throws Exception
    {
        response.setContentType( "text/xml; charset=UTF-8" );
        Writer writer = response.getWriter();
        XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat().setOmitDeclaration( false ) );
        outputter.output( doc, writer );
        writer.close();
    }

    private void serializeDataSource( HttpServletResponse response, PageTraceInfo info, PortletKey portletKey )
        throws Exception
    {
        DataTraceInfo traceInfo = info;
        if ( portletKey != null )
        {
            traceInfo = info.getPortlet( portletKey );
        }

        serializeXml( response, traceInfo.getDataSourceResult().getAsJDOMDocument() );
    }
}
