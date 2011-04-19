/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.base;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;

public abstract class BaseProcessorManager
    implements XsltProcessorManager
{
    @Override
    public final XsltProcessor createProcessor( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final Transformer transformer = createTransformer(xsl, resolver);
        transformer.setURIResolver( resolver );
        return new XsltProcessorImpl( transformer );
    }

    protected abstract TransformerFactory getTransformerFactory()
        throws XsltProcessorException;

    private Transformer createTransformer( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        final TransformerFactory factory = getTransformerFactory();
        factory.setErrorListener( errors );
        factory.setURIResolver( resolver );

        try {
            return factory.newTransformer(xsl);
        } catch (Exception e) {
            throw new XsltProcessorException( e, errors );
        }
    }
}
