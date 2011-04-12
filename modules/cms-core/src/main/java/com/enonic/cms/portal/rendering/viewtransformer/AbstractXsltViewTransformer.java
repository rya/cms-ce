/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

import javax.xml.transform.TransformerException;

import com.enonic.cms.core.resource.ResourceKey;
import org.slf4j.Logger;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.XsltResource;

import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.portal.rendering.StyleSheetURIResolver;

/**
 * May 13, 2009
 */
public abstract class AbstractXsltViewTransformer

{
    protected final static String XSLT_NS = "http://www.w3.org/1999/XSL/Transform";

    protected StyleSheetURIResolver styleSheetURIResolver;

    protected ResourceService resourceService;

    protected void logXsltProcessorErrors( XsltProcessorErrors errors, Logger logger )
    {
        if ( errors == null )
        {
            return;
        }

        for ( TransformerException error : errors.getFatalErrors() )
        {
            logger.error( error.getMessageAndLocation() );
        }

        for ( TransformerException error : errors.getErrors() )
        {
            logger.error( error.getMessageAndLocation() );
        }

        for ( TransformerException error : errors.getWarnings() )
        {
            logger.warn( error.getMessageAndLocation() );
        }
    }

    protected XsltProcessor createProcessor( ResourceKey styleSheetKey, XMLDocument xslt )
        throws XsltProcessorException
    {
        XsltResource resource = new XsltResource( styleSheetKey.toString(), xslt.getAsString() );
        XsltProcessorManager manager = XsltProcessorManagerAccessor.getProcessorManager();
        return manager.createProcessor( resource, styleSheetURIResolver );
    }

    protected XsltProcessor createProcessor( ResourceKey styleSheetKey, XMLDocument xslt, boolean omitXmlDecl )
        throws XsltProcessorException
    {
        XsltProcessor processor;
        processor = createProcessor( styleSheetKey, xslt );
        processor.setOmitXmlDecl( omitXmlDecl );
        return processor;
    }


    protected void setup()
    {
        styleSheetURIResolver = new StyleSheetURIResolver( resourceService );
    }
}
