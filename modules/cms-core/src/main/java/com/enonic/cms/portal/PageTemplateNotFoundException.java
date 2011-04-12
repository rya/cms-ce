/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.domain.NotFoundErrorType;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

/**
 * Aug 28, 2009
 */
public class PageTemplateNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{

    public PageTemplateNotFoundException( SitePath sitePath )
    {
        super( "Could not find any page template for path '" + sitePath.getLocalPath().toString() + "' on site: " + sitePath.getSiteKey() );
    }
}
