/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.Map;

import com.enonic.cms.core.content.ContentKey;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

public class ImportResultXmlCreator
{
    private boolean includeContentInformation = false;

    public void setIncludeContentInformation( final boolean includeContentInformation )
    {
        this.includeContentInformation = includeContentInformation;
    }

    public XMLDocument getReport( final ImportResult importResult )
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

        return XMLDocumentFactory.create( doc );
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
/* ---- OLD report generating -----

    private static Document createImportReport( AdminService admin, boolean includeContents, int[] inserted, int[] updated, int[] skipped,
                                               int[] archived, int[] deleted, int[] remaining )
    {
        Document reportDoc = XMLTool.createDocument( "importreport" );
        Element reportElem = reportDoc.getDocumentElement();

        // inserts
        Element insertedElem = XMLTool.createElement( reportDoc, reportElem, "inserted" );
        insertedElem.setAttribute( "count", String.valueOf( inserted.length ) );
        if ( includeContents )
        {
            appendContentElems( admin, inserted, insertedElem );
        }

        // updates
        Element updatedElem = XMLTool.createElement( reportDoc, reportElem, "updated" );
        updatedElem.setAttribute( "count", String.valueOf( updated.length ) );
        if ( includeContents )
        {
            appendContentElems( admin, updated, updatedElem );
        }

        // skips
        Element skippedElem = XMLTool.createElement( reportDoc, reportElem, "skipped" );
        skippedElem.setAttribute( "count", String.valueOf( skipped.length ) );
        if ( includeContents )
        {
            appendContentElems( admin, skipped, skippedElem );
        }

        // archives
        Element archivedElem = XMLTool.createElement( reportDoc, reportElem, "archived" );
        archivedElem.setAttribute( "count", String.valueOf( archived.length ) );
        if ( includeContents )
        {
            appendContentElems( admin, archived, archivedElem );
        }

        // deletes
        Element deletedElem = XMLTool.createElement( reportDoc, reportElem, "deleted" );
        deletedElem.setAttribute( "count", String.valueOf( deleted.length ) );
        if ( includeContents )
        {
            appendContentElems( admin, deleted, deletedElem );
        }

        // remaining
        Element remainingElem = XMLTool.createElement( reportDoc, reportElem, "remaining" );
        remainingElem.setAttribute( "count", String.valueOf( remaining.length ) );
        if ( includeContents )
        {
            appendContentElems( admin, remaining, remainingElem );
        }

        return reportDoc;
    }

    private static void appendContentElems( AdminService admin, int[] keys, Element parent )
    {
        Document contentTitlesDoc = XMLTool.domparse( admin.getContentTitles( keys ) );
        Element[] contentTitleElems = XMLTool.getElements( contentTitlesDoc.getDocumentElement() );
        for ( Element contentTitleElem : contentTitleElems )
        {
            Element contentElem = XMLTool.createElement( parent, "content" );
            contentElem.setAttribute( "key", contentTitleElem.getAttribute( "key" ) );
            contentElem.setAttribute( "title", XMLTool.getElementText( contentTitleElem ) );
        }
    }
*/

}
