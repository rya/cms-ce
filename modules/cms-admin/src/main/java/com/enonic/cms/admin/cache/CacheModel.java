package com.enonic.cms.admin.cache;

public final class CacheModel
{
    private String name;

    private int memoryCapacity;

    private int diskCapacity;

    private boolean diskOverflow;

    private int timeToLive;

    private int objectCount;

    private int cacheHits;

    private int cacheMisses;


    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getMemoryCapacity()
    {
        return memoryCapacity;
    }

    public void setMemoryCapacity( int memoryCapacity )
    {
        this.memoryCapacity = memoryCapacity;
    }

    public int getDiskCapacity()
    {
        return diskCapacity;
    }

    public void setDiskCapacity( int diskCapacity )
    {
        this.diskCapacity = diskCapacity;
    }

    public boolean isDiskOverflow()
    {
        return diskOverflow;
    }

    public void setDiskOverflow( boolean diskOverflow )
    {
        this.diskOverflow = diskOverflow;
    }

    public int getTimeToLive()
    {
        return timeToLive;
    }

    public void setTimeToLive( int timeToLive )
    {
        this.timeToLive = timeToLive;
    }

    public int getObjectCount()
    {
        return objectCount;
    }

    public void setObjectCount( int objectCount )
    {
        this.objectCount = objectCount;
    }

    public int getCacheHits()
    {
        return cacheHits;
    }

    public void setCacheHits( int cacheHits )
    {
        this.cacheHits = cacheHits;
    }

    public int getCacheMisses()
    {
        return cacheMisses;
    }

    public void setCacheMisses( int cacheMisses )
    {
        this.cacheMisses = cacheMisses;
    }
}
