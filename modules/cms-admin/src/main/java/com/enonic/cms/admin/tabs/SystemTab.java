/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import com.enonic.cms.admin.spring.VaadinComponent;
import com.vaadin.ui.Label;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@VaadinComponent
@TopLevelTab(title = "System", order = 4096)
public class SystemTab
        extends AbstractBaseTab
{
    public SystemTab()
    {
        addComponent( new Label("TODO") );
    }
}
