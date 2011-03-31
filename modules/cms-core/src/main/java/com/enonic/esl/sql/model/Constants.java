/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.sql.Types;

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

    public static final int OPERATION_INSERT = 0;

    public static final int OPERATION_UPDATE = 1;

    public static final int OPERATION_SELECT = 2;

    public static final int OPERATION_REMOVE = 3;

    // Prevent instantiation
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

    public static DataType guessType( String typeStr, int sqlType )
    {
        typeStr = typeStr.toLowerCase();

        String temp = typeStr.substring( typeStr.lastIndexOf( "_" ) + 1 );

        if ( temp.equals( "sschema" ) )
        {
            return Constants.COLUMN_SHORTXML;
        }
        else if ( temp.equals( "ssyncvalue" ) )
        {
            return Constants.COLUMN_VARCHAR;
        }
        else if ( typeStr.equals( "chl_dtetimestamp" ) || typeStr.equals( "chb_dtetimestamp" ) )
        {
            return Constants.COLUMN_TIMESTAMP;
        }

        if ( temp.startsWith( "l" ) )
        {
            return Constants.COLUMN_INTEGER;
        }
        else if ( temp.startsWith( "sxml" ) )
        {
            return Constants.COLUMN_XML;
        }
        else if ( temp.startsWith( "s" ) )
        {
            if ( sqlType == Types.CHAR )
            {
                return Constants.COLUMN_CHAR;
            }
            else
            {
                return Constants.COLUMN_VARCHAR;
            }
        }
        else if ( temp.startsWith( "dtetimestamp" ) )
        {
            return Constants.COLUMN_CURRENT_TIMESTAMP;
        }
        else if ( temp.startsWith( "dtecreated" ) )
        {
            return Constants.COLUMN_CREATED_TIMESTAMP;
        }
        else if ( temp.startsWith( "dte" ) )
        {
            return Constants.COLUMN_TIMESTAMP;
        }
        else if ( temp.startsWith( "blob" ) )
        {
            return Constants.COLUMN_BINARY;
        }
        else if ( temp.startsWith( "d" ) )
        {
            return Constants.COLUMN_FLOAT;
        }
        else if ( temp.startsWith( "xml" ) )
        {
            if ( sqlType == Types.VARCHAR )
            {
                return Constants.COLUMN_SHORTXML;
            }
            else
            {
                return Constants.COLUMN_XML;
            }
        }
        else if ( temp.startsWith( "cdata" ) )
        {
            return Constants.COLUMN_CDATA;
        }
        else if ( temp.startsWith( "b" ) )
        {
            return Constants.COLUMN_BOOLEAN;
        }
        else if ( temp.startsWith( "h" ) )
        {
            return Constants.COLUMN_CHAR;
        }

        // Dirty hack... These column names does not follow our convention
        else if ( temp.startsWith( "mbdata" ) )
        {
            return Constants.COLUMN_XML;
        }
        else if ( temp.startsWith( "firstpage" ) )
        {
            return Constants.COLUMN_INTEGER;
        }
        else if ( temp.startsWith( "errorpage" ) )
        {
            return Constants.COLUMN_INTEGER;
        }
        else if ( temp.startsWith( "xsldata" ) )
        {
            return Constants.COLUMN_XML;
        }

        //throw new RuntimeException();
        System.err.println( "guessType(String): Unknown column type: " + typeStr );
        return null;
    }
}