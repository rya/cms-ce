package com.enonic.cms.core.plugin;

import com.enonic.cms.api.plugin.ext.Extension;

public interface ExtensionListener
{
    public void extensionAdded( Extension ext );

    public void extensionRemoved( Extension ext );
}
