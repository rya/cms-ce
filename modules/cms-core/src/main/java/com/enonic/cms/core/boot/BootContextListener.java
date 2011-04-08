/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import javax.servlet.ServletContextEvent;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import net.sf.saxon.Configuration;

import com.enonic.cms.core.boot.support.ClassLoadedFromWebAppVerifier;

/**
 * This class implements the bootstrap context listener.
 */
public final class BootContextListener
        extends ContextLoaderListener
{

    private static final Logger LOG = LoggerFactory.getLogger( BootContextListener.class );

    /**
     * Initialize the root web application context.
     */
    public void contextInitialized( ServletContextEvent event )
    {
        verifyJdkVersion();
        verifyJDOM();
        verifySaxon();

        super.contextInitialized( event );
    }

    private void verifyJdkVersion()
    {
        final String version = System.getProperty( "java.specification.version" );
        if ( !version.startsWith( "1.6" ) && !version.startsWith( "1.7" ) )
        {
            reportVerificationError( "Java Version must be 1.6 or greater" );
        }
    }

    private void verifyJDOM()
    {
        checkLocation( "JDOM", Document.class );
    }

    private void verifySaxon()
    {
        checkLocation( "Saxon", Configuration.class );
    }

    private void checkLocation( String subject, Class c )
    {
        ClassLoadedFromWebAppVerifier.Verification verificaiton = ClassLoadedFromWebAppVerifier.verify( c );
        if ( !verificaiton.passed )
        {
            reportVerificationError( buildLocationErrorMessage( subject, verificaiton ) );
        }
        else
        {
            LOG.debug( "Location of " + subject + " is verified to be in the web app: " + verificaiton.location );
        }
    }

    private String buildLocationErrorMessage( String subject, ClassLoadedFromWebAppVerifier.Verification verification )
    {
        return subject + " classes does not seem to be loaded from the web app. Tested class was [" + verification.claz.getName() +
                "]. It's location was expected to match [" + ClassLoadedFromWebAppVerifier.REGEXP + "]. It's location was [" +
                verification.location + "]";
    }

    private void reportVerificationError( String error )
    {
        System.err.println( "------------------------------------------------------------------------------------------" );
        System.err.println( " Enonic CMS could not start due to errors found during self testing. See log for details." );
        System.err.println( "------------------------------------------------------------------------------------------" );

        LOG.error( error );
        throw new Error( error );
    }
}
