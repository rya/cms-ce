/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import org.joda.time.DateTime;

import com.enonic.cms.core.PathAndParamsToStringBuilder;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.structure.SiteEntity;

/**
 * Oct 6, 2010
 */
public class PortalRequestTrace
    implements Trace
{
    private long requestNumber;

    private long completedNumber;

    private RequestMode mode;

    private MaxLengthedString url;

    private Duration duration = new Duration();

    private MaxLengthedString httpRequestRemoteAddress;

    private MaxLengthedString httpRequestCharacterEncoding;

    private MaxLengthedString httpRequestContentType;

    private MaxLengthedString httpRequestUserAgent;

    private MaxLengthedString requester;

    private SiteKey siteKey;

    private String siteName;

    private MaxLengthedString siteLocalPathAndParams;

    private MaxLengthedString responseRedirect;

    private MaxLengthedString responseForward;

    private PageRenderingTrace pageRenderingTrace;

    private WindowRenderingTrace windowRenderingTrace;

    private AttachmentRequestTrace attachmentRequestTrace;

    private ImageRequestTrace imageRequestTrace;

    PortalRequestTrace( long requestNumber, String url )
    {
        this.requestNumber = requestNumber;
        this.url = new MaxLengthedString( url );
    }

    public long getRequestNumber()
    {
        return requestNumber;
    }

    public long getCompletedNumber()
    {
        return completedNumber;
    }

    void setCompletedNumber( long completedNumber )
    {
        this.completedNumber = completedNumber;
    }

    public String getUrl()
    {
        return url != null ? url.toString() : null;
    }

    public RequestMode getMode()
    {
        return mode;
    }

    void setMode( RequestMode mode )
    {
        this.mode = mode;
    }

    public String getType()
    {
        if ( pageRenderingTrace != null )
        {
            return "P";
        }
        else if ( windowRenderingTrace != null )
        {
            return "W";
        }
        else if ( attachmentRequestTrace != null )
        {
            return "A";
        }
        else if ( imageRequestTrace != null )
        {
            return "I";
        }
        else
        {
            return "?";
        }
    }

    public String getTypeDescription()
    {
        if ( pageRenderingTrace != null )
        {
            return "Page";
        }
        else if ( windowRenderingTrace != null )
        {
            return "Window";
        }
        else if ( attachmentRequestTrace != null )
        {
            return "Attachment";
        }
        else if ( imageRequestTrace != null )
        {
            return "Image";
        }
        else
        {
            return "Unknown";
        }
    }

    void setStartTime( DateTime time )
    {
        this.duration.setStartTime( time );
    }

    void setStopTime( DateTime stopTime )
    {
        this.duration.setStopTime( stopTime );
    }

    public Duration getDuration()
    {
        return duration;
    }

    public String getHttpRequestRemoteAddress()
    {
        return httpRequestRemoteAddress != null ? httpRequestRemoteAddress.toString() : null;
    }

    void setHttpRequestRemoteAddress( String httpRequestRemoteAddress )
    {
        this.httpRequestRemoteAddress = new MaxLengthedString( httpRequestRemoteAddress );
    }

    public String getHttpRequestCharacterEncoding()
    {
        return httpRequestCharacterEncoding != null ? httpRequestCharacterEncoding.toString() : null;
    }

    void setHttpRequestCharacterEncoding( String httpRequestCharacterEncoding )
    {
        this.httpRequestCharacterEncoding = new MaxLengthedString( httpRequestCharacterEncoding );
    }

    public String getHttpRequestContentType()
    {
        return httpRequestContentType != null ? httpRequestContentType.toString() : null;
    }

    void setHttpRequestContentType( String httpRequestContentType )
    {
        this.httpRequestContentType = new MaxLengthedString( httpRequestContentType );
    }

    public String getHttpRequestUserAgent()
    {
        return httpRequestUserAgent != null ? httpRequestUserAgent.toString() : null;
    }

    void setHttpRequestUserAgent( String httpRequestUserAgent )
    {
        this.httpRequestUserAgent = new MaxLengthedString( httpRequestUserAgent );
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getSiteName()
    {
        return siteName;
    }

    void setSite( SiteEntity site )
    {
        this.siteKey = site.getKey();
        this.siteName = site.getName();
    }

    void setSitePath( SitePath sitePath )
    {
        PathAndParamsToStringBuilder stringBuilder = new PathAndParamsToStringBuilder();
        this.siteLocalPathAndParams = new MaxLengthedString( stringBuilder.toString( sitePath.getPathAndParams() ) );
    }

    public String getSiteLocalUrl()
    {
        return siteLocalPathAndParams != null ? siteLocalPathAndParams.toString() : null;
    }

    public String getRequester()
    {
        return requester != null ? requester.toString() : null;
    }

    void setRequester( QualifiedUsername requester )
    {
        this.requester = new MaxLengthedString( requester.toString() );
    }

    public String getResponseRedirect()
    {
        return responseRedirect != null ? responseRedirect.toString() : null;
    }

    void setResponseRedirect( String responseRedirect )
    {
        this.responseRedirect = new MaxLengthedString( responseRedirect );
    }

    public String getResponseForward()
    {
        return responseForward != null ? responseForward.toString() : null;
    }

    void setResponseForward( String responseForward )
    {
        this.responseForward = new MaxLengthedString( responseForward );
    }

    public boolean hasPageRenderingTrace()
    {
        return pageRenderingTrace != null;
    }

    public PageRenderingTrace getPageRenderingTrace()
    {
        return pageRenderingTrace;
    }

    void setPageRenderingTrace( PageRenderingTrace pageRenderingTrace )
    {
        this.pageRenderingTrace = pageRenderingTrace;
    }

    void setWindowRenderingTrace( WindowRenderingTrace windowRenderingTrace )
    {
        this.windowRenderingTrace = windowRenderingTrace;
    }

    public boolean hasWindowRenderingTrace()
    {
        return windowRenderingTrace != null;
    }

    public WindowRenderingTrace getWindowRenderingTrace()
    {
        return windowRenderingTrace;
    }

    public boolean hasAttachmentRequsetTrace()
    {
        return attachmentRequestTrace != null;
    }

    public AttachmentRequestTrace getAttachmentRequestTrace()
    {
        return attachmentRequestTrace;
    }

    void setAttachmentRequestTrace( AttachmentRequestTrace attachmentRequestTrace )
    {
        this.attachmentRequestTrace = attachmentRequestTrace;
    }

    public boolean hasImageRequestTrace()
    {
        return imageRequestTrace != null;
    }

    public ImageRequestTrace getImageRequestTrace()
    {
        return imageRequestTrace;
    }

    void setImageRequestTrace( ImageRequestTrace imageRequestTrace )
    {
        this.imageRequestTrace = imageRequestTrace;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        PortalRequestTrace that = (PortalRequestTrace) o;

        if ( requestNumber != that.requestNumber )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return (int) ( requestNumber ^ ( requestNumber >>> 32 ) );
    }

    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "url: " ).append( url ).append( "\n" );
        s.append( "duration: [" ).append( "\n" ).append( duration ).append( "]" ).append( "\n" );
        s.append( "requester: " ).append( requester ).append( "\n" );
        s.append( "siteKey: " ).append( siteKey ).append( "\n" );
        return s.toString();
    }
}
