/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import com.enonic.cms.admin.spring.VaadinComponent;
import com.vaadin.ui.Label;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@VaadinComponent
@TopLevelTab(title = "Reports", order = 3072)
public class ReportsTab
        extends AbstractBaseTab
{
    public ReportsTab()
    {
        addComponent( new Label("TODO") );
    }
}
