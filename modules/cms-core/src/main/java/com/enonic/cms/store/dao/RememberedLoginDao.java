/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.RememberedLoginEntity;
import com.enonic.cms.domain.security.RememberedLoginKey;
import com.enonic.cms.domain.security.user.UserKey;


public interface RememberedLoginDao
    extends EntityDao<RememberedLoginEntity>
{
    RememberedLoginEntity findByKey( RememberedLoginKey key );

    RememberedLoginEntity findByGuidAndSite( String guid, SiteKey siteKey );

    RememberedLoginEntity findByUserKeyAndSiteKey( UserKey userKey, SiteKey siteKey );

}