package com.enonic.cms.admin.timezone;

public final class TimezoneModel
{
    private String id;
    private String shortName;
    private String name;
    private int offset;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset( int offset )
    {
        this.offset = offset;
    }
}
