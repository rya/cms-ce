/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.preference;

import java.io.Serializable;

public class Preference
    implements Serializable
{

    private static final long serialVersionUID = 9129880822683283644L;

    private PreferenceScope scope;

    private String key;

    private String value;

    public Preference( PreferenceScope scope, String key, String value )
    {
        this.scope = scope;
        this.key = key;
        this.value = value;
    }

    public PreferenceScope getScope()
    {
        return scope;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Preference ) )
        {
            return false;
        }

        Preference that = (Preference) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }
        if ( !scope.equals( that.getScope() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = scope.hashCode();
        result = 87 * result + key.hashCode();
        return result;
    }

    public String toString()
    {
        return key + " = " + value;
    }
}
