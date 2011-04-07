/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preferences;

import com.enonic.cms.domain.security.user.UserEntity;


public class PreferenceAccessResolver
{


    public boolean hasReadAccess( UserEntity user )
    {

        // anon is not allowed to have preferences
        if ( user.isAnonymous() )
        {
            return false;
        }

        return true;
    }

    public boolean hasWriteAccess( UserEntity user )
    {

        // anon is not allowed to have preferences
        if ( user.isAnonymous() )
        {
            return false;
        }

        return true;
    }
}
