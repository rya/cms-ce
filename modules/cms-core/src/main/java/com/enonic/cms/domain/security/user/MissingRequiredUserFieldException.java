/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.user;

import com.enonic.cms.domain.user.field.UserFieldType;


public class MissingRequiredUserFieldException
    extends RuntimeException
{
    public MissingRequiredUserFieldException( UserFieldType type )
    {
        super( buildMessage( type ) );

    }

    private static String buildMessage( UserFieldType type )
    {
        return "Missing required user field: " + type.getName();
    }


}
