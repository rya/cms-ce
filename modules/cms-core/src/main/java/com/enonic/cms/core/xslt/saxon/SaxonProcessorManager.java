/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.saxon;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;

import org.springframework.stereotype.Component;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.functions.JavaExtensionLibrary;

import com.enonic.cms.core.xslt.XsltProcessor;
import com.enonic.cms.core.xslt.XsltProcessorErrors;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManager;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.XsltResource;
import com.enonic.cms.core.xslt.lib.PortalFunctions;

/**
 * This class implements the standard xslt processor manager.
 */
@Component
public final class SaxonProcessorManager
    implements XsltProcessorManager
{
    private final TransformerFactoryImpl transformerFactory;

    public SaxonProcessorManager()
    {
        XsltProcessorManagerAccessor.setProcessorManager( this );
        this.transformerFactory = new TransformerFactoryImpl();

        final Configuration configuration = this.transformerFactory.getConfiguration();
        final FunctionLibraryList libraryList = new FunctionLibraryList();
        final JavaExtensionLibrary extensionLibrary = new JavaExtensionLibrary( configuration );
        libraryList.addFunctionLibrary( extensionLibrary );
        registerExtensions( extensionLibrary );

        configuration.setExtensionBinder( "java", libraryList );
        configuration.setLineNumbering( true );
        configuration.setHostLanguage( Configuration.XSLT );
        configuration.setVersionWarning( false );
    }

    private void registerExtensions(final JavaExtensionLibrary library)
    {
        library.declareJavaClass( PortalFunctions.NAMESPACE_URI, PortalFunctions.class );
        library.declareJavaClass( PortalFunctions.OLD_NAMESPACE_URI, PortalFunctions.class );
    }

    public XsltProcessor createProcessor( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final Transformer transformer = createTransformer(xsl, resolver);
        transformer.setURIResolver( resolver );
        return new XsltProcessorImpl( transformer );
    }

    public XsltProcessor createProcessor( final XsltResource xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        return createProcessor( xsl.getAsSource(), resolver );
    }

    private Transformer createTransformer( final Source xsl, final URIResolver resolver )
        throws XsltProcessorException
    {
        final XsltProcessorErrors errors = new XsltProcessorErrors();
        this.transformerFactory.setErrorListener( errors );
        this.transformerFactory.setURIResolver( resolver );

        try {
            return this.transformerFactory.newTransformer(xsl);
        } catch (Exception e) {
            throw new XsltProcessorException( e, errors );
        }
    }
}
