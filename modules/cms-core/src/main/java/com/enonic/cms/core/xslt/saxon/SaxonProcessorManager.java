/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.saxon;

import javax.xml.transform.TransformerFactory;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.functions.JavaExtensionLibrary;

import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorManagerAccessor;
import com.enonic.cms.core.xslt.base.BaseProcessorManager;
import com.enonic.cms.core.xslt.lib.PortalFunctions;
import com.enonic.cms.core.xslt.trace.NopTraceListener;
import com.enonic.cms.core.xslt.trace.RecursionTraceListener;

/**
 * This class implements the standard xslt processor manager.
 */
public final class SaxonProcessorManager
    extends BaseProcessorManager
{
    private final TransformerFactoryImpl transformerFactory;
    private final Configuration configuration;

    public SaxonProcessorManager()
    {
        XsltProcessorManagerAccessor.setProcessorManager( this );
        this.transformerFactory = new TransformerFactoryImpl();
        this.configuration = this.transformerFactory.getConfiguration();

        final FunctionLibraryList libraryList = new FunctionLibraryList();
        final JavaExtensionLibrary extensionLibrary = new JavaExtensionLibrary( this.configuration );
        libraryList.addFunctionLibrary( extensionLibrary );
        registerExtensions( extensionLibrary );

        this.configuration.setExtensionBinder( "java", libraryList );
        this.configuration.setLineNumbering( true );
        this.configuration.setHostLanguage( Configuration.XSLT );
        this.configuration.setVersionWarning( false );

        setMaxRecursionDepth( -1 );
    }

    private void registerExtensions(final JavaExtensionLibrary library)
    {
        library.declareJavaClass( PortalFunctions.NAMESPACE_URI, PortalFunctions.class );
        library.declareJavaClass( PortalFunctions.OLD_NAMESPACE_URI, PortalFunctions.class );
    }

    @Override
    protected TransformerFactory getTransformerFactory()
        throws XsltProcessorException
    {
        return this.transformerFactory;
    }

    /**
     * Set max recursion depth (-1 for unlimited).
     */
    public void setMaxRecursionDepth( int maxRecursionDepth )
    {
        if ( maxRecursionDepth > 0 )
        {
            this.configuration.setTraceListener( new RecursionTraceListener( maxRecursionDepth ) );
        }
        else
        {
            this.configuration.setTraceListener( new NopTraceListener() );
        }
    }
}
