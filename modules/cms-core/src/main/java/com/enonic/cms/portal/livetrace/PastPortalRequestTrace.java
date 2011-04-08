/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

/**
 * Oct 7, 2010
 */
public class PastPortalRequestTrace
{
    private long historyRecordNumber;

    private PortalRequestTrace portalRequestTrace;

    public PastPortalRequestTrace( long historyRecordNumber, PortalRequestTrace portalRequestTrace )
    {
        this.historyRecordNumber = historyRecordNumber;
        this.portalRequestTrace = portalRequestTrace;
    }

    public long getHistoryRecordNumber()
    {
        return historyRecordNumber;
    }

    public PortalRequestTrace getPortalRequestTrace()
    {
        return portalRequestTrace;
    }
}
