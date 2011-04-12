/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.resolver.ForcedResolverValueLifetimeSettings;
import com.enonic.cms.core.resolver.ResolverContext;

/**
 * Created by rmy - Date: Apr 22, 2009
 */
public interface LocaleResolverService
{
    public Locale getLocale( ResolverContext context );

    public void setForcedLocale( ResolverContext context, HttpServletResponse response, ForcedResolverValueLifetimeSettings setting,
                                 String localeString );

    public void resetLocale( ResolverContext context, HttpServletResponse response );

}
