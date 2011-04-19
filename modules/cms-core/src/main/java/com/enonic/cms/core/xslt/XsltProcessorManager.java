/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

/**
 * This interface defines the xslt processor manager.
 */
public interface XsltProcessorManager
{
    public XsltProcessor createProcessor( Source xsl, URIResolver resolver )
        throws XsltProcessorException;
}
