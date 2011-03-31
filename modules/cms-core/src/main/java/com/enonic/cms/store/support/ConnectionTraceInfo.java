/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.util.Collection;

public interface ConnectionTraceInfo
{
    public long getAge();

    public String getConnectionId();

    public Collection<String> getStackTrace();
}
