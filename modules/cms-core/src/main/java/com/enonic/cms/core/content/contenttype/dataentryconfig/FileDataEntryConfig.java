/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;


public class FileDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    public FileDataEntryConfig( String name, boolean required, String displayName, String xpath )
    {
        super( name, required, DataEntryConfigType.FILE, displayName, xpath );
    }
}