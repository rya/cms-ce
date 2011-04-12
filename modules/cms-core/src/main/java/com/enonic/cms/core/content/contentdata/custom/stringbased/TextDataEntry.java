/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

public class TextDataEntry
    extends AbstractStringBasedInputDataEntry
{
    public TextDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.TEXT, value );
    }

    protected void customValidate()
    {
        TextDataEntryConfig config = (TextDataEntryConfig) super.getConfig();
        validateMaxLength( super.getValue(), config.getMaxLength(), super.getName(), super.getXPath() );
    }

    public boolean breaksRequiredContract()
    {
        return StringUtils.isBlank( value );
    }
}
