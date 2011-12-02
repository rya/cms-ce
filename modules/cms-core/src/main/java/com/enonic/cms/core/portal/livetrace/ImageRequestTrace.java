package com.enonic.cms.core.portal.livetrace;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;

/**
 * Oct 11, 2010
 */
public class ImageRequestTrace
    implements Trace
{
    private PortalRequestTrace portalRequestTrace;

    private Duration duration = new Duration();

    private ContentKey contentKey;

    private MaxLengthedString label;

    private MaxLengthedString imageParamQuality;

    private MaxLengthedString imageParamFormat;

    private MaxLengthedString imageParamFilter;

    private MaxLengthedString imageParamBackgroundColor;

    private MaxLengthedString imageName;

    private Long sizeInBytes;

    private boolean usedCachedResult = false;

    private long concurrencyBlockStartTime = 0;

    private long concurrencyBlockingTime = 0;

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

    void setContentKey( ContentKey contentKey )
    {
        this.contentKey = contentKey;
    }

    public String getLabel()
    {
        return label != null ? label.toString() : null;
    }

    void setLabel( String label )
    {
        this.label = new MaxLengthedString( label );
    }

    public String getImageParamFormat()
    {
        return imageParamFormat != null ? imageParamFormat.toString() : null;
    }

    void setImageParamFormat( String imageParamFormat )
    {
        this.imageParamFormat = new MaxLengthedString( imageParamFormat );
    }

    public String getImageParamQuality()
    {
        return imageParamQuality != null ? imageParamQuality.toString() : null;
    }

    public void setImageParamQuality( String imageParamQuality )
    {
        this.imageParamQuality = new MaxLengthedString( imageParamQuality );
    }

    public String getImageParamFilter()
    {
        return imageParamFilter != null ? imageParamFilter.toString() : null;
    }

    public void setImageParamFilter( String imageParamFilter )
    {
        this.imageParamFilter = new MaxLengthedString( imageParamFilter );
    }

    public String getImageParamBackgroundColor()
    {
        return imageParamBackgroundColor != null ? imageParamBackgroundColor.toString() : null;
    }

    public void setImageParamBackgroundColor( String imageParamBackgroundColor )
    {
        this.imageParamBackgroundColor = new MaxLengthedString( imageParamBackgroundColor );
    }

    public boolean isUsedCachedResult()
    {
        return usedCachedResult;
    }

    void setUsedCachedResult( boolean usedCachedResult )
    {
        this.usedCachedResult = usedCachedResult;
    }

    public boolean isConcurrencyBlocked()
    {
        return concurrencyBlockingTime > CONCURRENCY_BLOCK_THRESHOLD;
    }

    public long getConcurrencyBlockingTime()
    {
        return isConcurrencyBlocked() ? concurrencyBlockingTime : 0;
    }

    void startConcurrencyBlockTimer()
    {
        concurrencyBlockStartTime = System.currentTimeMillis();
    }

    void stopConcurrencyBlockTimer()
    {
        this.concurrencyBlockingTime = System.currentTimeMillis() - concurrencyBlockStartTime;
    }

    public String getImageName()
    {
        return imageName != null ? imageName.toString() : null;
    }

    void setImageName( String imageName )
    {
        this.imageName = new MaxLengthedString( imageName );
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
