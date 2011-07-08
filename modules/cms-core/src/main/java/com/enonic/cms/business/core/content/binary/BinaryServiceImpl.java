/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.core.content.binary;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.blob.BlobRecord;

import com.enonic.cms.store.dao.BinaryDataDao;

import com.enonic.cms.business.portal.livetrace.BlobFetchingTrace;
import com.enonic.cms.business.portal.livetrace.BlobFetchingTracer;
import com.enonic.cms.business.portal.livetrace.LivePortalTraceService;

import com.enonic.cms.domain.content.binary.AttachmentNotFoundException;
import com.enonic.cms.domain.content.binary.BinaryDataKey;

public class BinaryServiceImpl
    implements BinaryService
{
    @Autowired
    private BinaryDataDao binaryDataDao;

    @Autowired
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
