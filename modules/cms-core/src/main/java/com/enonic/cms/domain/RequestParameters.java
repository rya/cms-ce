/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

public class RequestParameters
{
    private SortedMap<String, Param> parameters;

    public RequestParameters()
    {
        this.parameters = new TreeMap<String, Param>();
    }

    public RequestParameters( Map<String, String[]> params )
    {
        this();
        if ( params != null )
        {
            for ( Map.Entry<String, String[]> entry : params.entrySet() )
            {
                this.parameters.put( entry.getKey(), new Param( entry.getKey(), entry.getValue() ) );
            }
        }
    }

    public RequestParameters( RequestParameters requestParameters )
    {
        parameters = new TreeMap<String, Param>();
        for ( Param param : requestParameters.parameters.values() )
        {
            parameters.put( param.getName(), param );
        }
    }

    public Collection<Param> getParameters()
    {
        return parameters.values();
    }

    public Param getParameter( String paramName )
    {
        return parameters.get( paramName );
    }

    public int getParameterCount()
    {
        return this.parameters.size();
    }

    public String[] getParameterNames()
    {
        return this.parameters.keySet().toArray( new String[this.parameters.size()] );
    }

    public boolean hasParameter( String name )
    {
        return this.parameters.containsKey( name );
    }

    public String getParameterValue( String name )
    {
        final Param param = this.parameters.get( name );
        if ( param == null )
        {
            return null;
        }
        return param.getFirstValue();
    }

    public String[] getParameterValues( String name )
    {
        final Param param = this.parameters.get( name );
        if ( param == null )
        {
            return null;
        }
        return param.getValues();
    }

    public String getParameterValuesAsCommaSeparatedString( String parameterName )
    {
        Param param = parameters.get( parameterName );
        if ( param == null )
        {
            return null;
        }
        return param.getParameterValuesAsCommaSeparatedString();
    }

    public void setParameterValue( String name, String value )
    {
        if ( value != null )
        {
            setParameterValues( name, new String[]{value} );
        }
    }

    public void setParameterValues( String name, String[] values )
    {
        if ( ( values != null ) && ( values.length > 0 ) )
        {
            this.parameters.put( name, new Param( name, values ) );
        }
        else
        {
            this.parameters.remove( name );
        }
    }

    public void setParam( Param param )
    {
        this.parameters.put( param.getName(), param );
    }

    public void addParameterValue( String name, String value )
    {
        if ( value != null )
        {
            addParameterValues( name, new String[]{value} );
        }
    }

    public void addParameterValues( String name, String[] values )
    {
        if ( ( values != null ) && ( values.length > 0 ) )
        {
            Param existingParam = this.parameters.get( name );
            if ( existingParam != null )
            {
                this.parameters.put( name, existingParam.addValues( values ) );
            }
            else
            {
                this.parameters.put( name, new Param( name, values ) );
            }
        }
    }

    public void removeParameter( String name )
    {
        this.parameters.remove( name );
    }

    public RequestParameters copy()
    {
        return new RequestParameters( this );
    }

    public boolean hasParameters()
    {

        return parameters.size() > 0;
    }

    /**
     * Return the parameters formatted as in a http request.
     */
    public String getAsString( boolean startWithQuestionMark )
    {
        RequestParametersToStringBuilder builder = new RequestParametersToStringBuilder();
        builder.setStartWithQuestionMark( startWithQuestionMark );
        return builder.toString( this );
    }

    public SortedMap<String, Param> getAsMap()
    {
        return Collections.unmodifiableSortedMap( parameters );
    }

    public SortedMap<String, String[]> getAsMapWithStringValues()
    {
        SortedMap<String, String[]> map = new TreeMap<String, String[]>();

        for ( Param param : parameters.values() )
        {
            map.put( param.getName(), param.getValues() );
        }

        return map;
    }


    public class Param
    {
        private String name;

        private String[] values;

        public Param( String name, String value )
        {
            Assert.notNull( name, "name cannot be null" );
            Assert.notNull( value, "value cannot be null" );

            this.name = name;
            this.values = new String[]{value};
        }

        public Param( String name, String[] values )
        {
            Assert.notNull( name, "name cannot be null" );
            Assert.notNull( values, "values cannot be null" );

            this.name = name;
            this.values = values;
        }

        public String getName()
        {
            return name;
        }

        public String[] getValues()
        {
            return values;
        }

        public Param addValues( String[] values )
        {
            String[] existingValueArray = this.values;
            String[] newValueArray = new String[existingValueArray.length + values.length];
            System.arraycopy( existingValueArray, 0, newValueArray, 0, existingValueArray.length );
            System.arraycopy( values, 0, newValueArray, existingValueArray.length, values.length );
            return new Param( name, newValueArray );
        }

        public String getFirstValue()
        {
            if ( values.length == 0 )
            {
                return null;
            }
            return values[0];
        }

        public boolean isEmpty()
        {
            if ( values.length == 0 )
            {
                return true;
            }

            for ( String v : values )
            {
                if ( !StringUtils.isEmpty( v ) )
                {
                    return false;
                }
            }

            return true;
        }

        public String getParameterValuesAsCommaSeparatedString()
        {
            StringBuffer s = new StringBuffer( "" );
            for ( int i = 0; i < values.length; i++ )
            {
                String v = values[i];
                s.append( v );
                if ( i < values.length - 1 )
                {
                    s.append( "," );
                }
            }
            return s.toString();
        }

        public String toString()
        {
            if ( isEmpty() )
            {
                return "[]";
            }

            StringBuffer s = new StringBuffer();
            s.append( "[" );
            for ( int i = 0; i < values.length; i++ )
            {
                String v = values[i];
                s.append( v );
                if ( i < values.length - 1 )
                {
                    s.append( "," );
                }
            }

            s.append( "]" );
            return s.toString();
        }
    }
}
