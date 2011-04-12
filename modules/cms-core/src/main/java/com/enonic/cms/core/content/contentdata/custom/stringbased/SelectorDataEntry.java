/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class SelectorDataEntry
    extends AbstractStringBasedInputDataEntry
{
    public SelectorDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.SELECTOR, value );
    }

    protected void customValidate()
    {
        //No validation implemented
    }

    public boolean breaksRequiredContract()
    {
        return StringUtils.isEmpty( value );
    }
}