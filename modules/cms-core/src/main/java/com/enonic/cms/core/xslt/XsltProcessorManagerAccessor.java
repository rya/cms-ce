/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

/**
 * This class implements the processor manager accessor.
 */
public final class XsltProcessorManagerAccessor
{
    /**
     * Processor manager.
     */
    private static XsltProcessorManager PROCESSOR_MANAGER;

    /**
     * Return the processor manager.
     */
    public static XsltProcessorManager getProcessorManager()
    {
        return PROCESSOR_MANAGER;
    }

    /**
     * Set the processor manager.
     */
    public static void setProcessorManager( XsltProcessorManager processorManager )
    {
        PROCESSOR_MANAGER = processorManager;
    }
}
