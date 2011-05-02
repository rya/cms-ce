/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import com.enonic.cms.admin.spring.VaadinComponent;
import com.vaadin.ui.Label;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@VaadinComponent
@TopLevelTab(title = "Dashboard", order = 1024)
public class DashboardTab
        extends AbstractBaseTab
{
    public DashboardTab()
    {
        addComponent( new Label("TODO") );
    }
}
