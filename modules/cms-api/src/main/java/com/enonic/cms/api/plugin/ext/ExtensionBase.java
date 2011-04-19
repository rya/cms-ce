package com.enonic.cms.api.plugin.ext;

public abstract class ExtensionBase
    implements Extension
{
    private String displayName;

    public final String getDisplayName()
    {
        return this.displayName != null ? this.displayName : getClass().getSimpleName();
    }

    public final void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }
}
