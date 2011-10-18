/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.livetrace;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;

/**
 * Oct 11, 2010
 */
public class AttachmentRequestTrace
    implements Trace
{
    private PortalRequestTrace portalRequestTrace;

    private Duration duration = new Duration();

    private ContentKey contentKey;

    private BinaryDataKey binaryDataKey;

    private Long sizeInBytes;

    AttachmentRequestTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
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

    public PortalRequestTrace getPortalRequestTrace()
    {
        return portalRequestTrace;
    }

    public void setPortalRequestTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public BinaryDataKey getBinaryDataKey()
    {
        return binaryDataKey;
    }

    public void setBinaryDataKey( BinaryDataKey binaryDataKey )
    {
        this.binaryDataKey = binaryDataKey;
    }

    public Long getSizeInBytes()
    {
        return sizeInBytes;
    }

    void setSizeInBytes( Long sizeInBytes )
    {
        this.sizeInBytes = sizeInBytes;
    }
}
