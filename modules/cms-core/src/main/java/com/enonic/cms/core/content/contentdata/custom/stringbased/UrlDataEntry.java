/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.TitleDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class UrlDataEntry
    extends AbstractStringBasedInputDataEntry
    implements TitleDataEntry
{
    public UrlDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.URL, stripNewLinesAndFormFeedsAndTabs( value ) );
    }

    public String getValueAsTitle()
    {
        Preconditions.checkNotNull( getValue() );
        return getValue();
    }

    protected void customValidate()
    {
        UrlDataEntryConfig config = (UrlDataEntryConfig) super.getConfig();
        validateMaxLength( super.getValue(), config.getMaxLength(), super.getName(), super.getXPath() );
    }

    public boolean breaksRequiredContract()
    {
        return StringUtils.isEmpty( value );
    }
}