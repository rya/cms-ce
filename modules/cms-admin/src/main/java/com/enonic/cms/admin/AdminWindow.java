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
import com.vaadin.ui.Window;

import com.enonic.cms.admin.tabs.AbstractBaseTab;

@Component
@Scope("prototype")
final class AdminWindow
    extends Window
{
    @Resource
    private ApplicationContext applicationContext;

    public AdminWindow()
    {
        setWidth("100%");
        setHeight( "900px" );
        setCaption( "Enonic CMS" );
    }

    @PostConstruct
    void init() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setHeight( "650px" );
        tabSheet.setWidth("100%");

        Map<String, AbstractBaseTab> tabs = applicationContext.getBeansOfType( AbstractBaseTab.class );
        for (AbstractBaseTab tab : tabs.values()) {
            tabSheet.addTab( tab );
        }

        addComponent(tabSheet);
    }

}