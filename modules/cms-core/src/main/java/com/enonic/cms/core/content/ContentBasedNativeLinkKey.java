/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.domain.nativelink.NativeLinkKey;

/**
 * Feb 15, 2010
 */
public interface ContentBasedNativeLinkKey
    extends NativeLinkKey
{
    ContentKey getContentKey();
}
