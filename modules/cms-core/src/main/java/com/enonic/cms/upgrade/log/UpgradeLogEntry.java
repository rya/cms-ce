/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.log;

/**
 * This class implements the upgrade log entry.
 */
public final class UpgradeLogEntry
{
    /**
     * Log level.
     */
    private final UpgradeLogLevel level;

    /**
     * Model number.
     */
    private final int modelNumber;

    /**
     * Message.
     */
    private final String message;

    /**
     * Cause.
     */
    private final Throwable cause;

    /**
     * Construct the entry.
     */
    public UpgradeLogEntry( UpgradeLogLevel level, int modelNumber, String message, Throwable cause )
    {
        this.level = level;
        this.modelNumber = modelNumber;
        this.message = message != null ? escapeCharacters( message ) : "No Message";
        this.cause = cause;
    }

    /**
     * Return the level.
     */
    public UpgradeLogLevel getLevel()
    {
        return this.level;
    }

    /**
     * Return the model number.
     */
    public int getModelNumber()
    {
        return this.modelNumber;
    }

    /**
     * Return the message.
     */
    public String getMessage()
    {
        if ( this.modelNumber >= 0 )
        {
            return "[Model " + this.modelNumber + "] " + this.message;
        }
        else
        {
            return this.message;
        }
    }

    /**
     * Escape entities.
     */
    private String escapeCharacters( String str )
    {
        str = str.replace( "<", "&lt;" );
        str = str.replace( ">", "&gt;" );
        return str;
    }

    /**
     * Return the cause.
     */
    public Throwable getCause()
    {
        return this.cause;
    }
}