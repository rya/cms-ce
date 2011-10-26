package com.enonic.cms.core.portal.image;


import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.store.dao.ContentDao;

public class BinaryDataForImageRequestResolver
{
    private ContentDao contentDao;

    public BinaryDataForImageRequestResolver( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public BinaryDataEntity resolveBinaryData( final ImageRequest imageRequest )
    {
        final ContentEntity content = contentDao.findByKey( imageRequest.getContentKey() );
        if ( content == null || content.isDeleted() )
        {
            return null;
        }
        return resolveBinaryData( content, imageRequest );
    }

    public BinaryDataEntity resolveBinaryData( final ContentEntity content, final ImageRequest imageRequest )
    {
        final ContentVersionEntity version = resolveVersion( content, imageRequest );
        if ( version == null )
        {
            return null;
        }

        final ContentBinaryDataEntity contentBinaryData = resolveContentBinaryData( version, imageRequest );
        if ( contentBinaryData == null )
        {
            return null;
        }

        return contentBinaryData.getBinaryData();
    }

    public ContentVersionEntity resolveVersion( final ContentEntity content, final ImageRequest imageRequest )
    {
        if ( imageRequest.getContentVersionKey() != null )
        {
            return content.getVersion( imageRequest.getContentVersionKey() );
        }
        else
        {
            return content.getMainVersion();
        }
    }

    public ContentBinaryDataEntity resolveContentBinaryData( final ContentVersionEntity version, final ImageRequest imageRequest )
    {
        if ( imageRequest.getBinaryDataKey() != null )
        {
            return version.getContentBinaryData( imageRequest.getBinaryDataKey() );
        }
        else
        {
            return version.getContentBinaryData( imageRequest.getLabel() );
        }
    }

}
