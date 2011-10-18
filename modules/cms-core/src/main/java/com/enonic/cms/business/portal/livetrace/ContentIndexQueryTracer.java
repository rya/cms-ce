package com.enonic.cms.business.portal.livetrace;

import java.util.Collection;
import java.util.Iterator;

import com.enonic.cms.core.content.index.ContentIndexQuery;

public class ContentIndexQueryTracer
{
    public static ContentIndexQueryTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        if ( livePortalTraceService.tracingEnabled() )
        {
            return livePortalTraceService.startContentIndexQueryTracing();
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( ContentIndexQueryTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceQuery( ContentIndexQuery query, int index, int count, ContentIndexQueryTrace trace )
    {
        if ( trace != null )
        {
            trace.setIndex( index );
            trace.setCount( count );

            trace.setQuery( query.getQuery() );
            trace.setContentFilter( collectionToString( query.getContentFilter() ) );
            trace.setSectionFilter( collectionToString( query.getSectionFilter() ) );
            trace.setCategoryFilter( collectionToString( query.getCategoryFilter() ) );
            trace.setContentTypeFilter( collectionToString( query.getContentTypeFilter() ) );
            trace.setCategoryAccessTypeFilter( collectionToString( query.getCategoryAccessTypeFilter() ) );
            trace.setSecurityFilter( collectionToString( query.getSectionFilter() ) );
        }
    }

    public static void traceMatchCount( int matchCount, ContentIndexQueryTrace trace )
    {
        if ( trace != null )
        {
            trace.setMatchCount( matchCount );
        }
    }

    private static String collectionToString( Collection collection )
    {
        if ( collection == null || collection.size() == 0 )
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator it = collection.iterator();
        while ( it.hasNext() )
        {
            sb.append( it.next() );

            if ( it.hasNext() )
            {
                sb.append( ", " );
            }
        }

        return sb.toString();
    }
}
