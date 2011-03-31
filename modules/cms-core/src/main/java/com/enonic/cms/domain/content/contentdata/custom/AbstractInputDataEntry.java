/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contentdata.custom;

import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfig;

public abstract class AbstractInputDataEntry
    extends AbstractDataEntry
    implements InputDataEntry
{

    protected AbstractInputDataEntry( DataEntryConfig config, DataEntryType type )
    {
        super( config, type );
    }
}
