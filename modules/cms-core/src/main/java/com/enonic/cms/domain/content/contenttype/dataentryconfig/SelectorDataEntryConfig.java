/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contenttype.dataentryconfig;

import java.util.LinkedHashMap;

public class SelectorDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    LinkedHashMap<String, String> optionValuesWithDescriptions;

    public SelectorDataEntryConfig( String name, boolean required, DataEntryConfigType type, String displayName, String xpath,
                                    LinkedHashMap<String, String> optionValuesWithDescriptions )
    {
        super( name, required, type, displayName, xpath );
        this.optionValuesWithDescriptions = optionValuesWithDescriptions;
    }

    public boolean containsOption( String value )
    {
        return optionValuesWithDescriptions.keySet().contains( value );
    }
}
