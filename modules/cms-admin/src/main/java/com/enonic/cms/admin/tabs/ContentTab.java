package com.enonic.cms.admin.tabs;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Label;

import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@Component
@Scope("prototype")
@TopLevelTab(title = "Content", order = 2048)
public class ContentTab
        extends AbstractBaseTab
{
    public ContentTab()
    {
        addComponent( new Label("TODO") );
    }
}
