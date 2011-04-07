/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.net;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.enonic.esl.containers.MultiValueMap;

public class URL
{

    public static class Parameter
    {
        private String key;

        private List values;

        private Parameter( String key, List values )
        {
            this.key = key;
            this.values = values;
        }

        public String getKey()
        {
            return key;
        }

        public List getValues()
        {
            return values;
        }
    }

    private static class ParameterIterator
        implements Iterator
    {
        private Iterator entryIterator;

        private ParameterIterator( MultiValueMap queryParams )
        {
            entryIterator = queryParams.entrySet().iterator();
        }

        public boolean hasNext()
        {
            return entryIterator.hasNext();
        }

        public Object next()
        {
            Map.Entry entry = (Map.Entry) entryIterator.next();
            String key = entry.getKey().toString();
            List values = (List) entry.getValue();
            return new Parameter( key, values );
        }

        public void remove()
        {
            entryIterator.remove();
        }
    }

    private String baseURL;

    private MultiValueMap queryParams = new MultiValueMap();

    private String label;


    public URL( String url )
    {

        if ( url != null && url.trim().length() > 0 )
        {
            String urlString;
            int labelIndex = url.indexOf( '#' );
            if ( labelIndex >= 0 )
            {
                this.label = url.substring( labelIndex + 1 );
                urlString = url.substring( 0, labelIndex );
            }
            else
            {
                urlString = url;
            }

            StringTokenizer tokenizer = new StringTokenizer( urlString, "?" );
            this.baseURL = tokenizer.nextToken();
            if ( tokenizer.hasMoreTokens() )
            {
                String queryString = tokenizer.nextToken( "" ).substring( 1 );
                tokenizer = new StringTokenizer( queryString, "&" );
                while ( tokenizer.hasMoreTokens() )
                {
                    String token = tokenizer.nextToken();
                    String name;
                    String value = null;
                    int equalIdx = token.indexOf( '=' );
                    if ( equalIdx == -1 )
                    {
                        name = token;
                    }
                    else
                    {
                        name = token.substring( 0, equalIdx );
                        value = token.substring( equalIdx + 1 );
                    }
                    if ( value != null )
                    {
                        queryParams.put( name, value );
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException( "URL cannot be empty" );
        }
    }

    public URL( StringBuffer url )
    {
        this( url.toString() );
    }

    /**
     * Add a parameter. It will be appended to the URL string as a "name=value" pair. Previously added parameters with the same name are
     * preserved, as multiple values pr. name is allowed.
     *
     * @param name  String
     * @param value String
     * @return <code>true</code> if the value was added, <code>false</code> otherwise.
     */
    public boolean addParameter( String name, String value )
    {
        if ( name == null || name.length() == 0 )
        {
            return false;
        }
        if ( value == null || value.length() == 0 )
        {
            return false;
        }

        queryParams.put( URLUtil.encode( name ), URLUtil.encode( value ) );

        return true;
    }

    /**
     * Add parameters from av multi-value map. Iterates over the map's values and append them to the URL string as "name=value" pairs.
     * Previously added parameters with the same names as those added are preserved, as multiple values pr. name is allowed.
     *
     * @param valueMap MultiValueMap
     */
    public void addParameters( MultiValueMap valueMap )
    {
        for ( Object key : valueMap.keySet() )
        {
            for ( Object value : ( valueMap.getValueList( key ) ) )
            {
                queryParams.put( URLUtil.encode( key.toString() ), URLUtil.encode( value.toString() ) );
            }
        }
    }

    public boolean setParameter( String name, int value )
    {
        return setParameter( name, String.valueOf( value ) );
    }

    public boolean setParameter( String name, String value )
    {
        if ( name == null || name.length() == 0 )
        {
            return false;
        }
        if ( value == null || value.length() == 0 )
        {
            queryParams.put( URLUtil.encode( name ), "", true );
        }
        else
        {
            queryParams.put( URLUtil.encode( name ), URLUtil.encode( value ), true );
        }

        return true;
    }

    public List getParameters( String name )
    {
        if ( name == null || name.length() == 0 )
        {
            return null;
        }
        return queryParams.getValueList( name );
    }

    public MultiValueMap getParameterMap()
    {
        return queryParams;
    }

    public String getParameter( String name )
    {
        if ( name == null || name.length() == 0 )
        {
            return null;
        }
        List valueList = queryParams.getValueList( name );
        if ( valueList != null && valueList.size() > 0 )
        {
            return (String) valueList.get( 0 );
        }
        else
        {
            return null;
        }
    }

    public Iterator parameterIterator()
    {
        return new ParameterIterator( queryParams );
    }

    public String toString()
    {
        StringBuffer url = new StringBuffer( baseURL );
        Iterator keyIterator = queryParams.keySet().iterator();
        boolean firstParam = true;
        while ( keyIterator.hasNext() )
        {
            String key = (String) keyIterator.next();
            for ( Object value : queryParams.getValueList( key ) )
            {
                if ( firstParam )
                {
                    url.append( '?' );
                    firstParam = false;
                }
                else
                {
                    url.append( '&' );
                }
                url.append( key );
                url.append( '=' );
                url.append( value );
            }
        }
        if ( label != null )
        {
            if ( firstParam )
            {
                // First param has still not been added, so there are no parameters and the & is not needed.
                url.append( "#" );
            }
            else
            {
                // HACK: adds an extra '&' to the URL to bypass the bug concerning
                // request.sendRedirect()
                // and Internet Explorer
                url.append( "&#" );
            }
            url.append( label );
        }

        return url.toString();
    }

    public void setLabel( String string )
    {
        label = string;
    }
}