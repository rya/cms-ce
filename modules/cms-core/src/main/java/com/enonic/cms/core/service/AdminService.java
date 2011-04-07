/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import java.util.Map;

public interface AdminService
{
    public boolean initializeDatabaseSchema()
        throws Exception;

    public boolean initializeDatabaseValues()
        throws Exception;

    public Map getMenuMap()
        throws Exception;
}
