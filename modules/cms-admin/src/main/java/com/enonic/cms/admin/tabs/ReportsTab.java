package com.enonic.cms.admin.tabs;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Label;

import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@Component
@Scope("prototype")
@TopLevelTab(title = "Reports", order = 3072)
public class ReportsTab
        extends AbstractBaseTab
{
    public ReportsTab()
    {
        addComponent( new Label("TODO") );
    }
}