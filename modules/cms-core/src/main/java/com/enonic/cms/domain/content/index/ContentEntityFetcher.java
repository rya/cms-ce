/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index;

import java.util.List;
import java.util.Map;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;

/**
 * This interface defines the content entity fetcher.
 */
public interface ContentEntityFetcher
{

    Map<ContentKey, ContentEntity> fetch( List<ContentKey> keys );

}
