/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.base;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import com.enonic.cms.core.xslt.*;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseProcessorManager
    implements XsltProcessorManager
{
    @Autowired
    public XsltResourceLoader loader;

    public final XsltProcessor createProcessor( final XsltResource xslt )
        throws XsltProcessorException
    {
        final LoaderURIResolver resolver = new LoaderURIResolver(this.loader, xslt.getPath());
        final Transformer transformer = createTransformer(xslt.getSource(), resolver);
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
        factory.setURIResolver(resolver);

        try {
            return factory.newTransformer(xsl);
        } catch (Exception e) {
            throw new XsltProcessorException( e, errors );
        }
    }
}
