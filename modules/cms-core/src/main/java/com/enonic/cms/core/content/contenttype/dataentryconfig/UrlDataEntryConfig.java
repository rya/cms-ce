/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

public class UrlDataEntryConfig
    extends AbstractBaseDataEntryConfig
{

    private Integer maxLength;

    public UrlDataEntryConfig( String name, boolean required, String displayName, String xpath, Integer maxLength )
    {
        super( name, required, DataEntryConfigType.URL, displayName, xpath );
        this.maxLength = maxLength;
    }

    public Integer getMaxLength()
    {
        return maxLength;
    }

}   
