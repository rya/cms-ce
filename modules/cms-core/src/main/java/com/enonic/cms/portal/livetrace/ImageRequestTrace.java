package com.enonic.cms.portal.livetrace;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;


public class ImageRequestTrace
{
    private PortalRequestTrace portalRequestTrace;

    private Duration duration = new Duration();

    private ContentKey contentKey;

    private String label;

    private String imageParamQuality;

    private String imageParamFormat;

    private String imageParamFilter;

    private String imageParamBackgroundColor;

    private String imageName;

    private Long sizeInBytes;

    private Boolean usedCachedResult;

    ImageRequestTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
    }

    void setStartTime( DateTime start )
    {
        duration.setStartTime( start );
    }

    void setStopTime( DateTime stop )
    {
        duration.setStopTime( stop );
    }

    public Duration getDuration()
    {
        return duration;
    }

    public PortalRequestTrace getPortalRequestTrace()
    {
        return portalRequestTrace;
    }

    public void setPortalRequestTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public String getImageParamFormat()
    {
        return imageParamFormat;
    }

    public void setImageParamFormat( String imageParamFormat )
    {
        this.imageParamFormat = imageParamFormat;
    }

    public String getImageParamQuality()
    {
        return imageParamQuality;
    }

    public void setImageParamQuality( String imageParamQuality )
    {
        this.imageParamQuality = imageParamQuality;
    }

    public String getImageParamFilter()
    {
        return imageParamFilter;
    }

    public void setImageParamFilter( String imageParamFilter )
    {
        this.imageParamFilter = imageParamFilter;
    }

    public String getImageParamBackgroundColor()
    {
        return imageParamBackgroundColor;
    }

    public void setImageParamBackgroundColor( String imageParamBackgroundColor )
    {
        this.imageParamBackgroundColor = imageParamBackgroundColor;
    }

    public Boolean getUsedCachedResult()
    {
        return usedCachedResult;
    }

    void setUsedCachedResult( Boolean usedCachedResult )
    {
        this.usedCachedResult = usedCachedResult;
    }

    public String getImageName()
    {
        return imageName;
    }

    void setImageName( String imageName )
    {
        this.imageName = imageName;
    }

    public Long getSizeInBytes()
    {
        return sizeInBytes;
    }

    void setSizeInBytes( Long sizeInBytes )
    {
        this.sizeInBytes = sizeInBytes;
    }
}
