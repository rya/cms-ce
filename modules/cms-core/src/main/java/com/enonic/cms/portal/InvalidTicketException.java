/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.domain.ForbiddenErrorType;

public class InvalidTicketException
    extends RuntimeException
    implements ForbiddenErrorType
{
    public InvalidTicketException()
    {
        super( "Invalid ticket" );
    }
}
