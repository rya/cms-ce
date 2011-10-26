/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jdbc;

import org.springframework.beans.factory.InitializingBean;

import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.upgrade.UpgradeService;

/**
 * This class implements the database initializer. It will initialize the database if not initialized before.
 */
public final class DatabaseInitializer
    implements InitializingBean
{
    /**
     * Admin service.
     */
    private AdminService adminService;

    private UpgradeService upgradeService;

    private UserStoreService userStoreService;

    /**
     * Set the admin service.
     */
    public void setAdminService( AdminService adminService )
    {
        this.adminService = adminService;
    }

    public void setUpgradeService( UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    /**
     * Initialize the database.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        this.adminService.initializeDatabaseSchema();


       // databaseInitializerService.
        this.adminService.initializeDatabaseValues();

        if ( !this.upgradeService.needsUpgrade() )
        {
            this.userStoreService.initializeUserStores();
        }
    }

    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }
}
