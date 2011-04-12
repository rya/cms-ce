/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.contentkeybased;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class RelatedContentDataEntry
    extends AbstractContentKeyBasedInputDataEntry
    implements RelationDataEntry
{
    public RelatedContentDataEntry( DataEntryConfig config, ContentKey contentKey )
    {
        super( config, DataEntryType.RELATED_CONTENT, contentKey );
    }

    protected void customValidate()
    {

    }

    public boolean breaksRequiredContract()
    {
        return contentKey == null;
    }
}
