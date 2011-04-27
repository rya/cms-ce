/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Label;

import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@Component
@Scope("prototype")
@TopLevelTab(title = "System", order = 4096)
public class SystemTab
        extends AbstractBaseTab
{
    public SystemTab()
    {
        addComponent( new Label("TODO") );
    }
}
