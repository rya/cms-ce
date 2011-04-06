/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AdminEngine;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.business.core.resource.ResourceService;

import com.enonic.cms.domain.security.user.User;

public class AdminServiceImpl
    implements AdminService
{

    public void setAdminEngine( AdminEngine value )
    {
        adminEngine = value;
    }

    protected AdminEngine adminEngine;

    private ResourceService resourceService;

    public void setResourceService( ResourceService value )
    {
        this.resourceService = value;
    }


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

    private String doGetAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {
        Document doc = this.adminEngine.getAdminMenu( user, menuKeys, menuItemTypes, includeReadOnlyAccessRight );
        return XMLTool.documentToString( doc );
    }

    /**
     * Return a map of top level menus with name.
     */
    public Map<Integer, String> getMenuMap()
        throws Exception
    {
        return this.adminEngine.getMenuMap();
    }

    public long getArchiveSizeByCategory( int categoryKey )
    {
        return adminEngine.getArchiveSizeByCategory( categoryKey );
    }

    public long getArchiveSizeByUnit( int unitKey )
    {
        return adminEngine.getArchiveSizeByUnit( unitKey );
    }

}
