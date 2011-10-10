/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.contentkeybased;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class FileDataEntry
    extends AbstractContentKeyBasedInputDataEntry
    implements RelationDataEntry
{

    public FileDataEntry( DataEntryConfig config, ContentKey contentKey )
    {
        super( config, DataEntryType.FILE, contentKey );
    }

    protected void customValidate()
    {
        //Validation not implemented
    }

    public boolean breaksRequiredContract()
    {
        return contentKey == null;
    }


}
