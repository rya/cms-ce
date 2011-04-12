/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.BinarySourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringArraySourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringSourceValue;
import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.ContentKey;

public class ImportValueFormater
{

/* String based types */

    public static String getText( final AbstractSourceValue value )
    {
        return getStringValue( value );
    }

    public static String getTextArea( final AbstractSourceValue value )
    {
        return getStringValue( value );
    }

    public static String getXml( final AbstractSourceValue value )
    {
        return getStringValue( value );
    }

    public static String getHtmlArea( final AbstractSourceValue value )
    {
        return getStringValue( value );
    }

    public static String getUrl( final AbstractSourceValue value )
    {
        return getStringValue( value );
    }

    public static String getSelector( final AbstractSourceValue value )
    {
        return getStringValue( value );
    }

/* Other types */

    public static ContentKey getContentKey( final AbstractSourceValue value )
    {
        final String contentKeyAsString = getStringValue( value );
        if ( StringUtils.isNotEmpty( contentKeyAsString ) )
        {
            return new ContentKey( contentKeyAsString );
        }
        return null;
    }

    public static List<ContentKey> getContentKeys( final AbstractSourceValue value )
    {
        List<ContentKey> contentKeys = new ArrayList<ContentKey>();
        if ( value instanceof StringSourceValue )
        {
            contentKeys.add( new ContentKey( ( (StringSourceValue) value ).getValue() ) );
        }
        else if ( value instanceof StringArraySourceValue )
        {
            for ( final String key : ( (StringArraySourceValue) value ).getValues() )
            {
                contentKeys.add( new ContentKey( key ) );
            }
        }
        else
        {
            throw new ImportException( "Invalid source value type. Expected: " + StringSourceValue.class.getSimpleName() + " or " +
                StringArraySourceValue.class.getSimpleName() + ", Was: " + value.getClass().getSimpleName() );
        }
        return contentKeys;
    }

    public static List<String> getKeywords( final AbstractSourceValue value )
    {
        return getStringArrayValue( value );
    }

    public static List<String> getRelatedContent( final AbstractSourceValue value )
    {
        return getStringArrayValue( value );
    }

    public static Boolean getBoolean( final AbstractSourceValue value )
    {
        final String strValue = getStringValue( value );
        if ( strValue.equalsIgnoreCase( "true" ) )
        {
            return true;
        }
        if ( strValue.equalsIgnoreCase( "false" ) )
        {
            return false;
        }
        return null;
    }

    public static Date getDate( final AbstractSourceValue value, final String format )
        throws ImportException
    {
        String strValue = getStringValue( value );
        if ( StringUtils.isEmpty( strValue ) )
        {
            return null;
        }

        String pattern = "yyyy-MM-dd";
        try
        {
            if ( StringUtils.isNotEmpty( format ) )
            {
                pattern = format;
            }
            return new SimpleDateFormat( pattern, Locale.ENGLISH ).parse( strValue );
        }
        catch ( ParseException ex )
        {
            throw new ImportException( "Failed to parse date from value '" + strValue + "', using pattern: " + pattern, ex );
        }
    }

    public static byte[] getBinary( final AbstractSourceValue value )
    {
        if ( value instanceof BinarySourceValue )
        {
            return ( (BinarySourceValue) value ).getValue();
        }
        throw new ImportException( "Invalid source value type. Expected: " + BinarySourceValue.class.getSimpleName() + ", Was: " +
            value.getClass().getSimpleName() );
    }

    public static String getAdditionalValue( final AbstractSourceValue value, final String defaultValue )
    {
        if ( value.hasAdditionalValue() )
        {
            return value.getAdditionalValue();
        }
        return defaultValue;
    }

    private static String getStringValue( final AbstractSourceValue value )
    {
        if ( value instanceof StringSourceValue )
        {
            return ( (StringSourceValue) value ).getValue();
        }
        throw new ImportException( "Invalid source value type. Expected: " + StringSourceValue.class.getSimpleName() + ", Was: " +
            value.getClass().getSimpleName() );
    }

    private static List<String> getStringArrayValue( final AbstractSourceValue value )
    {
        List<String> list = new ArrayList<String>();
        if ( value instanceof StringSourceValue )
        {
            list.add( ( (StringSourceValue) value ).getValue() );
        }
        else if ( value instanceof StringArraySourceValue )
        {
            list.addAll( ( (StringArraySourceValue) value ).getValues() );
        }
        else
        {
            throw new ImportException( "Invalid source value type. Expected: " + StringSourceValue.class.getSimpleName() + " or " +
                StringArraySourceValue.class.getSimpleName() + ", Was: " + value.getClass().getSimpleName() );
        }
        return list;
    }
}
