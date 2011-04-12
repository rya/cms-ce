/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

import com.enonic.cms.framework.util.UUIDGenerator;

/**
 * This class implements the abstract render trace info.
 */
public final class RenderTraceInfo
    extends TraceInfo
{
    /**
     * Id.
     */
    private final String key;

    /**
     * Page trace.
     */
    private PageTraceInfo pageInfo;

    /**
     * Construct the info.
     */
    public RenderTraceInfo()
    {
        this.key = UUIDGenerator.randomUUID();
    }

    /**
     * Return the uuid.
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Return the page trace info.
     */
    public PageTraceInfo getPageInfo()
    {
        return this.pageInfo;
    }

    /**
     * Set the page trace info.
     */
    public void setPageInfo( PageTraceInfo info )
    {
        this.pageInfo = info;
    }
}
