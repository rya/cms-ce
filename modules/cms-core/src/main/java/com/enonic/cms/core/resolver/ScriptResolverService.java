/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resolver.ScriptResolverResult;
import com.enonic.cms.domain.resource.ResourceFile;

/**
 * Created by rmy - Date: Apr 29, 2009
 */
public interface ScriptResolverService
{
    public ScriptResolverResult resolveValue( ResolverContext context, ResourceFile localeResolverScript );
}
