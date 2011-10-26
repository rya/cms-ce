/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.cache;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

public interface Cache
{
    @ManagedAttribute
    long getTimeToLiveInSeconds();

    @ManagedAttribute
    long getMaxElementsInMemory();

    @ManagedAttribute
    long getObjectCount();

    @ManagedAttribute
    long getCacheHits();

    @ManagedAttribute
    long getCacheMisses();

    @ManagedOperation
    void clearCache();

    @ManagedOperation
    void clearStatistics();
}
