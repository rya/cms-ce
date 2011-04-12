/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

public class HtmlAreaDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    public HtmlAreaDataEntryConfig( String name, boolean required, String displayName, String xpath )
    {
        super( name, required, DataEntryConfigType.HTMLAREA, displayName, xpath );
    }
}