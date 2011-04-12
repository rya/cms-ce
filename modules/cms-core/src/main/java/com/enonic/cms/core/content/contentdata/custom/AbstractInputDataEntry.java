/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public abstract class AbstractInputDataEntry
    extends AbstractDataEntry
    implements InputDataEntry
{

    protected AbstractInputDataEntry( DataEntryConfig config, DataEntryType type )
    {
        super( config, type );
    }
}
