/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import com.vaadin.Application;

final class AdminApplication
    extends Application
{
    @Override
    public void init()
    {
        setMainWindow( new AdminWindow() );
    }
}
