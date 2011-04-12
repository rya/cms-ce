/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;

public class UrlDataEntry
    extends AbstractStringBasedInputDataEntry
{
    public UrlDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.URL, value );
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