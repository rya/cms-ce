/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.SystemHandler;

public final class AdminEngine
    extends BaseEngine
{
    private CommonHandler commonHandler;

    private SystemHandler systemHandler;

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

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

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void setSystemHandler( SystemHandler systemHandler )
    {
        this.systemHandler = systemHandler;
    }
}
