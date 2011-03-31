/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.util.Collection;

import javax.sql.DataSource;

public interface TraceableDataSource
    extends DataSource
{
    public Collection<ConnectionTraceInfo> getTraceInfo();
}
