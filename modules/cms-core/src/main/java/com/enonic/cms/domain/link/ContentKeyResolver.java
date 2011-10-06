/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.link;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;

public interface ContentKeyResolver
{
    ContentKey resolvFromBinaryKey( BinaryDataKey binaryDataKey );
}
