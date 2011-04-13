/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.engine.handlers.SystemHandler;

public final class AdminEngine
    extends BaseEngine
{
    private SystemHandler systemHandler;

    public boolean initializeDatabaseSchema()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseSchema();
    }

    public boolean initializeDatabaseValues()
        throws Exception
    {
        return this.systemHandler.initializeDatabaseValues();
    }

    public void setSystemHandler( SystemHandler systemHandler )
    {
        this.systemHandler = systemHandler;
    }
}
