/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;

import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserKey;

public final class ImageRequest
{
    private final static String DEFAULT_FORMAT = "png";

    private DateTime requestDateTime;

    private BinaryDataKey binaryDataKey;

    private ContentKey contentKey;

    private String label;

    private String format;

    private final ImageRequestParams params = new ImageRequestParams();

    private User requester;

    private String blobKey;

    private UserKey userKey;

    private ContentVersionKey contentVersionKey;

    public String getName()
    {
        StringBuffer str = new StringBuffer();
        if ( this.userKey != null )
        {
            str.append( "user-" ).append( this.userKey.toString() );
        }
        else
        {
            str.append( "binary-" ).append( this.contentKey ).append( "-" );
            if ( this.binaryDataKey != null )
            {
                str.append( this.binaryDataKey );
            }
            else
            {
                str.append( this.label );
            }
        }

        return str.toString();
    }

    public BinaryDataKey getBinaryDataKey()
    {
        return this.binaryDataKey;
    }

    public void setBinaryDataKey( BinaryDataKey binaryDataKey )
    {
        this.binaryDataKey = binaryDataKey;
    }

    public ContentKey getContentKey()
    {
        return this.contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public ContentVersionKey getContentVersionKey()
    {
        return this.contentVersionKey;
    }

    public void setContentVersionKey( ContentVersionKey value )
    {
        this.contentVersionKey = value;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public String getFormat()
    {
        String format = this.params.getFormat();
        if ( format == null )
        {
            format = this.format;
        }

        return format != null ? format : DEFAULT_FORMAT;
    }

    public void setFormat( String format )
    {
        this.format = format;
    }

    public User getRequester()
    {
        return requester;
    }

    public void setRequester( User requester )
    {
        this.requester = requester;
    }

    public DateTime getRequestDateTime()
    {
        return requestDateTime;
    }

    public void setRequestDateTime( DateTime requestDateTime )
    {
        this.requestDateTime = requestDateTime;
    }

    public ImageRequestParams getParams()
    {
        return this.params;
    }

    public String getBlobKey()
    {
        return blobKey;
    }

    public void setBlobKey( String blobKey )
    {
        this.blobKey = blobKey;
    }

    public UserKey getUserKey()
    {
        return userKey;
    }

    public void setUserKey( UserKey userKey )
    {
        this.userKey = userKey;
    }

    public String getCacheKey()
    {
        StringBuffer str = new StringBuffer();
        str.append( getBlobKey() ).append( "-" );
        str.append( getParams().getQuality() ).append( "-" );
        str.append( getParams().getFilter() ).append( "-" );
        str.append( getParams().getBackgroundColor() );
        return DigestUtils.shaHex( str.toString() ) + "." + getFormat();
    }
}
