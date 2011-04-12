/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.InvalidKeyException;
import com.enonic.cms.core.security.user.UserKey;


public class PreferenceKey
    implements Serializable
{

    private String rawKey = null;

    private PreferenceType type;

    private String typeKey;

    private String scopePart;

    private PreferenceScopeType scopeType;

    private PreferenceScopeKey scopeKey = null;

    private String baseKey;


    public PreferenceKey( UserKey userKey, PreferenceScope scope, String baseKey )
    {
        this( PreferenceType.USER, userKey.toString(), scope.getType(), scope.getKey(), baseKey );
    }

    public PreferenceKey( UserKey userKey, PreferenceScopeType scopeType, PreferenceScopeKey scopeKey, String baseKey )
    {
        this( PreferenceType.USER, userKey.toString(), scopeType, scopeKey, baseKey );
    }

    private PreferenceKey( PreferenceType type, String typeKey, PreferenceScopeType scopeType, PreferenceScopeKey scopeKey, String baseKey )
    {

        if ( type == null )
        {
            throw new IllegalArgumentException( "type cannot be null" );
        }
        if ( typeKey == null )
        {
            throw new IllegalArgumentException( "typeKey cannot be null" );
        }
        if ( scopeType == null )
        {
            throw new IllegalArgumentException( "scope cannot be null" );
        }
        if ( scopeType != PreferenceScopeType.GLOBAL && scopeKey == null )
        {
            throw new IllegalArgumentException( "scopeKey cannot be null" );
        }
        if ( baseKey == null )
        {
            throw new IllegalArgumentException( "baseKey cannot be null" );
        }

        this.type = type;
        this.typeKey = typeKey;
        this.scopeType = scopeType;
        this.scopeKey = scopeKey;
        this.baseKey = baseKey;

        if ( PreferenceScopeType.GLOBAL == scopeType )
        {
            this.scopePart = scopeType.getName();
        }
        else
        {
            this.scopePart = scopeType.getName() + ":" + scopeKey;
        }

        this.rawKey = type.getName() + ":" + typeKey + "." + this.scopePart + "." + baseKey;
    }

    public PreferenceKey( String key )
    {

        if ( key == null || key.trim().length() == 0 )
        {
            throw new InvalidKeyException( key, this.getClass(), "key cannot be null or empty" );
        }

        this.rawKey = key;
        init( key );
    }

    public String getRawKey()
    {
        return rawKey;
    }

    public static String getRawKeyWithWildCardScope( UserKey userKey, String wildCardBaseKey )
    {
        return PreferenceType.USER.getName() + ":" + userKey.toString() + ".*." + wildCardBaseKey;
    }

    private void init( String rawKey )
    {

        int start = 0;
        int end = rawKey.indexOf( "." );
        int partCounter = 1;
        while ( partCounter <= 3 )
        {

            if ( start == -1 || ( end == -1 && partCounter < 3 ) )
            {
                throw new InvalidKeyException( this.rawKey, this.getClass() );
            }

            if ( partCounter == 1 )
            {
                String part = rawKey.substring( start, end );
                initType( part );

                start = end + 1;
                end = rawKey.indexOf( ".", start );
            }
            else if ( partCounter == 2 )
            {
                String part = rawKey.substring( start, end );
                initScope( part );

                start = end + 1;
                end = Math.max( rawKey.indexOf( ".", start ), rawKey.length() );
            }
            else if ( partCounter == 3 )
            {
                String part = rawKey.substring( start, end );
                initBaseKey( part );
                break;
            }

            partCounter++;
        }
    }


    private void initType( String typePart )
    {

        StringTokenizer st = new StringTokenizer( typePart, ":" );

        if ( st.hasMoreTokens() )
        {
            this.type = PreferenceType.parse( st.nextToken() );
        }
        if ( st.hasMoreTokens() )
        {
            this.typeKey = st.nextToken();
        }

        if ( this.type == null )
        {
            throw new InvalidKeyException( rawKey, this.getClass(), "type missing" );
        }

        if ( this.typeKey == null )
        {
            throw new InvalidKeyException( rawKey, this.getClass(), "type key missing" );
        }
    }

    private void initScope( String scopePart )
    {

        this.scopePart = scopePart;

        String scopeName;
        String scopeKeyStr = null;
        if ( StringUtils.contains( scopePart, ':' ) )
        {
            scopeName = StringUtils.substringBefore( scopePart, ":" );
            scopeKeyStr = StringUtils.substringAfter( scopePart, ":" );
        }
        else
        {
            scopeName = scopePart;
        }

        this.scopeType = PreferenceScopeType.parse( scopeName );
        if ( this.scopeType == null )
        {
            throw new InvalidKeyException( rawKey, this.getClass(), "invalid scope" );
        }

        if ( scopeType != PreferenceScopeType.GLOBAL )
        {
            this.scopeKey = new PreferenceScopeKey( scopeKeyStr );
        }
    }

    private void initBaseKey( String part )
    {

        if ( part == null || part.trim().length() == 0 )
        {
            throw new InvalidKeyException( rawKey, this.getClass(), "base key empty" );
        }

        this.baseKey = part;
    }

    public String getKeyExcludingTypePart()
    {
        return scopePart + "." + baseKey;
    }

    public String getType()
    {
        return type.toString();
    }

    public String getTypeKey()
    {
        return typeKey;
    }

    public PreferenceScopeType getScopeType()
    {
        return scopeType;
    }

    public PreferenceScopeKey getScopeKey()
    {
        return scopeKey;
    }

    public String getBaseKey()
    {
        return baseKey;
    }

    public UserKey getUserKey()
    {
        if ( this.type == PreferenceType.USER )
        {
            return new UserKey( typeKey );
        }
        return null;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PreferenceKey ) )
        {
            return false;
        }

        PreferenceKey that = (PreferenceKey) o;

        return rawKey.equals( that.rawKey );

    }

    public int hashCode()
    {
        return new HashCodeBuilder( 235, 549 ).append( rawKey ).toHashCode();
    }

    public String toString()
    {
        return rawKey;
    }


    private enum PreferenceType
    {

        USER( "USER" );

        private String name;

        PreferenceType( String name )
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public static PreferenceType parse( String typeName )
        {

            for ( PreferenceType x : values() )
            {
                if ( x.getName().equalsIgnoreCase( typeName ) )
                {
                    return x;
                }
            }
            return null;
        }
    }

}
