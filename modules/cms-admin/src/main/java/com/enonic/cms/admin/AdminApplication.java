/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import com.enonic.cms.admin.spring.VaadinComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.Application;

@VaadinComponent
final public class AdminApplication
    extends Application
{
    public static final String PATH_TO_USER_ICON = "images/no_avatar.gif";

    public static final String PATH_TO_GROUP_ICON = "images/group_icon.jpg";

    @Autowired
    private AdminWindow adminWindow;


    @Override
    public void init()
    {
        setTheme( "enonic" );
        setMainWindow(adminWindow);
    }
}
