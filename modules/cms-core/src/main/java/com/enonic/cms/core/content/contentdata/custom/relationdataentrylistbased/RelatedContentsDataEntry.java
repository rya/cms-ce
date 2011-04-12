/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased;

import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;

public class RelatedContentsDataEntry
    extends AbstractRelationDataEntryListBasedInputDataEntry<RelatedContentDataEntry>
    implements RelationsDataEntry
{

    public RelatedContentsDataEntry( final DataEntryConfig config )
    {
        super( config, DataEntryType.RELATED_CONTENTS );
    }

    public RelatedContentsDataEntry add( final RelatedContentDataEntry entry )
    {
        super.addEntry( entry );
        return this;
    }

    protected void customValidate()
    {
        final RelatedContentDataEntryConfig config = (RelatedContentDataEntryConfig) super.getConfig();
        if ( !config.isMultiple() )
        {
            validateNotMultiple();
        }
    }

    public boolean breaksRequiredContract()
    {
        return entries.isEmpty();
    }

    private void validateNotMultiple()
    {
        if ( super.getEntries().size() > 1 )
        {
            throw new InvalidContentDataException( "Invalid field length, field " + super.getName() + " with xpath " + super.getXPath() +
                " is marked as multiple=false, but has more than one entry" );
        }
    }
}