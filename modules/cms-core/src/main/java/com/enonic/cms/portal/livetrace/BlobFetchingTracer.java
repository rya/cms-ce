/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import com.enonic.cms.framework.blob.BlobRecord;


/**
 * Nov 25, 2010
 */
public class BlobFetchingTracer
{
    public static BlobFetchingTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        AttachmentRequestTrace attachmentRequestTrace = livePortalTraceService.getCurrentAttachmentRequestTrace();

        if ( attachmentRequestTrace != null )
        {
            return livePortalTraceService.startBlobFetchTracing( attachmentRequestTrace );
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( final BlobFetchingTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceBlob( final BlobFetchingTrace trace, final BlobRecord blobRecord )
    {
        if ( trace != null && blobRecord != null )
        {
            trace.setSizeInBytes( blobRecord.getLength() );
        }
    }

}
