/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.security.RememberedLoginEntity;
import com.enonic.cms.core.security.RememberedLoginKey;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.security.user.UserKey;


public class RememberedLoginEntityDao
    extends AbstractBaseEntityDao<RememberedLoginEntity>
    implements RememberedLoginDao
{

    public RememberedLoginEntity findByKey( RememberedLoginKey key )
    {
        return get( RememberedLoginEntity.class, key );
    }

    public RememberedLoginEntity findByGuidAndSite( String guid, SiteKey siteKey )
    {
        return findSingleByNamedQuery( RememberedLoginEntity.class, "RememberedLoginEntity.findByGuidAndSite",
                                       new String[]{"guid", "siteKey"}, new Object[]{guid, siteKey} );
    }

    public RememberedLoginEntity findByUserKeyAndSiteKey( UserKey userKey, SiteKey siteKey )
    {
        return findSingleByNamedQuery( RememberedLoginEntity.class, "RememberedLoginEntity.findByUserKeyAndSiteKey",
                                       new String[]{"userKey", "siteKey"}, new Object[]{userKey, siteKey} );
    }

}