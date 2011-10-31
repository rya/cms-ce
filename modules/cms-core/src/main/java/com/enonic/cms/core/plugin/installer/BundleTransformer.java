package com.enonic.cms.core.plugin.installer;

import java.io.IOException;
import java.io.InputStream;

interface BundleTransformer
{
    public InputStream transform(final InputStream in)
        throws IOException;
}
