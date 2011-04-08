/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.Comparator;

/**
 * Oct 6, 2010
 */
public class PortalRequestTraceComparatorByLongestTime
    implements Comparator<PortalRequestTrace>
{
    public int compare( PortalRequestTrace a, PortalRequestTrace b )
    {
        if ( a.getDuration().getExecutionTimeInMilliseconds() > b.getDuration().getExecutionTimeInMilliseconds() )
        {
            return -1;
        }
        else if ( a.getDuration().getExecutionTimeInMilliseconds() < b.getDuration().getExecutionTimeInMilliseconds() )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
