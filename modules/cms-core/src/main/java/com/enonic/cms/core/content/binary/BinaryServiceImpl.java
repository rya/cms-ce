/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.binary.access.BinaryAccessResolver;
import com.enonic.cms.portal.livetrace.BlobFetchingTrace;
import com.enonic.cms.portal.livetrace.BlobFetchingTracer;

import com.enonic.cms.business.preview.PreviewContext;
import com.enonic.cms.business.preview.PreviewService;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.AttachmentNotFoundException;
import com.enonic.cms.domain.content.binary.AttachmentRequest;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;

public class BinaryServiceImpl
    implements BinaryService
{

    @Autowired
    private BinaryDataDao binaryDataDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BinaryAccessResolver binaryAccessResolver;

    @Autowired
    private TimeService timeService;

    @Autowired
    private PreviewService previewService;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    public BinaryDataEntity getBinaryDataForPortal( User user, AttachmentRequest attachmentRequest )
    {
        BinaryDataEntity binaryData = binaryDataDao.findByKey( attachmentRequest.getBinaryDataKey() );
        checkAccessibilityForPortal( attachmentRequest, binaryData, userDao.findByKey( user.getKey() ) );
        return binaryData;
    }

    public BinaryDataEntity getBinaryDataForAdmin( User user, BinaryDataKey binaryDataKey )
    {
        BinaryDataEntity binaryData = binaryDataDao.findByKey( binaryDataKey );
        checkAccessibilityForAdmin( binaryDataKey, binaryData, userDao.findByKey( user.getKey() ) );
        return binaryData;
    }

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

    private void checkAccessibilityForAdmin( BinaryDataKey binaryDataKey, BinaryDataEntity binaryData, UserEntity user )
    {

        if ( binaryData == null )
        {
            throw AttachmentNotFoundException.notFound( binaryDataKey );
        }

        if ( !binaryAccessResolver.hasReadAccess( binaryData, user ) )
        {
            throw AttachmentNotFoundException.notFound( binaryDataKey );
        }
    }

    private void checkAccessibilityForPortal( AttachmentRequest attachmentRequest, BinaryDataEntity binaryData, UserEntity user )
    {
        if ( binaryData == null )
        {
            throw AttachmentNotFoundException.notFound( attachmentRequest.getBinaryDataKey() );
        }

        if ( !binaryAccessResolver.hasReadAccess( binaryData, user ) )
        {
            throw AttachmentNotFoundException.noAccess( attachmentRequest.getBinaryDataKey() );
        }

        if ( previewService.isInPreview() )
        {
            PreviewContext previewContext = previewService.getPreviewContext();
            if ( previewContext.isPreviewingContent() &&
                previewContext.getContentPreviewContext().treatContentAsAvailableEvenIfOffline( attachmentRequest.getContentKey() ) )
            {
                // in preview, content related to the previewed content
                return;
            }
        }

        if ( !binaryAccessResolver.hasReadAndIsAccessibleOnline( binaryData, user, timeService.getNowAsDateTime() ) )
        {
            throw AttachmentNotFoundException.notFound( attachmentRequest.getBinaryDataKey() );
        }
    }

    public BinaryDataKey resolveBinaryDataKey( ContentKey contentKey, String label, ContentVersionKey contentVersionKey )
    {
        BinaryDataEntity binaryData;

        if ( contentVersionKey != null )
        {
            binaryData = binaryDataDao.findByContentVersionKey( contentVersionKey, label );
        }
        else
        {
            binaryData = binaryDataDao.findByContentKey( contentKey, label );
        }

        return binaryData != null ? binaryData.getBinaryDataKey() : null;
    }
}
