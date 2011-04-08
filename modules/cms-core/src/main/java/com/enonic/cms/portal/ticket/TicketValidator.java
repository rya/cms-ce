/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.ticket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class TicketValidator
{
    public static boolean isValid( HttpServletRequest request )
    {
        String ticket = TicketResolver.resolve( request );
        if ( ticket == null )
        {
            // no ticket is a invalid ticket
            return false;
        }

        // Remember: getSession() creates a new session if not already present
        final HttpSession session = request.getSession();

        return ticket.equals( session.getId() );
    }
}
