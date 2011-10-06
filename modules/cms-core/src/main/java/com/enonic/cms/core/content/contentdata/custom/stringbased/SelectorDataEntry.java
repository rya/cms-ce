/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.contentdata.InvalidContentDataException;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.TitleDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DropdownDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RadioButtonDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.SelectorDataEntryConfig;

public class SelectorDataEntry
    extends AbstractStringBasedInputDataEntry
    implements TitleDataEntry
{
    public SelectorDataEntry( DataEntryConfig config, String value )
    {
        super( config, DataEntryType.SELECTOR, stripNewLinesAndFormFeedsAndTabs( value ) );
    }

    public String getValueAsTitle()
    {
        Preconditions.checkNotNull( getValue() );
        return getValue();
    }

    protected void customValidate()
    {
        SelectorDataEntryConfig config = (SelectorDataEntryConfig) getConfig();
        if ( config instanceof RadioButtonDataEntryConfig || config instanceof DropdownDataEntryConfig )
        {
            if ( config.isRequired() )
            {
                if ( !config.containsOption( getValue() ) )
                {
                    throw new InvalidContentDataException(
                        "Value, " + getValue() + " is not a legal value for the " + config.getDisplayName() + " field." );
                }
            }
            else
            {
                if ( !( getValue() == null ) && !( getValue().equals( "" ) ) && !config.containsOption( getValue() ) )
                {
                    throw new InvalidContentDataException(
                        "Value, " + getValue() + " is not a legal value for the " + config.getDisplayName() + " field." );
                }
            }

        }
        else
        {
            throw new InvalidContentDataException( "Not a valid SelectorDataEntryConfig: " + config.getClass().getCanonicalName() );
        }
    }

    public boolean breaksRequiredContract()
    {
        return StringUtils.isEmpty( value );
    }
}