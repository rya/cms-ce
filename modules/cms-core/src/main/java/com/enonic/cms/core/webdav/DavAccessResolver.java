/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import com.enonic.cms.domain.security.user.UserEntity;

public interface DavAccessResolver
{
    boolean hasAccess( UserEntity userEntity );
}
