/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;


public class CreatedTimestampType
    extends TimestampType
{
    private static final CreatedTimestampType type = new CreatedTimestampType();

    public String getTypeString()
    {
        return "CREATED_TIMESTAMP";
    }

    public static DataType getInstance()
    {
        return type;
    }

}