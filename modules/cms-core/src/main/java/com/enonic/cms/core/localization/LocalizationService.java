/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

import java.util.Locale;

import com.enonic.cms.core.structure.SiteEntity;

/**
 * Created by rmy - Date: Apr 22, 2009
 */
public interface LocalizationService
{
    public String getLocalizedPhrase( SiteEntity site, String phrase, Locale locale );

    public String getLocalizedPhrase( SiteEntity site, String phrase, Object[] arguments, Locale locale );

}
