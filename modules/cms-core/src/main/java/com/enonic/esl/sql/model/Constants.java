/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import com.enonic.esl.sql.model.datatypes.BigIntType;
import com.enonic.esl.sql.model.datatypes.BinaryType;
import com.enonic.esl.sql.model.datatypes.BooleanType;
import com.enonic.esl.sql.model.datatypes.CDATAType;
import com.enonic.esl.sql.model.datatypes.CharType;
import com.enonic.esl.sql.model.datatypes.CreatedTimestampType;
import com.enonic.esl.sql.model.datatypes.CurrentTimestampType;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.sql.model.datatypes.FloatType;
import com.enonic.esl.sql.model.datatypes.IntegerType;
import com.enonic.esl.sql.model.datatypes.ShortXMLType;
import com.enonic.esl.sql.model.datatypes.TimestampType;
import com.enonic.esl.sql.model.datatypes.VarcharType;
import com.enonic.esl.sql.model.datatypes.XMLType;

public class Constants
{
    public final static DataType COLUMN_INTEGER = IntegerType.getInstance();

    public final static DataType COLUMN_BIGINT = BigIntType.getInstance();

    public final static DataType COLUMN_CHAR = CharType.getInstance();

    public final static DataType COLUMN_VARCHAR = VarcharType.getInstance();

    public final static DataType COLUMN_BOOLEAN = BooleanType.getInstance();

    public final static DataType COLUMN_TIMESTAMP = TimestampType.getInstance();

    public final static DataType COLUMN_CREATED_TIMESTAMP = CreatedTimestampType.getInstance();

    public final static DataType COLUMN_CURRENT_TIMESTAMP = CurrentTimestampType.getInstance();

    public final static DataType COLUMN_BINARY = BinaryType.getInstance();

    public final static DataType COLUMN_FLOAT = FloatType.getInstance();

    public final static DataType COLUMN_XML = XMLType.getInstance();

    public final static DataType COLUMN_CDATA = CDATAType.getInstance();

    public final static DataType COLUMN_SHORTXML = ShortXMLType.getInstance();

    private Constants()
    {
    }

    public static DataType getType( String typeStr )
    {
        if ( "integer".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_INTEGER;
        }
        if ( "bigint".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_BIGINT;
        }
        else if ( "char".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_CHAR;
        }
        else if ( "varchar".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_VARCHAR;
        }
        else if ( "boolean".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_BOOLEAN;
        }
        else if ( "timestamp".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_TIMESTAMP;
        }
        else if ( "created_timestamp".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_CREATED_TIMESTAMP;
        }
        else if ( "current_timestamp".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_CURRENT_TIMESTAMP;
        }
        else if ( "binary".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_BINARY;
        }
        else if ( "float".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_FLOAT;
        }
        else if ( "xml".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_XML;
        }
        else if ( "cdata".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_CDATA;
        }
        else if ( "shortxml".equalsIgnoreCase( typeStr ) )
        {
            return Constants.COLUMN_SHORTXML;
        }

        //throw new RuntimeException();
        System.err.println( "Constants.getType(String): Unknown column type: " + typeStr );
        return null;
    }
}