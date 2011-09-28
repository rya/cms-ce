package com.enonic.cms.business.portal.image;


import org.joda.time.DateTime;

import com.enonic.cms.api.util.Preconditions;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.business.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.business.preview.PreviewContext;
import com.enonic.cms.business.preview.PreviewService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.security.user.UserEntity;

public class ImageRequestAccessResolver
{
    public enum Access
    {
        OK,
        CONTENT_NOT_FOUND,
        CONTENT_VERSION_NOT_FOUND,
        CONTENT_BINARYDATA_NOT_FOUND,
        NO_ACCESS_TO_CONTENT,
        CONTENT_NOT_ONLINE,
        VERSION_IS_NOT_MAIN_VERSION
    }

    private ContentAccessResolver contentAccessResolver;

    private ContentDao contentDao;

    private UserEntity imageRequester;

    private boolean requireOnlineNow = false;

    private DateTime now;

    private PreviewService previewService;

    private boolean requireMainVersion = false;

    public ImageRequestAccessResolver( ContentDao contentDao, ContentAccessResolver contentAccessResolver )
    {
        Preconditions.checkNotNull( contentDao );
        Preconditions.checkNotNull( contentAccessResolver );
        this.contentDao = contentDao;
        this.contentAccessResolver = contentAccessResolver;
    }
                                  
    public ImageRequestAccessResolver imageRequester( UserEntity requester )
    {
        this.imageRequester = requester;
        return this;
    }

    public ImageRequestAccessResolver requireOnlineNow( DateTime now, PreviewService previewService )
    {
        this.previewService = previewService;
        this.requireOnlineNow = true;
        this.now = now;
        return this;
    }

    public ImageRequestAccessResolver requireMainVersion()
    {
        this.requireMainVersion = true;
        return this;
    }

    public Access isAccessible( final ImageRequest imageRequest )
    {
        Preconditions.checkNotNull( imageRequester );

        if ( imageRequest.getContentKey() != null )
        {
            return isContentImageAccessible( imageRequest );
        }
        else
        {
            // Give always access to user image
            return Access.OK;
        }
    }

    private Access isContentImageAccessible( final ImageRequest imageRequest )
    {
        ContentEntity content = contentDao.findByKey( imageRequest.getContentKey() );
        if ( content == null )
        {
            return Access.CONTENT_NOT_FOUND;
        }
        else if ( content.isDeleted() )
        {
            return Access.CONTENT_NOT_FOUND;
        }

        Access accessToContent = userRightsToContent( content );
        if ( accessToContent != Access.OK )
        {
            return accessToContent;
        }

        final BinaryDataForImageRequestResolver binaryDataForImageRequestResolver = new BinaryDataForImageRequestResolver( contentDao );
        final ContentVersionEntity contentVersion = binaryDataForImageRequestResolver.resolveVersion( content, imageRequest );
        if ( contentVersion == null )
        {
            return Access.CONTENT_VERSION_NOT_FOUND;
        }
        final ContentBinaryDataEntity contentBinaryData =
            binaryDataForImageRequestResolver.resolveContentBinaryData( contentVersion, imageRequest );
        if ( contentBinaryData == null )
        {
            return Access.CONTENT_BINARYDATA_NOT_FOUND;
        }

        if ( requireOnlineNow )
        {
            Access onlineCheck = checkOnline( content );
            if ( onlineCheck != Access.OK )
            {
                return onlineCheck;
            }
        }

        if ( requireMainVersion )
        {
            if ( !content.getMainVersion().equals( contentVersion ) )
            {
                return Access.VERSION_IS_NOT_MAIN_VERSION;
            }
        }

        return Access.OK;
    }

    private Access userRightsToContent( final ContentEntity content )
    {
        if ( !contentAccessResolver.hasReadContentAccess( imageRequester, content ) )
        {
            return Access.NO_ACCESS_TO_CONTENT;
        }
        return Access.OK;
    }

    private Access checkOnline( final ContentEntity content )
    {
        if ( previewService.isInPreview() )
        {
            PreviewContext previewContext = previewService.getPreviewContext();
            if ( previewContext.isPreviewingContent() &&
                previewContext.getContentPreviewContext().treatContentAsAvailableEvenIfOffline( content.getKey() ) )
            {
                // when in preview, the content doesn't need to be online
                return Access.OK;
            }
        }

        if ( !content.isOnline( now ) )
        {
            return Access.CONTENT_NOT_ONLINE;
        }

        return Access.OK;
    }
}
