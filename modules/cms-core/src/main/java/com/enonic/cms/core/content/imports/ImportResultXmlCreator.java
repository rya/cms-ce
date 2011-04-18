/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.content.ContentKey;

public class ImportResultXmlCreator
{
    private boolean includeContentInformation = false;

    public void setIncludeContentInformation( final boolean includeContentInformation )
    {
        this.includeContentInformation = includeContentInformation;
    }

    public Document getReport( final ImportResult importResult )
    {
        final Element root = new Element( "importreport" );
        root.setAttribute( "elapsedTimeInSeconds", String.valueOf( importResult.getElapsedTimeInSeconds() ) );
        final Document doc = new Document( root );

        root.addContent( createReportElement( "inserted", importResult.getInserted() ) );
        root.addContent( createReportElement( "updated", importResult.getUpdated() ) );
        root.addContent( createReportElement( "skipped", importResult.getSkipped() ) );
        root.addContent( createReportElement( "archived", importResult.getArchived() ) );
        root.addContent( createReportElement( "deleted", importResult.getDeleted() ) );
        root.addContent( createReportElement( "remaining", importResult.getRemaining() ) );
        root.addContent( createReportElement( "alreadyArchived", importResult.getAlreadyArchived() ) );

        return doc;
    }

    private Element createReportElement( final String name, final Map<ContentKey, String> entries )
    {
        final Element reportEl = new Element( name );
        reportEl.setAttribute( "count", String.valueOf( entries.size() ) );

        if ( includeContentInformation )
        {
            for ( Map.Entry<ContentKey, String> entry : entries.entrySet() )
            {
                final Element contentInfoEl = new Element( "content" );
                contentInfoEl.setAttribute( "key", entry.getKey().toString() );
                contentInfoEl.setAttribute( "title", entry.getValue() );
                reportEl.addContent( contentInfoEl );
            }
        }
        return reportEl;
    }
}
