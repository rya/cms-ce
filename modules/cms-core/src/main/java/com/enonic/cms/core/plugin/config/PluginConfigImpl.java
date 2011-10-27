package com.enonic.cms.core.plugin.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.api.plugin.PluginConfig;

final class PluginConfigImpl
    implements PluginConfig
{
    private final Map<String, String> map;

    public PluginConfigImpl( final Map<String, String> map )
    {
        this.map = map;
    }

    public String getString( final String key )
    {
        return getString( key, null );
    }

    public String getString( final String key, final String defValue )
    {
        final String value = get( key );
        if ( value == null )
        {
            return defValue;
        }

        return value;
    }

    public Boolean getBoolean( final String key )
    {
        return getBoolean( key, null );
    }

    public Boolean getBoolean( final String key, final Boolean defValue )
    {
        final String value = get( key );
        if ( value == null )
        {
            return defValue;
        }

        return "true".equals(value) ? Boolean.TRUE : Boolean.FALSE;
    }

    public Integer getInteger( final String key )
    {
        return getInteger( key, null );
    }

    public Integer getInteger( final String key, final Integer defValue )
    {
        final String value = get( key );
        if ( value == null )
        {
            return defValue;
        }

        try
        {
            return Integer.parseInt( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }

    public Long getLong( final String key )
    {
        return getLong( key, null );
    }

    public Long getLong( final String key, final Long defValue )
    {
        final String value = get( key );
        if ( value == null )
        {
            return defValue;
        }

        try
        {
            return Long.parseLong( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }

    public Float getFloat( final String key )
    {
        return getFloat( key, null );
    }

    public Float getFloat( final String key, final Float defValue )
    {
        final String value = get( key );
        if ( value == null )
        {
            return defValue;
        }

        try
        {
            return Float.parseFloat( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }

    public Double getDouble( final String key )
    {
        return getDouble( key, null );
    }

    public Double getDouble( final String key, final Double defValue )
    {
        final String value = get( key );
        if ( value == null )
        {
            return defValue;
        }

        try
        {
            return Double.parseDouble( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }

    public int size()
    {
        return this.map.size();
    }

    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    public boolean containsKey( final Object key )
    {
        return this.map.containsKey( key );
    }

    public boolean containsValue( final Object value )
    {
        return this.map.containsValue( value );
    }

    public String get( Object key )
    {
        return this.map.get( key );
    }

    public String put( final String key, final String value )
    {
        throw new UnsupportedOperationException();
    }

    public String remove( final Object key )
    {
        throw new UnsupportedOperationException();
    }

    public void putAll( final Map<? extends String, ? extends String> map )
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    public Set<String> keySet()
    {
        return this.map.keySet();
    }

    public Collection<String> values()
    {
        return this.map.values();
    }

    public Set<Entry<String, String>> entrySet()
    {
        return this.map.entrySet();
    }
}
