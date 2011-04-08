/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import org.joda.time.DateTime;

/**
 * Oct 11, 2010
 */
public class BlobFetchingTrace
{
    private AttachmentRequestTrace attachmentRequestTrace;

    private Duration duration = new Duration();

    private long sizeInBytes;

    private boolean usedCachedResult = false;

    BlobFetchingTrace( AttachmentRequestTrace attachmentRequestTrace )
    {
        this.attachmentRequestTrace = attachmentRequestTrace;
    }

    void setStartTime( DateTime start )
    {
        duration.setStartTime( start );
    }

    void setStopTime( DateTime stop )
    {
        duration.setStopTime( stop );
    }

    public Duration getDuration()
    {
        return duration;
    }

    public long getSizeInBytes()
    {
        return sizeInBytes;
    }

    public void setSizeInBytes( long sizeInBytes )
    {
        this.sizeInBytes = sizeInBytes;
    }

}
