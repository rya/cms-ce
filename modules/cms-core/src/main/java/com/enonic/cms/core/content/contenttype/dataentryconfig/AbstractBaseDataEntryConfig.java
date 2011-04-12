/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

import com.enonic.cms.core.content.contenttype.CtySetConfig;

public abstract class AbstractBaseDataEntryConfig
    implements DataEntryConfig
{

    private String name;

    private boolean required = false;

    private DataEntryConfigType type;

    private String displayName;

    private String xpath;

    private String relativeXPath;

    private CtySetConfig setConfig;

    public AbstractBaseDataEntryConfig( String name, boolean required, DataEntryConfigType type, String displayName, String xpath )
    {

        this.name = name;
        this.required = required;
        this.type = type;
        this.displayName = displayName;
        this.xpath = xpath;

        this.relativeXPath = xpath;
    }

    public String getName()
    {
        return name;
    }

    public boolean isRequired()
    {
        return required;
    }

    public DataEntryConfigType getType()
    {
        return type;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getXpath()
    {
        return xpath;
    }

    public String getRelativeXPath()
    {
        return relativeXPath;
    }

    public CtySetConfig getSetConfig()
    {
        return setConfig;
    }

    public void setSetConfig( CtySetConfig value )
    {
        this.setConfig = value;
    }

    public String toString()
    {
        return getName();
    }

}

