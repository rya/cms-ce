package com.enonic.cms.admin.common;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class LoadStoreRequest
{
    @DefaultValue("0") @QueryParam("start")
    private int index;

    @DefaultValue("10") @QueryParam("limit")
    private int count;

    @DefaultValue("") @QueryParam("sort")
    private String sort;

    @DefaultValue("ASC") @QueryParam("dir")
    private String dir;

    public int getIndex()
    {
        return this.index;
    }

    public void setIndex(final int index)
    {
        this.index = index;
    }

    public int getCount()
    {
        return this.count;
    }

    public void setCount(final int count)
    {
        this.count = count;
    }

    public String getSort()
    {
        return this.sort.equals("") ? null : this.sort;
    }

    public void setSort(final String sort)
    {
        this.sort = (sort == null) ? "" : sort;
    }

    public String getDirection()
    {
        return this.dir;
    }

    public void setDirection(final String dir)
    {
        if ("DESC".equalsIgnoreCase(dir)) {
            this.dir = "DESC";
        } else {
            this.dir = "ASC";
        }
    }
}
