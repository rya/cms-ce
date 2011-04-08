/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.domain.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.FunctionTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.PagePortletTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.PageTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.RenderTraceInfo;
import com.enonic.cms.domain.portal.rendering.tracing.TraceContext;
import com.enonic.cms.domain.structure.portlet.PortletKey;

/**
 * This class manages the rendering trace.
 */
public final class RenderTrace
{
    /**
     * Context key.
     */
    private final static String CONTEXT_KEY = TraceContext.class.getName();

    /**
     * A list of render traces.
     */
    private final static LinkedList<RenderTraceInfo> HISTORY = new LinkedList<RenderTraceInfo>();

    /**
     * History size.
     */
    private final static int HISTORY_SIZE = 1000;

    /**
     * Return the current trace context.
     */
    public static TraceContext getCurrentTraceContext()
    {
        return (TraceContext) getCurrentRequest().getAttribute( CONTEXT_KEY );
    }

    /**
     * Return the current request.
     */
    private static HttpServletRequest getCurrentRequest()
    {
        return ServletRequestAccessor.getRequest();
    }

    /**
     * Add render context.
     */
    private synchronized static void addTraceContext( RenderTraceInfo info )
    {
        TraceContext context = new TraceContext( info );
        getCurrentRequest().setAttribute( CONTEXT_KEY, context );

        HISTORY.addFirst( info );
        if ( HISTORY.size() > HISTORY_SIZE )
        {
            HISTORY.removeLast();
        }
    }

    /**
     * Start render trace.
     */
    public static RenderTraceInfo enter()
    {
        RenderTraceInfo info = new RenderTraceInfo();
        addTraceContext( info );
        info.enter();
        return info;
    }

    /**
     * Stop render trace.
     */
    public static RenderTraceInfo exit()
    {
        RenderTraceInfo info = getCurrentRenderTraceInfo();
        getCurrentRequest().removeAttribute( CONTEXT_KEY );
        return info;
    }

    /**
     * Enter page trace.
     */
    public static PageTraceInfo enterPage( int key )
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PageTraceInfo info = new PageTraceInfo( key );
            context.setPageTraceInfo( info );
            info.enter();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Exit page trace.
     */
    public static PageTraceInfo exitPage()
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PageTraceInfo info = context.getPageTraceInfo();
            info.exit();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Enter page object trace.
     */
    public static PagePortletTraceInfo enterPageObject( PortletKey key )
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PagePortletTraceInfo info = new PagePortletTraceInfo( key );
            context.pushPageObjectTraceInfo( info );
            info.enter();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Exit page object trace.
     */
    public static PagePortletTraceInfo exitPageObject()
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PagePortletTraceInfo info = context.popPageObjectTraceInfo();
            info.exit();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Enter function trace.
     */
    public static FunctionTraceInfo enterFunction( String name )
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            FunctionTraceInfo info = new FunctionTraceInfo( name );
            context.pushFunctionTraceInfo( info );
            info.enter();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Exit function trace.
     */
    public static FunctionTraceInfo exitFunction()
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            FunctionTraceInfo info = context.popFunctionTraceInfo();
            info.exit();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Return true if it is inside render trace.
     */
    public static boolean isTraceOn()
    {
        return getCurrentTraceContext() != null;
    }

    public static boolean isTraceOff()
    {
        return !isTraceOn();
    }

    /**
     * Return the current render trace info.
     */
    public static RenderTraceInfo getCurrentRenderTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getRenderTraceInfo() : null;
    }

    /**
     * Return the current render trace info.
     */
    public static PageTraceInfo getCurrentPageTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getPageTraceInfo() : null;
    }

    /**
     * Return the current render trace info.
     */
    public static PagePortletTraceInfo getCurrentPageObjectTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getCurrentPageObjectTraceInfo() : null;
    }

    /**
     * Return the current render trace info.
     */
    public static DataTraceInfo getCurrentDataTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getCurrentDataTraceInfo() : null;
    }

    /**
     * Return the current render trace info.
     */
    public static FunctionTraceInfo getCurrentFunctionTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getCurrentFunctionTraceInfo() : null;
    }

    /**
     * Return a render trace info by key.
     */
    public synchronized static RenderTraceInfo getRenderTraceInfo( String key )
    {
        for ( RenderTraceInfo info : HISTORY )
        {
            if ( info.getKey().equals( key ) )
            {
                return info;
            }
        }
        return null;
    }
}
