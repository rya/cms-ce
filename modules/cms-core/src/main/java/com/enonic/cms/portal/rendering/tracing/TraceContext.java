/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

import java.util.Stack;

/**
 * This class implements the abstract render trace context.
 */
public final class TraceContext
{
    /**
     * Render trace info.
     */
    private final RenderTraceInfo info;

    /**
     * Stack of data trace info.
     */
    private final Stack<PagePortletTraceInfo> pageInfoStack;

    /**
     * Stack of data trace info.
     */
    private final Stack<FunctionTraceInfo> functionInfoStack;

    /**
     * Construct the context.
     */
    public TraceContext( RenderTraceInfo info )
    {
        this.info = info;
        this.pageInfoStack = new Stack<PagePortletTraceInfo>();
        this.functionInfoStack = new Stack<FunctionTraceInfo>();
    }

    /**
     * Return the render trace info.
     */
    public RenderTraceInfo getRenderTraceInfo()
    {
        return this.info;
    }

    /**
     * Return the page trace info.
     */
    public PageTraceInfo getPageTraceInfo()
    {
        return this.info.getPageInfo();
    }

    /**
     * Add page trace info.
     */
    public void setPageTraceInfo( PageTraceInfo info )
    {
        this.info.setPageInfo( info );
    }

    /**
     * Add page object trace info.
     */
    public void pushPageObjectTraceInfo( PagePortletTraceInfo info )
    {
        PageTraceInfo pageInfo = getPageTraceInfo();
        if ( pageInfo != null )
        {
            pageInfo.addPortlet( info );
        }
        this.pageInfoStack.push( info );
    }

    /**
     * Pop page object trace info.
     */
    public PagePortletTraceInfo popPageObjectTraceInfo()
    {
        return this.pageInfoStack.pop();
    }

    /**
     * Add page object trace info.
     */
    public void pushFunctionTraceInfo( FunctionTraceInfo info )
    {
        getCurrentDataTraceInfo().addFunction( info );
        this.functionInfoStack.push( info );
    }

    /**
     * Pop page object trace info.
     */
    public FunctionTraceInfo popFunctionTraceInfo()
    {
        return this.functionInfoStack.pop();
    }

    /**
     * Return the current data trace info.
     */
    public DataTraceInfo getCurrentDataTraceInfo()
    {
        PagePortletTraceInfo objectInfo = getCurrentPageObjectTraceInfo();
        if ( objectInfo != null )
        {
            return objectInfo;
        }
        else
        {
            return this.info.getPageInfo();
        }
    }

    /**
     * Return the current page object trace info.
     */
    public PagePortletTraceInfo getCurrentPageObjectTraceInfo()
    {
        if ( !this.pageInfoStack.isEmpty() )
        {
            return this.pageInfoStack.peek();
        }
        else
        {
            return null;
        }
    }

    /**
     * Return the current function trace info.
     */
    public FunctionTraceInfo getCurrentFunctionTraceInfo()
    {
        if ( !this.functionInfoStack.isEmpty() )
        {
            return this.functionInfoStack.peek();
        }
        else
        {
            return null;
        }
    }
}
