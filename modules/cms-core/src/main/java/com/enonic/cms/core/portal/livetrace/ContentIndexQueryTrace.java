package com.enonic.cms.core.portal.livetrace;

import org.joda.time.DateTime;

public class ContentIndexQueryTrace
    implements Trace
{
    private Duration duration = new Duration();

    private int index;

    private int count;

    private int matchCount;

    private MaxLengthedString query;

    private String contentFilter;

    private String sectionFilter;

    private String categoryFilter;

    private String contentTypeFilter;

    private String securityFilter;

    private String categoryAccessTypeFilter;

    ContentIndexQueryTrace()
    {
    }

    public Duration getDuration()
    {
        return duration;
    }

    void setStartTime( DateTime startTime )
    {
        duration.setStartTime( startTime );
    }

    void setStopTime( DateTime stopTime )
    {
        duration.setStopTime( stopTime );
    }

    public int getIndex()
    {
        return index;
    }

    void setIndex( int index )
    {
        this.index = index;
    }

    public int getCount()
    {
        return count;
    }

    void setCount( int count )
    {
        this.count = count;
    }

    public String getQuery()
    {
        return query != null ? query.toString() : null;
    }

    void setQuery( String query )
    {
        this.query = new MaxLengthedString( query, 6000 );
    }

    public String getContentFilter()
    {
        return contentFilter;
    }

    void setContentFilter( String contentFilter )
    {
        this.contentFilter = contentFilter;
    }

    public String getSectionFilter()
    {
        return sectionFilter;
    }

    void setSectionFilter( String sectionFilter )
    {
        this.sectionFilter = sectionFilter;
    }

    public String getCategoryFilter()
    {
        return categoryFilter;
    }

    void setCategoryFilter( String categoryFilter )
    {
        this.categoryFilter = categoryFilter;
    }

    public String getContentTypeFilter()
    {
        return contentTypeFilter;
    }

    void setContentTypeFilter( String contentTypeFilter )
    {
        this.contentTypeFilter = contentTypeFilter;
    }

    public String getSecurityFilter()
    {
        return securityFilter;
    }

    void setSecurityFilter( String securityFilter )
    {
        this.securityFilter = securityFilter;
    }

    public String getCategoryAccessTypeFilter()
    {
        return categoryAccessTypeFilter;
    }

    void setCategoryAccessTypeFilter( String categoryAccessTypeFilter )
    {
        this.categoryAccessTypeFilter = categoryAccessTypeFilter;
    }

    public int getMatchCount()
    {
        return matchCount;
    }

    void setMatchCount( int matchCount )
    {
        this.matchCount = matchCount;
    }


}
