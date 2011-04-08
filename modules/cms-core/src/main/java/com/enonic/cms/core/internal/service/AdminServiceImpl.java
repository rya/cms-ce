/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.engine.AdminEngine;

import com.enonic.cms.core.service.AdminService;

public class AdminServiceImpl
    implements AdminService
{

    public void setAdminEngine( AdminEngine value )
    {
        adminEngine = value;
    }

    protected AdminEngine adminEngine;

    /**
     *
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return adminEngine.initializeDatabaseSchema();
    }

    /**
     *
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initializeDatabaseValues()
        throws Exception
    {
        return adminEngine.initializeDatabaseValues();
    }
}
