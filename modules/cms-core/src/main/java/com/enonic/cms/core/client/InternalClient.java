/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.core.service.PresentationService;
import com.enonic.cms.core.service.UserServicesService;

/**
 * This interface defines the internal client.
 */
public interface InternalClient
    extends LocalClient
{

    public KeyService getKeyService();

    public PresentationService getPresentationService();

    public UserServicesService getUserServicesService();

    public DataSourceService getDataSourceService();

    public AdminService getAdminService();
}
