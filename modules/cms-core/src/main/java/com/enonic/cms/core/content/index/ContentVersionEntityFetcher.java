/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.ContentVersionEntity;

/**
 * This interface defines the content entity fetcher.
 */
public interface ContentVersionEntityFetcher
{

    Map<ContentVersionKey, ContentVersionEntity> fetch( List<ContentVersionKey> keys );

}
