/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p/> Use this class to build URLs. The functionality is <strong>very</strong> basic. Initialize it with an URL, and add new parameters
 * using the <code>addParameter</code> and <code>addParameters</code> method. Use the <code>setParameter</code> method to set/reset
 * parameters. The <code>setLabel</code> method is used to set the optional label identifier at the end of the url. </p>
 */
public class URL
{

    private String baseURL;

    private MultiValueMap queryParams = new MultiValueMap();

    private String label;

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

    private class MultiValueMap
        extends HashMap<Object, Object>
    {

        private HashMap attributeMap = new HashMap<Object, String>();

        private boolean allowNullValues = false;

        public MultiValueMap( int initialCapacity, float loadFactor )
        {
            super( initialCapacity, loadFactor );
        }

        public MultiValueMap( int initialCapacity )
        {
            super( initialCapacity );
        }

        public MultiValueMap()
        {
            super();
        }

        public MultiValueMap( boolean allowNullValues )
        {
            super();
            this.allowNullValues = allowNullValues;
        }

        public MultiValueMap( Map m )
        {
            Iterator iterator = m.entrySet().iterator();
            while ( iterator.hasNext() )
            {
                Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();
                put( entry.getKey(), entry.getValue() );
            }
        }

        public Object put( Object key, Object value )
        {
            return put( key, value, null, false );
        }

        public Object put( Object key, Object value, String attribute )
        {
            return put( key, value, attribute, false );
        }

        public Object put( Object key, int value )
        {
            return put( key, value, null, false );
        }

        public Object put( Object key, int value, String attribute )
        {
            return put( key, value, attribute, false );
        }

        public Object put( Object key, int[] values )
        {
            return put( key, values, null );
        }

        public Object put( Object key, int[] values, String attribute )
        {
            if ( values == null )
            {
                return put( key, null, attribute, false );
            }
            Object obj = null;
            for ( int i = 0; i < values.length; i++ )
            {
                if ( i == 0 )
                {
                    obj = put( key, values[0], false );
                }
                else
                {
                    put( key, values[i], attribute, false );
                }
            }
            return obj;
        }

        public Object put( Object key, boolean value )
        {
            return put( key, value, null, false );
        }

        public Object put( Object key, boolean value, String attribute )
        {
            return put( key, value, attribute, false );
        }

        public Object put( Object key, Object value, boolean removeOld )
        {
            return put( key, value, null, removeOld );
        }

        public Object put( Object key, Object value, String attribute, boolean removeOld )
        {
            Object obj;
            if ( !removeOld && containsKey( key ) && ( value != null || allowNullValues ) )
            {
                List<Object> values = (List<Object>) get( key );
                if ( value instanceof ArrayList )
                {
                    values.addAll( (ArrayList) value );
                }
                else
                {
                    values.add( value );
                }
                obj = values;
            }
            else if ( value != null && value instanceof ArrayList )
            {
                obj = super.put( key, value );
            }
            else
            {
                List<Object> values = new ArrayList<Object>();
                if ( value != null || allowNullValues )
                {
                    values.add( value );
                }
                obj = super.put( key, values );
            }

            if ( attribute != null )
            {
                attributeMap.put( key, attribute );
            }

            return obj;
        }

        public List getValueList( Object key )
        {
            return (List) get( key );
        }

        public String getAttribute( Object key )
        {
            return (String) attributeMap.get( key );
        }

        public boolean containsValue( Object value )
        {
            Iterator iterator = super.values().iterator();
            while ( iterator.hasNext() )
            {
                List values = (List) iterator.next();
                if ( values.contains( value ) )
                {
                    return true;
                }
            }
            return false;
        }

    }

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

        queryParams.put( UrlPathEncoder.encode( name ), UrlPathEncoder.encode( value ) );

        return true;
    }

    public void addParameters( Map valueMap )
    {
        for ( Object key : valueMap.keySet() )
        {
            Object value = valueMap.get( key );
            if ( value instanceof String[] )
            {
                String[] values = (String[]) value;
                for ( String v : values )
                {
                    queryParams.put( UrlPathEncoder.encode( key.toString() ), UrlPathEncoder.encode( v ) );
                }
            }
            else
            {
                queryParams.put( UrlPathEncoder.encode( key.toString() ), UrlPathEncoder.encode( value.toString() ) );
            }
        }
    }


    /**
     * Add parameters from av multi-value map. Iterates over the map's values and append them to the URL string as "name=value" pairs.
     * Previously added parameters with the same names as those added are preserved, as multiple values pr. name is allowed.
     *
     * @param valueMap Multimap
     */
    public void addParameters( Multimap valueMap )
    {
        for ( Object key : valueMap.keySet() )
        {
            for ( Object value : ( valueMap.get( key ) ) )
            {
                queryParams.put( UrlPathEncoder.encode( key.toString() ), UrlPathEncoder.encode( value.toString() ) );
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
            queryParams.put( UrlPathEncoder.encode( name ), "", true );
        }
        else
        {
            queryParams.put( UrlPathEncoder.encode( name ), UrlPathEncoder.encode( value ), true );
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

    public void removeParameter( String name )
    {
        if ( name == null || name.length() == 0 )
        {
            return;
        }
        queryParams.remove( name );
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

    public boolean hasParameter( String name )
    {
        return queryParams.containsKey( name );
    }
}
