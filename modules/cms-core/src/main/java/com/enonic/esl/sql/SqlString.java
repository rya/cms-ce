/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql;

public abstract class SqlString
{

    /**
     * Generate the SQL string.
     *
     * @return String
     */
    public final String generateSQL()
    {
        return generateSQL( false );
    }

    /**
     * Generate the SQL string. If linefeed is turned on a nice output format is used.
     *
     * @param linefeed boolean
     * @return String
     */
    protected abstract String generateSQL( boolean linefeed );

    /**
     * Wrapped call to generateSQL with linefeed on.
     *
     * @return String
     */
    public final String toString()
    {
        return generateSQL( true );
    }
}
