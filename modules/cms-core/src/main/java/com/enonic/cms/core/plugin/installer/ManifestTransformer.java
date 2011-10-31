package com.enonic.cms.core.plugin.installer;

import java.io.IOException;
import java.util.Set;
import java.util.jar.Manifest;

interface ManifestTransformer
{
    public void transform(final Manifest mf, final Set<String> entries)
        throws IOException;
}
