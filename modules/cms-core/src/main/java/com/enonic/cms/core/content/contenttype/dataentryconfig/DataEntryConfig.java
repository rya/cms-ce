/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

import com.enonic.cms.core.content.contenttype.CtySetConfig;

public interface DataEntryConfig
{

    public String getName();

    public boolean isRequired();

    public DataEntryConfigType getType();

    public String getDisplayName();

    public String getXpath();

    public String getRelativeXPath();

    public CtySetConfig getSetConfig();

    public void setSetConfig( CtySetConfig value );
}
