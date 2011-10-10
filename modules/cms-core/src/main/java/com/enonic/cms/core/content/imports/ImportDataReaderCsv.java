/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.enonic.esl.util.StringUtil;

import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringArraySourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringSourceValue;

public class ImportDataReaderCsv
    extends AbstractImportDataReader
{
    private final List<ImportDataEntry> entries = new ArrayList<ImportDataEntry>();

    private ImportDataEntry prefetchedNextDataEntry;

    public ImportDataReaderCsv( final CtyImportConfig config, final InputStream data )
        throws ImportCSVSourceException
    {
        super( config );

        readAllLines( data );
    }

    public ImportDataEntry getNextEntry()
    {
        return fetchNextEntry();
    }

    public boolean hasMoreEntries()
    {
        ImportDataEntry next;
        if ( prefetchedNextDataEntry != null )
        {
            next = prefetchedNextDataEntry;
        }
        else
        {
            next = fetchNextEntry();
            prefetchedNextDataEntry = next;
        }

        return next != null;
    }

    private ImportDataEntry fetchNextEntry()
    {
        if ( prefetchedNextDataEntry != null )
        {
            ImportDataEntry next = prefetchedNextDataEntry;
            prefetchedNextDataEntry = null;
            return next;
        }

        if ( entries.size() == 0 )
        {
            return null;
        }
        return entries.remove( 0 );
    }

    private void readAllLines( final InputStream data )
        throws ImportCSVSourceException
    {
        BufferedReader br;

        try
        {
            br = new BufferedReader( new InputStreamReader( data, "UTF-8" ) );
        }
        catch ( IOException e )
        {
            throw new ImportCSVSourceException( "Failed to create input stream: " + e.getMessage(), e );
        }

        int lineNumber = 1;
        String line = readNextLine( br, lineNumber );
        while ( line != null )
        {
            if ( lineNumber <= config.getSkip() )
            {
                lineNumber++;
                line = readNextLine( br, lineNumber );
                continue;
            }
            else if ( StringUtils.isBlank( line ) )
            {
                // skipping blank lines
                lineNumber++;
                line = readNextLine( br, lineNumber );
                continue;
            }
            else
            {
                final LineParser lineParser = new LineParser( lineNumber, line );
                final ImportDataEntry entry = lineParser.parse();

                entries.add( entry );

                lineNumber++;
                line = readNextLine( br, lineNumber );
            }
        }
    }

    private String readNextLine( BufferedReader br, int lineNumber )
    {
        try
        {
            return br.readLine();
        }
        catch ( IOException e )
        {
            throw new ImportCSVSourceException( lineNumber, e );
        }
    }


    private class LineParser
    {
        private int lineNumber;

        private String line;

        private LineParser( int lineNumber, String line )
        {
            this.lineNumber = lineNumber;
            this.line = line;
        }

        private ImportDataEntry parse()
        {
            final ImportDataEntry entry = new ImportDataEntry( config.getSyncMapping() );

            final String[] columnValues = StringUtil.splitString( line, config.getSeparator(), true );
            addMappings( entry, columnValues );
            addMetadataMappings( entry, columnValues );
            return entry;
        }

        private void addMappings( final ImportDataEntry entry, final String[] fields )
        {
            for ( CtyImportMappingConfig mapping : config.getMappings() )
            {
                AbstractSourceValue value = getSourceValue( fields, mapping );
                entry.add( mapping, value );
            }
        }

        private void addMetadataMappings( final ImportDataEntry entry, final String[] columnValues )
        {
            for ( CtyImportMappingConfig metadataMapping : config.getMetadataMappings() )
            {
                AbstractSourceValue value = getSourceValue( columnValues, metadataMapping );
                entry.addMetadata( metadataMapping, value );
            }
        }

        private AbstractSourceValue getSourceValue( final String[] columnValues, final CtyImportMappingConfig mapping )
        {
            AbstractSourceValue value = null;
            int index = Integer.valueOf( mapping.getSource() ) - 1;
            if ( index < columnValues.length )
            {
                String columnValue = columnValues[index];
                if ( !mapping.isMetaDataMapping() && mapping.isMultiple() && mapping.getSeparator() != null )
                {
                    final String[] values = StringUtil.splitString( columnValue, mapping.getSeparator(), false );
                    value = new StringArraySourceValue( values );
                }
                else
                {
                    value = new StringSourceValue( columnValue );
                }
            }
            else
            {
                throw new ImportCSVSourceException( lineNumber, line, "No column at position " + mapping.getSource() + " (destination = '" +
                    mapping.getDestination() + "')." );
            }

            if ( mapping.hasAdditionalSource() )
            {
                int exIndex = Integer.valueOf( mapping.getAdditionalSource() ) - 1;
                if ( exIndex < columnValues.length )
                {
                    value.setAdditionalValue( columnValues[exIndex] );
                }
            }
            return value;
        }

    }

}