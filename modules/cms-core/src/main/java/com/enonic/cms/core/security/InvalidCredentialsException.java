/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import com.enonic.cms.core.BadRequestErrorType;
import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.security.user.QualifiedUsername;

public class InvalidCredentialsException
    extends RuntimeException
    implements BadRequestErrorType, StacktraceLoggingUnrequired
{

    public InvalidCredentialsException( String uid )
    {
        super( createMessage( uid ) );
    }

    public InvalidCredentialsException( QualifiedUsername qualifiedUsername )
    {
        super( createMessage( qualifiedUsername ) );
    }

    private static String createMessage( final String uid )
    {
        return "Invalid username or password, username: '" + uid + "'";
    }

    private static String createMessage( final QualifiedUsername qualifiedUsername )
    {
        return "Invalid username or password, username: '" + qualifiedUsername.toString() + "'";
    }
}