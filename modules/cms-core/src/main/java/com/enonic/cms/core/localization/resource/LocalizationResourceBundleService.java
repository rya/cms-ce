/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization.resource;

import java.util.Locale;

import com.enonic.cms.core.localization.LocalizationResourceBundle;
import com.enonic.cms.domain.structure.SiteEntity;

/**
 * Created by rmy - Date: Apr 22, 2009
 */
public interface LocalizationResourceBundleService
{
    public LocalizationResourceBundle getResourceBundle( SiteEntity site, Locale locale );

}
