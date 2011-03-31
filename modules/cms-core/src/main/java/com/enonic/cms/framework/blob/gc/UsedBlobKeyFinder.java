/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.gc;

import java.util.Set;

import com.enonic.cms.framework.blob.BlobKey;

public interface UsedBlobKeyFinder
{
    public Set<BlobKey> findKeys()
        throws Exception;
}
