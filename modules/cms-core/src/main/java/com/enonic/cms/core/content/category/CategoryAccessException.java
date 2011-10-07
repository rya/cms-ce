/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import com.enonic.cms.core.security.user.QualifiedUsername;


public class CategoryAccessException
    extends RuntimeException
{

    public CategoryAccessException( String message, QualifiedUsername user, CategoryAccessType categoryAccessType, CategoryKey categoryKey )
    {
        super( buildMessage( message, user, categoryAccessType, categoryKey ) );
    }

    private static String buildMessage( String message, QualifiedUsername user, CategoryAccessType categoryAccessType,
                                        CategoryKey categoryKey )
    {
        StringBuffer msg = new StringBuffer();
        msg.append( message );
        msg.append( " User " ).append( user ).append( " do not have " ).append( categoryAccessType );
        msg.append( " access on category " ).append( categoryKey );
        return msg.toString();
    }
}
