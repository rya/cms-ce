/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;


public class ContentLogEntrySpecification
    extends LogEntrySpecification
{
    private boolean allowDeletedContent = false;

    public boolean isAllowDeletedContent()
    {
        return allowDeletedContent;
    }


}