/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.link;

public final class LinkMatch
    implements Comparable<LinkMatch>
{
    private final String link;

    private final int start;

    private final int end;

    public LinkMatch( String link, int start, int end )
    {
        this.link = link;
        this.start = start;
        this.end = end;
    }

    public String getLink()
    {
        return this.link;
    }

    public int getStart()
    {
        return this.start;
    }

    public int getEnd()
    {
        return this.end;
    }

    public int compareTo( LinkMatch linkMatch )
    {
        return this.start - linkMatch.start;
    }

    public String toString()
    {
        return this.link + "[" + this.start + "-" + this.end + "]";
    }
}
