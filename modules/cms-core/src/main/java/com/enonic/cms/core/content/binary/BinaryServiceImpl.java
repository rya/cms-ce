/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import javax.inject.Inject;

import com.enonic.cms.framework.blob.BlobRecord;

import com.enonic.cms.portal.livetrace.BlobFetchingTrace;
import com.enonic.cms.portal.livetrace.BlobFetchingTracer;
import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.store.dao.BinaryDataDao;

public class BinaryServiceImpl
    implements BinaryService
{
    @Inject
    private BinaryDataDao binaryDataDao;

    @Inject
    private LivePortalTraceService livePortalTraceService;

    public BlobRecord fetchBinary( BinaryDataKey binaryDataKey )
    {
        final BlobFetchingTrace blobFetchingTrace = BlobFetchingTracer.startTracing( livePortalTraceService );

        try
        {
            final BlobRecord blob = binaryDataDao.getBlob( binaryDataKey );

            if ( blob == null )
            {
                throw AttachmentNotFoundException.notFound( binaryDataKey );
            }

            BlobFetchingTracer.traceBlob( blobFetchingTrace, blob );

            return blob;
        }
        finally
        {
            BlobFetchingTracer.stopTracing( blobFetchingTrace, livePortalTraceService );
        }
    }
}
