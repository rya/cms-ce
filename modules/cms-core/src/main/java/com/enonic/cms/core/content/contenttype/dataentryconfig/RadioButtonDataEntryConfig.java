/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;


import java.util.LinkedHashMap;

public class RadioButtonDataEntryConfig
    extends SelectorDataEntryConfig
{
    public RadioButtonDataEntryConfig( String name, boolean required, String displayName, String xpath,
                                       LinkedHashMap<String, String> optionValuesWithDescriptions )
    {
        super( name, required, DataEntryConfigType.RADIOBUTTON, displayName, xpath, optionValuesWithDescriptions );
    }
}