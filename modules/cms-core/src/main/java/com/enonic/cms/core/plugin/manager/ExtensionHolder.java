package com.enonic.cms.core.plugin.manager;

import java.util.List;

import org.osgi.framework.Bundle;

import com.enonic.cms.api.plugin.ext.Extension;

interface ExtensionHolder
{
    public List<Extension> getAll();

    public List<Extension> getAllForBundle(final Bundle bundle);
}
