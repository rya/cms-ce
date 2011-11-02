/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.AdminConsoleTranslationService;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.PagePortletTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.PageTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTraceInfo;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.portlet.PortletKey;

/**
 * This class implements the debug controller.
 */
public final class SiteDebugInfoController
    extends SiteDebugController
{

    private AdminService adminService;

    public void setAdminService( AdminService value )
    {
        this.adminService = value;
    }

    /**
     * Handle the request.
     */
    protected ModelAndView handleRequestInternal( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        String key = request.getParameter( "key" );
        String type = request.getParameter( "type" );

        if ( ( key == null ) || ( type == null ) )
        {
            return null;
        }

        RenderTraceInfo renderInfo = findRenderTraceInfo( key );
        if ( "javascript".equals( type ) )
        {
            serveJavaScriptFile( renderInfo, request, response );
        }
        else if ( "css".equals( type ) )
        {
            serveCssFile( response );
        }
        else if ( "renderXml".equals( type ) )
        {
            serveRenderXml( findDataTraceInfo( renderInfo, key ), response );
        }
        else if ( "renderTrace".equals( type ) )
        {
            serveRenderTrace( findDataTraceInfo( renderInfo, key ), response );
        }

        return null;
    }

    /**
     * Serve bootstrap info.
     */
    private void serveJavaScriptFile( RenderTraceInfo info, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        response.setContentType( "text/javascript; charset=utf-8" );
        response.setCharacterEncoding( "utf-8" );
        PrintWriter writer = new PrintWriter( response.getWriter() );
        writer.println( localizeScript( request, readStaticFile( "admin/ice/ice.js" ) ) );
        writer.println( "cms.ice.Setup.setBaseUrl('" + getAdminBaseUrl( request ) + "');" );
        writer.println( createSetPageInfoStatement( info.getKey(), info.getPageInfo(), request ) );

        for ( PagePortletTraceInfo objectInfo : info.getPageInfo().getPortlets() )
        {
            writer.println( createAddPageObjectInfoStatement( info.getKey() + "-" + objectInfo.getKey(), objectInfo, request ) );
        }

        writer.close();
    }

    /**
     * Create set page info statement.
     */
    private String createSetPageInfoStatement( String key, PageTraceInfo info, HttpServletRequest request )
    {
        String title = StringEscapeUtils.escapeJavaScript( info.getTitle() );
        String infoMenu = StringEscapeUtils.escapeJavaScript( createDataInfoMenu( key, info, request, true ) );

        StringBuffer str = new StringBuffer();
        str.append( "cms.ice.Setup.setPageInfo('" ).append( key ).append( "', '" );
        str.append( title ).append( "', \"" ).append( infoMenu ).append( "\");" );
        return str.toString();
    }

    /**
     * Create add page info statement.
     */
    private String createAddPageObjectInfoStatement( String key, PagePortletTraceInfo info, HttpServletRequest request )
    {
        String title = StringEscapeUtils.escapeJavaScript( info.getTitle() );
        String infoMenu = StringEscapeUtils.escapeJavaScript( createDataInfoMenu( key, info, request, false ) );

        StringBuffer str = new StringBuffer();
        str.append( "cms.ice.Setup.addPortletInfo('" ).append( key ).append( "', '" );
        str.append( title ).append( "', \"" ).append( infoMenu ).append( "\", " ).append( String.valueOf( info.isCacheable() ) ).append(
            ");" );
        return str.toString();
    }

    /**
     * Create data info menu.
     */
    private String createDataInfoMenu( String key, DataTraceInfo info, HttpServletRequest request, boolean isPage )
    {
        User user = securityService.getOldUserObject();
        SiteKey siteKey = info.getSiteKey();
        boolean hasAdmin = adminService.isSiteAdmin( user, siteKey );
        boolean hasDev = adminService.isDeveloper( user );

        String adminBaseUrl = getAdminBaseUrl( request );
        String baseUrl = getDebugBaseUrlWithHost( request, "__info__?key=" + key );
        StringBuffer str = new StringBuffer();

        // Header
        if ( isPage )
        {
            str.append( "<div style='float: left; width: 70%'><h3>" ).append( info.getTitle() ).append( "</h3></div>" );
            str.append( "<div style='float: left; width: 30%' id='ice-on-of-container'></div>" );
        }
        else
        {
            str.append( "<div><h3>" ).append( info.getTitle() ).append( "</h3></div>" );
        }

        if ( !info.getContentInfo().isEmpty() || hasAdmin )
        {
            str.append( "<div class='ice-divider'><!-- --></div>" );
        }

        // Content Wizard
        String contentWizardUrl = adminBaseUrl + "adminpage?page=960&op=createcontentwizard_step1&source=ice";

        str.append( "<a href='javascript:void(0)'" );
        str.append( " onclick='javascript:cms.ice.Utils.openWindow(\"" ).append( contentWizardUrl ).append( "\", 900, 600)'" );
        str.append( " style='background-image:url(" ).append( adminBaseUrl ).append( "ice/images/icon_state_unsaved_draft.gif);'" );
        str.append( ">" );
        str.append( "<span class='ice-lang-placeholder-create-content'><!-- --></span>" );
        str.append( "</a>" );

        if ( !info.getContentInfo().isEmpty() )
        {
            str.append( "<div class='ice-divider'><!-- --></div>" );
        }

        // Edit content
        if ( !info.getContentInfo().isEmpty() )
        {
            str.append( "<div class='ice-menu-item-content-container'>" );

            for ( int contentKey : info.getContentInfo().keySet() )
            {
                str.append( createEditContentLink( adminBaseUrl, info.getContentInfo().get( contentKey ), contentKey ) );
            }

            str.append( "</div>" );
        }

        // View render XML and View render trace
        if ( hasAdmin || hasDev )
        {
            boolean showDivider = !info.getContentInfo().isEmpty() && hasAdmin || hasDev;
            if ( showDivider )
            {
                str.append( "<div class='ice-divider'><!-- --></div>" );
            }

            String menuItemViewTraceText = isPage
                ? "<span class='ice-lang-placeholder-page-trace'><!-- --></span>"
                : "<span class='ice-lang-placeholder-portlet-trace'><!-- --></span>";
            String menuItemViewXMLText = isPage
                ? "<span class='ice-lang-placeholder-page-xml'><!-- --></span>"
                : "<span class='ice-lang-placeholder-portlet-xml'><!-- --></span>";

            str.append( createMenuInfoLink( menuItemViewTraceText, baseUrl + "&type=renderTrace", "_blank",
                                            adminBaseUrl + "ice/images/icon_xml.gif", null ) );
            str.append(
                createMenuInfoLink( menuItemViewXMLText, baseUrl + "&type=renderXml", "_blank", adminBaseUrl + "ice/images/icon_xml.gif",
                                    null ) );
        }

        // Edit object
        if ( info instanceof PagePortletTraceInfo && hasAdmin )
        {
            PagePortletTraceInfo pageObjectTraceInfo = (PagePortletTraceInfo) info;
            SiteKey pageObjectSiteKey = pageObjectTraceInfo.getSiteKey();
            PortletKey portletKey = pageObjectTraceInfo.getKey();
            String editPortletUrl =
                adminBaseUrl + "adminpage?page=900&op=form&subop=popup&key=" + portletKey + "&menukey=" + pageObjectSiteKey.toInt() +
                    "&fieldname=null&fieldrow=-1&callback=cms.ice.Utils.reloadPage";

            if ( hasDev )
            {
                str.append( "<div class='ice-divider'><!-- --></div>" );
            }

            str.append( "<a href='javascript:void(0)'" );
            str.append( " onclick='cms.ice.Utils.openWindow(\"" ).append( editPortletUrl ).append( "\", 900, 600)'" );
            str.append( " style='background-image:url(" ).append( adminBaseUrl ).append( "ice/images/icon_object.gif);'>" );
            str.append( "<span class='ice-lang-placeholder-edit-portlet'><!-- --></span>" );
            str.append( "</a>" );
        }

        return str.toString();
    }

    /**
     * Create edit content link.
     */
    private String createEditContentLink( String baseUrl, String name, int contentKey )
    {
        StringBuffer str = new StringBuffer();
        String editContentUrl = baseUrl + "adminpage?page=993&op=form&key=" + contentKey +
            "&versionkey=-1&subop=popup&fieldname=null&fieldrow=-1&callback=cms.ice.Utils.reloadPage&logread=true";

        str.append( "<a href='javascript: void(0)'" );
        str.append( " onclick='javascript: cms.ice.Utils.openWindow(\"" ).append( editContentUrl ).append( "\", 900, 600)'" );
        str.append( " style='background-image: url(" ).append( baseUrl ).append( "ice/images/edit.gif);'>" );
        str.append( name );
        str.append( "</a>" );

        return str.toString();
    }

    /**
     * Create info menu link.
     */
    private String createMenuInfoLink( String name, String link, String target, String pathToIcon, String onClick )
    {
        StringBuffer str = new StringBuffer();
        if ( link == null )
        {
            link = "void(0)";
        }

        str.append( "<a href='" ).append( link ).append( "'" );

        if ( target != null )
        {
            str.append( " target='" ).append( target ).append( "'" );
        }

        if ( onClick != null )
        {
            str.append( " onclick='" ).append( onClick ).append( "'" );
        }

        if ( pathToIcon != null )
        {
            str.append( " style='background-image:url(" ).append( pathToIcon ).append( ");'" );
        }

        str.append( ">" ).append( name ).append( "</a>" );
        return str.toString();
    }

    /**
     * Serve bootstrap info.
     */
    private void serveCssFile( HttpServletResponse response )
        throws Exception
    {
        response.setContentType( "text/css" );
        PrintWriter writer = new PrintWriter( response.getWriter() );
        writer.println( readStaticFile( "admin/ice/ice.css" ) );
        writer.close();
    }

    /**
     * Read external javascript file.
     */
    private String readStaticFile( String path )
        throws Exception
    {
        return readStaticFile( new ServletContextResource( getServletContext(), path ) );
    }

    /**
     * Translate javascript localization.
     */
    private String localizeScript( HttpServletRequest request, String script )
        throws Exception
    {
        String languageCode = (String) request.getSession( true ).getAttribute( "languageCode" );
        Map translationMap = AdminConsoleTranslationService.getInstance().getTranslationMap( languageCode );
        StringWriter result = new StringWriter();
        StringReader source = new StringReader( script );
        TranslationWriter dest = new TranslationWriter( translationMap, result );
        FileCopyUtils.copy( source, dest );
        return result.toString();
    }

    /**
     * Read external javascript file.
     */
    private String readStaticFile( Resource path )
        throws Exception
    {
        StringWriter out = new StringWriter();
        InputStreamReader in = new InputStreamReader( path.getInputStream() );
        FileCopyUtils.copy( in, out );
        return out.getBuffer().toString();
    }

    /**
     * Find render trace info.
     */
    private RenderTraceInfo findRenderTraceInfo( String key )
    {
        int pos = key.indexOf( '-' );
        if ( pos > -1 )
        {
            key = key.substring( 0, pos );
        }

        return RenderTrace.getRenderTraceInfo( key );
    }

    /**
     * Find render trace info.
     */
    private DataTraceInfo findDataTraceInfo( RenderTraceInfo info, String key )
    {
        PageTraceInfo pageInfo = info.getPageInfo();
        int pos = key.indexOf( '-' );
        if ( pos > -1 )
        {
            return pageInfo.getPortlet( new PortletKey( key.substring( pos + 1 ) ) );
        }
        else
        {
            return pageInfo;
        }
    }

    /**
     * Serve render xml.
     */
    private void serveRenderXml( DataTraceInfo info, HttpServletResponse response )
        throws Exception
    {
        response.setContentType( "text/xml; charset=UTF-8" );
        response.getWriter().println( info.getDataSourceResult().getAsString() );
    }

    /**
     * Serve render trace.
     */
    private void serveRenderTrace( DataTraceInfo info, HttpServletResponse response )
        throws Exception
    {
        response.setContentType( "text/xml; charset=UTF-8" );
        response.getWriter().println( info.getRenderTraceAsXml().getAsString() );
    }
}
