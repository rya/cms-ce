/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering;

/**
 * May 25, 2009
 */
public class ErrorRenderPortletResult
    extends RenderedWindowResult
{
    private Throwable error;

    public Throwable getError()
    {
        return error;
    }

    public void setError( Throwable error )
    {
        this.error = error;
    }
}
