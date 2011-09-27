/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.framework.io.EncryptedParameterSerializer;
import com.enonic.cms.framework.io.ParameterSerializer;

public final class ImageRequestParams
{
    private final static int DEFAULT_QUALITY = 85;

    private final static int DEFAULT_BACKGROUND = 0x00FFFFFF;

    private final static String FILTER_KEY = "_filter";

    private final static String QUALITY_KEY = "_quality";

    private final static String BACKGROUND_KEY = "_background";

    private final static String FORMAT_KEY = "_format";

    private final static String ENCODED_KEY = "_encoded";

    private final static ParameterSerializer SERIALIER = new EncryptedParameterSerializer();

    private String filter;

    private String format;

    private int quality = DEFAULT_QUALITY;

    private int backgroundColor = DEFAULT_BACKGROUND;

    public String getFilter()
    {
        return this.filter;
    }

    public String getFormat()
    {
        return this.format;
    }

    public int getQuality()
    {
        return this.quality;
    }

    public String getQualityAsString()
    {
        return Integer.toString( this.quality );
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public String getBackgroundColorAsString()
    {
        return "0x" + Integer.toString( this.backgroundColor, 16 );
    }

    public String getEncoded()
    {
        return SERIALIER.serializeList( getParamsAsList() );
    }

    public void setFilter( String value )
    {
        this.filter = value;
    }

    public void setFormat( String value )
    {
        this.format = value;
    }

    public void setQuality( String value )
    {
        try
        {
            setQuality( Integer.parseInt( value ) );
        }
        catch ( Exception e )
        {
            // Do nothing
        }
    }

    public void setQuality( int value )
    {
        if ( ( value >= 0 ) && ( value <= 100 ) )
        {
            this.quality = value;
        }
    }

    public void setBackgroundColor( String value )
    {
        if ( value != null )
        {
            if ( value.startsWith( "0x" ) )
            {
                value = value.substring( 2 );
            }

            try
            {
                setBackgroundColor( Integer.parseInt( value, 16 ) );
            }
            catch ( Exception e )
            {
                // Do nothing
            }
        }
    }

    public void setBackgroundColor( int value )
    {
        this.backgroundColor = value;
    }

    public void setEncoded( String value )
    {
        if ( value != null )
        {
            setParamsAsList( SERIALIER.deserializeList( value ) );
        }
    }

    public void setParams( Map<String, String> map, boolean requireEncoded )
    {
        if ( !requireEncoded )
        {
            setFilter( map.get( FILTER_KEY ) );
            setQuality( map.get( QUALITY_KEY ) );
            setBackgroundColor( map.get( BACKGROUND_KEY ) );
            setFormat( map.get( FORMAT_KEY ) );
        }
        else
        {
            setEncoded( map.get( ENCODED_KEY ) );
        }
    }

    public Map<String, String> getParams( boolean encode )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( encode )
        {
            map.put( ENCODED_KEY, getEncoded() );
        }
        else
        {
            map.put( FILTER_KEY, getFilter() );
            map.put( QUALITY_KEY, getQualityAsString() );
            map.put( BACKGROUND_KEY, getBackgroundColorAsString() );
            map.put( FORMAT_KEY, getFormat() );
        }

        return map;
    }

    private List<String> getParamsAsList()
    {
        ArrayList<String> list = new ArrayList<String>();
        addParamInList( list, getFilter() );
        addParamInList( list, getQualityAsString() );
        addParamInList( list, getBackgroundColorAsString() );
        addParamInList( list, getFormat() );
        return list;
    }

    private void setParamsAsList( List<String> list )
    {
        setFilter( getParamInList( list, 0 ) );
        setQuality( getParamInList( list, 1 ) );
        setBackgroundColor( getParamInList( list, 2 ) );
        setFormat( getParamInList( list, 3 ) );
    }

    private String getParamInList( List<String> list, int pos )
    {
        if ( ( list != null ) && ( pos < list.size() ) )
        {
            String val = list.get( pos );
            val = val.trim();

            if ( "".equals( val ) )
            {
                return null;
            }
            else
            {
                return val;
            }
        }

        return null;
    }

    private void addParamInList( List<String> list, String param )
    {
        if ( param == null )
        {
            list.add( "" );
        }
        else
        {
            list.add( param );
        }
    }
}
