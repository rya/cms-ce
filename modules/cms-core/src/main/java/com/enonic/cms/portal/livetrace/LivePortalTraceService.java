/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.List;

/**
 * Oct 6, 2010
 */
public interface LivePortalTraceService
{
    boolean tracingEnabled();

    PortalRequestTrace startPortalRequestTracing( String url );

    PageRenderingTrace startPageRenderTracing( PortalRequestTrace portalRequestTrace );

    WindowRenderingTrace startWindowRenderTracing( PortalRequestTrace portalRequestTrace );

    AttachmentRequestTrace startAttachmentRequestTracing( PortalRequestTrace portalRequestTrace );

    BlobFetchingTrace startBlobFetchTracing( AttachmentRequestTrace attachmentRequestTrace );

    PortalRequestTrace getCurrentPortalRequestTrace();

    AttachmentRequestTrace getCurrentAttachmentRequestTrace();

    BlobFetchingTrace getCurrentBlobFetchingTrace();

    void stopTracing( PortalRequestTrace livePortalRequestTrace );

    void stopTracing( PageRenderingTrace pageRenderTrace );

    void stopTracing( WindowRenderingTrace windowRenderingTrace );

    void stopTracing( AttachmentRequestTrace attachmentRequestTrace );

    void stopTracing( BlobFetchingTrace blobFetchingTrace );

    List<PortalRequestTrace> getCurrentPortalRequestTraces();

    List<PastPortalRequestTrace> getHistoryOfPortalRequests();

    List<PastPortalRequestTrace> getHistorySince( long historyNumber );

    List<PortalRequestTrace> getLongestTimePortalPageRequestTraces();

    List<PortalRequestTrace> getLongestTimePortalAttachmentRequestTraces();
}
