/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class FilesDataEntry
    extends AbstractRelationDataEntryListBasedInputDataEntry<FileDataEntry>
    implements RelationsDataEntry
{
    public FilesDataEntry( final DataEntryConfig config )
    {
        super( config, DataEntryType.FILES );
    }

    public FilesDataEntry add( final FileDataEntry entry )
    {
        super.addEntry( entry );
        return this;
    }

    protected void customValidate()
    {
        //Validation not implemented
    }

    public boolean breaksRequiredContract()
    {
        return entries.isEmpty();
    }
}