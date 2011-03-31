/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade;

/**
 * This class defines the upgrade exception.
 */
public final class UpgradeException
    extends Exception
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct the exception.
     */
    public UpgradeException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public UpgradeException( String message, Throwable cause )
    {
        super( message, cause );
    }
}