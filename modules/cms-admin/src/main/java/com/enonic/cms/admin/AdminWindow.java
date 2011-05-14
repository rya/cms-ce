/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.admin.spring.events.ApplicationCreatedEvent;
import com.enonic.cms.admin.spring.events.ApplicationEvent;
import com.enonic.cms.admin.spring.events.ApplicationEventListener;
import com.enonic.cms.admin.tabs.AbstractBaseTab;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

@VaadinComponent
final public class AdminWindow
        extends Window
        implements ApplicationEventListener
{
    @Resource
    private transient ApplicationContext applicationContext;

    public AdminWindow()
    {
        setCaption( "Enonic CMS" );
        setSizeFull();
    }

    /**
     * create tabs after AdminApplication is created - helps against cyclic dependencies
     * @param event event
     */
    @Override
    public void onApplicationEvent( ApplicationEvent event )
    {
        if ( event instanceof ApplicationCreatedEvent )
        {
            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();

            TabSheet tabSheet = new TabSheet();
            tabSheet.setSizeFull();

            Map<String, AbstractBaseTab> tabs = applicationContext.getBeansOfType( AbstractBaseTab.class );
            Map<Integer, AbstractBaseTab> sortedTabs = new TreeMap<Integer, AbstractBaseTab>();
            for ( AbstractBaseTab tab : tabs.values() )
            {
                sortedTabs.put(tab.getClass().getAnnotation( TopLevelTab.class ).order(), tab);
            }
            for ( AbstractBaseTab tab : sortedTabs.values() )
            {
                tabSheet.addTab( tab );
            }

            layout.addComponent( tabSheet );
            layout.setExpandRatio( tabSheet, 1 );

            setContent( layout );
        }
    }
}
