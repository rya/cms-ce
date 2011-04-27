/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.tabs.AbstractBaseTab;

@Component
@Scope("vaadin")
final class AdminWindow
    extends Window
{
    @Resource
    private transient ApplicationContext applicationContext;

    public AdminWindow()
    {
        setCaption( "Enonic CMS" );
        setSizeFull();
    }

    @PostConstruct
    void init() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();

        Map<String, AbstractBaseTab> tabs = applicationContext.getBeansOfType( AbstractBaseTab.class );
        for (AbstractBaseTab tab : tabs.values()) {
            tabSheet.addTab( tab );
        }

        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1);

        setContent(layout);
    }

}
