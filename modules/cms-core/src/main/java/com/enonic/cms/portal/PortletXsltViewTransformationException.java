/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;

/**
 * Apr 28, 2009
 */
public class PortletXsltViewTransformationException
    extends RuntimeException
{
    private XsltProcessorException xsltProcessorException;

    public PortletXsltViewTransformationException( String message, XsltProcessorException exception )
    {
        super( message, exception );
        this.xsltProcessorException = exception;
    }

    public XsltProcessorErrors getErrors()
    {
        return xsltProcessorException.getErrors();
    }
}