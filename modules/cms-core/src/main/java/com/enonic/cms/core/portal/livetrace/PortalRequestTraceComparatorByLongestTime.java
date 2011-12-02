/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.Comparator;

/**
 * Oct 6, 2010
 */
public class PortalRequestTraceComparatorByLongestTime
    implements Comparator<PortalRequestTrace>
{
    public int compare( PortalRequestTrace a, PortalRequestTrace b )
    {
        if ( a.getDuration().getAsMilliseconds() > b.getDuration().getAsMilliseconds() )
        {
            return -1;
        }
        else if ( a.getDuration().getAsMilliseconds() < b.getDuration().getAsMilliseconds() )
        {
            return 1;
        }
        else
        {
            if ( a.getRequestNumber() == b.getRequestNumber() )
            {
                return 0;
            }
            else
            {
                return a.getRequestNumber() < b.getRequestNumber() ? -1 : 1;
            }
        }
    }
}
