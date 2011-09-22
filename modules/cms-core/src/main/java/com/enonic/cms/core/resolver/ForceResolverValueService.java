/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by rmy - Date: May 5, 2009
 */
public interface ForceResolverValueService
{

    public void setForcedValue( ResolverContext context, HttpServletResponse response, String forcedValueKey,
                                ForcedResolverValueLifetimeSettings lifeTimeSettings, String forceValue );

    public void clearForcedValue( ResolverContext context, HttpServletResponse response, String forcedValueKey );

    public String getForcedResolverValue( ResolverContext context, String forcedValueKey );

}
