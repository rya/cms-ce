/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;


public class PreferenceSpecification
{

    private UserKey userKey;

    private List<PreferenceScope> preferenceScopes = new ArrayList<PreferenceScope>();

    private String wildCardBaseKey = "*";

    public PreferenceSpecification( UserEntity user )
    {
        this.userKey = user.getKey();
    }

    public void setPreferenceScopes( Collection<PreferenceScope> value )
    {
        if ( value == null )
        {
            return;
        }
        this.preferenceScopes.addAll( value );
    }

    public void addPreferenceScope( PreferenceScope scope )
    {
        if ( scope == null )
        {
            return;
        }

        this.preferenceScopes.add( scope );
    }

    public void setWildCardBaseKey( String wildCardBaseKey )
    {
        if ( wildCardBaseKey != null )
        {
            this.wildCardBaseKey = wildCardBaseKey;
        }
    }

    public List<String> getPrefixes()
    {

        List<String> keys = new ArrayList<String>();

        if ( preferenceScopes.size() == 0 )
        {
            keys.add( PreferenceKey.getRawKeyWithWildCardScope( userKey, wildCardBaseKey ) );
        }
        else
        {
            for ( PreferenceScope scope : preferenceScopes )
            {
                PreferenceKey preferenceKey = new PreferenceKey( userKey, scope.getType(), scope.getKey(), wildCardBaseKey );
                keys.add( preferenceKey.toString() );
            }
        }

        return keys;
    }

}
