package com.enonic.cms.admin.common;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class LoadStoreRequest
{
    @DefaultValue("0") @QueryParam("start")
    private int start;

    @DefaultValue("10") @QueryParam("limit")
    private int limit;

    @QueryParam("sort")
    private String sort;

    @DefaultValue("ASC") @QueryParam("dir")
    private String dir;

    @QueryParam("query")
    private String query;

    public int getStart()
    {
        return this.start;
    }

    public void setStart(final int start)
    {
        this.start = start;
    }

    public int getLimit()
    {
        return this.limit;
    }

    public void setLimit(final int limit)
    {
        this.limit = limit;
    }

    public String getSort()
    {
        return this.sort;
    }

    public void setSort(final String sort)
    {
        this.sort = sort;
    }

    public String getSortDir()
    {
        return this.dir;
    }

    public void setSortDir(final String dir)
    {
        if ("DESC".equalsIgnoreCase(dir)) {
            this.dir = "DESC";
        } else {
            this.dir = "ASC";
        }
    }

    public String getQuery()
    {
        return this.query;
    }

    public void setQuery(final String query)
    {
        this.query = query;
    }
}
