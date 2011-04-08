/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

public interface AdminService
{
    public boolean initializeDatabaseSchema()
        throws Exception;

    public boolean initializeDatabaseValues()
        throws Exception;
}
