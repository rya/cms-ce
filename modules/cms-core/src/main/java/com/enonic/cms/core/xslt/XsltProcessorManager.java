/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

/**
 * This interface defines the xslt processor manager.
 */
public interface XsltProcessorManager
{
    public XsltProcessor createProcessor( XsltResource xslt )
        throws XsltProcessorException;
}
