/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class ImagesDataEntry
    extends AbstractRelationDataEntryListBasedInputDataEntry<ImageDataEntry>
    implements RelationsDataEntry
{
    public ImagesDataEntry( final DataEntryConfig config )
    {
        super( config, DataEntryType.IMAGES );
    }

    public ImagesDataEntry add( final ImageDataEntry entry )
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