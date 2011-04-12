/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import org.joda.time.DateTime;

import com.enonic.cms.domain.PathAndParamsToStringBuilder;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.structure.SiteEntity;

/**
 * Oct 6, 2010
 */
public class PortalRequestTrace
{
    private long requestNumber;

    private RequestMode mode;

    private String id;

    private String url;

    private Duration duration = new Duration();

    private String httpRequestRemoteAddress;

    private String httpRequestCharacterEncoding;

    private String httpRequestContentType;

    private String httpRequestUserAgent;

    private QualifiedUsername requester;

    private SiteKey siteKey;

    private String siteName;

    private SitePath sitePath;

    private String siteLocalPathAndParams;

    private String responseRedirect;

    private String responseForward;

    private PageRenderingTrace pageRenderingTrace;

    private WindowRenderingTrace windowRenderingTrace;

    private AttachmentRequestTrace attachmentRequestTrace;

    PortalRequestTrace( long requestNumber, String url )
    {
        this.requestNumber = requestNumber;
        this.url = url;
        this.id = String.valueOf( hashCode() );
    }

    public String getId()
    {
        return id;
    }

    public long getRequestNumber()
    {
        return requestNumber;
    }

    public String getUrl()
    {
        return url;
    }

    public RequestMode getMode()
    {
        return mode;
    }

    public void setMode( RequestMode mode )
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
        return httpRequestRemoteAddress;
    }

    public void setHttpRequestRemoteAddress( String httpRequestRemoteAddress )
    {
        this.httpRequestRemoteAddress = httpRequestRemoteAddress;
    }

    public String getHttpRequestCharacterEncoding()
    {
        return httpRequestCharacterEncoding;
    }

    public void setHttpRequestCharacterEncoding( String httpRequestCharacterEncoding )
    {
        this.httpRequestCharacterEncoding = httpRequestCharacterEncoding;
    }

    public String getHttpRequestContentType()
    {
        return httpRequestContentType;
    }

    public void setHttpRequestContentType( String httpRequestContentType )
    {
        this.httpRequestContentType = httpRequestContentType;
    }

    public String getHttpRequestUserAgent()
    {
        return httpRequestUserAgent;
    }

    public void setHttpRequestUserAgent( String httpRequestUserAgent )
    {
        this.httpRequestUserAgent = httpRequestUserAgent;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSite( SiteEntity site )
    {
        this.siteKey = site.getKey();
        this.siteName = site.getName();
    }

    public void setSitePath( SitePath sitePath )
    {
        this.sitePath = sitePath;
    }

    public String getSiteLocalUrl()
    {
        if ( siteLocalPathAndParams == null )
        {
            PathAndParamsToStringBuilder stringBuilder = new PathAndParamsToStringBuilder();
            this.siteLocalPathAndParams = stringBuilder.toString( sitePath.getPathAndParams() );
        }

        return siteLocalPathAndParams;
    }

    public QualifiedUsername getRequester()
    {
        return requester;
    }

    public void setRequester( QualifiedUsername requester )
    {
        this.requester = requester;
    }

    public String getResponseRedirect()
    {
        return responseRedirect;
    }

    public void setResponseRedirect( String responseRedirect )
    {
        this.responseRedirect = responseRedirect;
    }

    public String getResponseForward()
    {
        return responseForward;
    }

    public void setResponseForward( String responseForward )
    {
        this.responseForward = responseForward;
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

    public void setWindowRenderingTrace( WindowRenderingTrace windowRenderingTrace )
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
