/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.Application;

@Component
@Scope("vaadin")
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
