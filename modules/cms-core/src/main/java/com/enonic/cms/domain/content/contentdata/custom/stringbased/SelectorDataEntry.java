/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contentdata.custom.stringbased;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.domain.content.contentdata.custom.DataEntryType;
import com.enonic.cms.domain.content.contentdata.custom.TitleDataEntry;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfig;

public class SelectorDataEntry
    extends AbstractStringBasedInputDataEntry
    implements TitleDataEntry
{
    public SelectorDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.SELECTOR, value );
    }

    public String getValueAsTitle()
    {
        Preconditions.checkNotNull( getValue() );
        return getValue();
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