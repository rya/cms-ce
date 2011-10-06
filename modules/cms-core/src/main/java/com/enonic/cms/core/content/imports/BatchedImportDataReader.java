/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

public class BatchedImportDataReader
    implements ImportDataReader
{
    final private ImportDataReader dataReader;

    final private long batchSize;

    private long entryCounter = 0;

    public BatchedImportDataReader( final ImportDataReader dataReader, final long batchSize )
    {
        this.dataReader = dataReader;
        this.batchSize = batchSize;
    }

    public ImportDataEntry getNextEntry()
    {
        if ( entryCounter >= batchSize )
        {
            // reached end of batch 
            return null;
        }

        entryCounter++;
        return dataReader.getNextEntry();
    }

    public boolean hasMoreEntries()
    {
        if ( entryCounter >= batchSize )
        {
            return false;
        }

        return dataReader.hasMoreEntries();
    }

    public void startNewBatch()
    {
        entryCounter = 0;
    }
}
