/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot.support;

import java.security.ProtectionDomain;

/**
 * Nov 23, 2010
 */
public class ClassLoadedFromWebAppVerifier
{
    public static final String REGEXP = ".+/WEB-INF/lib/.+";

    public static Verification verify( Class<?> type )
    {
        Verification verification = new Verification();
        verification.claz = type;
        verification.location = resolveLocation( type );

        verify( verification );

        return verification;
    }

    protected static void verify( Verification verification )
    {
        if ( verification.location == null )
        {
            verification.passed = false;
        }
        else
        {
            verification.passed = verification.location.matches( REGEXP );
        }
    }

    protected static String resolveLocation( Class<?> type )
    {
        final ProtectionDomain domain = type.getProtectionDomain();
        if ( domain.getCodeSource() == null )
        {
            return null;
        }
        return domain.getCodeSource().getLocation().toExternalForm();
    }

    public static class Verification
    {
        public Class claz;

        public String location;

        public boolean passed;
    }
}
