/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business;

import com.enonic.cms.core.structure.SiteProperties;

import com.enonic.cms.core.SiteKey;

public interface SitePropertiesService
{

    SiteProperties getSiteProperties( SiteKey siteKey );

    String getProperty( String key, SiteKey siteKey );

    Integer getPropertyAsInteger( String key, SiteKey siteKey );

    Boolean getPropertyAsBoolean( String key, SiteKey siteKey );
}
