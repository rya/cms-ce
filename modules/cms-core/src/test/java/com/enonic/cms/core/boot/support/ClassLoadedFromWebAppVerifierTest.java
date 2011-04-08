/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot.support;

import org.junit.Test;

import com.enonic.cms.core.boot.support.ClassLoadedFromWebAppVerifier;

import static org.junit.Assert.*;

/**
 * Nov 23, 2010
 */
public class ClassLoadedFromWebAppVerifierTest
{
    @Test
    public void isLoadedFromWebApp()
    {
        ClassLoadedFromWebAppVerifier.Verification verificaiton =
            ClassLoadedFromWebAppVerifier.verify( ClassLoadedFromWebAppVerifier.class );
        assertFalse( verificaiton.passed );
    }

    @Test
    public void verifyLocation_when_starting_tomcat_from_IntelliJ_as_exploded()
    {
        ClassLoadedFromWebAppVerifier.Verification verification = new ClassLoadedFromWebAppVerifier.Verification();
        verification.claz = ClassLoadedFromWebAppVerifier.class;
        verification.location = "file:/cms-webapp/target/cms-webapp-4.5.0-SNAPSHOT/WEB-INF/lib/quartz-1.4.5.jar";
        ClassLoadedFromWebAppVerifier.verify( verification );
        assertTrue( verification.passed );
    }

    @Test
    public void verifyLocation_when_starting_tomcat_from_IntelliJ_as_war()
    {
        ClassLoadedFromWebAppVerifier.Verification verification = new ClassLoadedFromWebAppVerifier.Verification();
        verification.claz = ClassLoadedFromWebAppVerifier.class;
        verification.location = "file:/Users/jvs/Progs/apache-tomcat-6.0.29/webapps/ROOT/WEB-INF/lib/quartz-1.4.5.jar";
        ClassLoadedFromWebAppVerifier.verify( verification );
        assertTrue( verification.passed );
    }

    @Test
    public void verifyLocation_when_starting_jboss()
    {
        ClassLoadedFromWebAppVerifier.Verification verification = new ClassLoadedFromWebAppVerifier.Verification();
        verification.claz = ClassLoadedFromWebAppVerifier.class;
        verification.location = "jar:file:/home/jboss/jboss-eap-5.0.1/jboss-as/server/web/deploy/cms.war/WEB-INF/lib/quartz-1.4.5.jar!/";
        ClassLoadedFromWebAppVerifier.verify( verification );
        assertTrue( verification.passed );
    }

}
