/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;


public class CurrentTimestampType
    extends TimestampType
{
    private static final CurrentTimestampType type = new CurrentTimestampType();

    public String getTypeString()
    {
        return "CURRENT_TIMESTAMP";
    }

    public static DataType getInstance()
    {
        return type;
    }

}