/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines the tools access resolver.
 */
public interface ToolsAccessResolver
{
    /**
     * Return true if it has access.
     */
    public boolean hasAccess( HttpServletRequest req );

    /**
     * Return the error message.
     */
    public String getErrorMessage( HttpServletRequest req );
}
