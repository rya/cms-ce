/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Feb 25, 2010
 * Time: 11:35:45 AM
 */
public class PreferenceUniqueMatchResolver
{
    List<PreferenceKey> preferenceKeys = new ArrayList<PreferenceKey>();

    public void addPreferenceKeyIfHigherPriority( PreferenceKey newPreferenceKey )
    {
        String preferenceBaseKey = newPreferenceKey.getBaseKey();

        boolean found = false;

        for ( PreferenceKey existingKey : preferenceKeys )
        {
            if ( existingKey.getBaseKey().equals( preferenceBaseKey ) )
            {
                found = true;

                if ( hasHigherPriority( existingKey.getScopeType(), newPreferenceKey.getScopeType() ) )
                {
                    preferenceKeys.remove( existingKey );
                    preferenceKeys.add( newPreferenceKey );
                }

                break;
            }
        }

        if ( !found )
        {
            preferenceKeys.add( newPreferenceKey );
        }

    }

    private boolean hasHigherPriority( PreferenceScopeType existingScope, PreferenceScopeType newScope )
    {
        return ( existingScope.getPriority() <= newScope.getPriority() );
    }

    public List<PreferenceKey> getUniquePreferenceKeys()
    {
        return preferenceKeys;
    }

}
