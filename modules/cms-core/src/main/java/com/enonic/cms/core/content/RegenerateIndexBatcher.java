/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;


public class RegenerateIndexBatcher
{

    private static final Logger LOG = LoggerFactory.getLogger( RegenerateIndexBatcher.class );

    private IndexService indexService;

    private ContentService contentService;

    public RegenerateIndexBatcher( IndexService indexService, ContentService contentService )
    {

        this.indexService = indexService;
        this.contentService = contentService;
    }

    public void regenerateIndex( ContentTypeEntity contentType, int batchSize, List<String> logEntries )
    {

        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Given contentType cannot be null" );
        }
        if ( batchSize <= 0 )
        {
            throw new IllegalArgumentException( "Given batchSize must be larger that zero" );
        }

        List<ContentKey> allContentKeys = contentService.findContentKeysByContentType( contentType );

        int currentIndex = 0;

        while ( currentIndex < allContentKeys.size() )
        {
            List<ContentKey> nextContentKeys = getNextContentKeys( allContentKeys, currentIndex, batchSize );

            if ( nextContentKeys != null && nextContentKeys.size() > 0 )
            {

                if ( logEntries != null )
                {
                    logEntries.add( "Regenerating indexes, (batch: " + ( currentIndex + 1 ) + " -> " + ( currentIndex + nextContentKeys.size() ) +
                                            " of total " + allContentKeys.size() + ") of content type '" + contentType.getName() +
                                            "'" );
                }

                LOG.info( "Regenerating indexes, (batch: " + ( currentIndex + 1 ) + " -> " + ( currentIndex + nextContentKeys.size() ) +
                                  " of total " + allContentKeys.size() + ") of content type '" + contentType.getName() + "'" );

                long start = System.currentTimeMillis();
                indexService.regenerateIndex( nextContentKeys );
                long end = System.currentTimeMillis();

                LOG.info( "Last batch took: " + ( ( end - start ) / 1000 ) + " sec" );

                currentIndex = currentIndex + batchSize;


            }
        }
    }

    private List<ContentKey> getNextContentKeys( List<ContentKey> allContentKeys, int currentIndex, int batchSize )
    {

        if ( currentIndex + batchSize > allContentKeys.size() )
        {
            return allContentKeys.subList( currentIndex, allContentKeys.size() );
        }

        return allContentKeys.subList( currentIndex, currentIndex + batchSize );
    }
}
