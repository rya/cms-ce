/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import com.enonic.cms.admin.spring.VaadinComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.Application;

@VaadinComponent
final class AdminApplication
    extends Application
{
    @Autowired
    private AdminWindow adminWindow;


    @Override
    public void init()
    {
        setTheme( "enonic" );
        setMainWindow(adminWindow);
    }
}
