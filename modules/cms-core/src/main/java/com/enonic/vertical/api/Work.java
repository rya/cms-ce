/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.api;

import java.util.Properties;

/**
 * Jobs can implement this interface, or only implement the right method signature in other class regardless of interface declaration.
 */
public interface Work
{
    /**
     * User name property.
     */
    public final static String PROP_USERNAME = "username";

    /**
     * Execute the job.
     */
    public void execute( Properties props )
        throws Exception;
}