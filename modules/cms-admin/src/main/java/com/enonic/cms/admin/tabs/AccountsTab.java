/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import javax.annotation.PostConstruct;

import com.enonic.cms.admin.AdminWindow;
import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.admin.tabs.accounts.AccordionPanel;
import com.enonic.cms.admin.tabs.accounts.TablePanel;
import com.enonic.cms.admin.tabs.accounts.UserPanel;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

@VaadinComponent
@TopLevelTab(title = "Accounts", order = 2048)
public class AccountsTab
        extends AbstractBaseTab
{
    @Autowired
    private AccordionPanel accordionPanel;

    @Autowired
    private AdminWindow adminWindow;

    @Autowired
    private TablePanel tablePanel;

    @Autowired
    private UserPanel userPanel;

    private OptionGroup selectView;

    @PostConstruct
    private void init()
    {
        AbsoluteLayout top = new AbsoluteLayout();
        top.setWidth( "99%" );
        top.setHeight( "40px" );

        Label label = new Label( "<h2>Browse Accounts</h2>" );
        label.setContentMode( Label.CONTENT_XHTML );
        top.addComponent( label );

        MenuBar bar = new MenuBar();
        MenuBar.MenuItem newItem = bar.addItem( "New", null, null );
        newItem.addItem( "New (User)", null, null );
        newItem.addItem( "New (Group)", null, null );
        top.addComponent( bar, "top:5px; right:0px" );
        OptionGroup selectView = new OptionGroup();
        selectView.setHeight( 25, Sizeable.UNITS_PIXELS );
        selectView.addItem( "Overview" );
        selectView.addItem( "Browse" );
        selectView.addItem( "Views" );
        selectView.setStyleName( "selectView" );
        selectView.setImmediate(true);
        selectView.addListener( new Property.ValueChangeListener()
        {
            public void valueChange( Property.ValueChangeEvent event )
            {
                getWindow().showNotification( "Selected city: " + event.getProperty() );

            }
        } );
        addComponent( top );
        addComponent( selectView );
        setSpacing( true );
        setComponentAlignment( selectView, Alignment.TOP_CENTER );
        setExpandRatio( selectView, 0 );
        setExpandRatio( top, 0 );

        HorizontalSplitPanel line = new HorizontalSplitPanel();
        line.setSplitPosition( 200, Sizeable.UNITS_PIXELS );
        line.setSizeFull();

        line.addComponent( accordionPanel );
        HorizontalSplitPanel line2 = new HorizontalSplitPanel();
        line2.setSplitPosition( 500, Sizeable.UNITS_PIXELS );
        line2.addComponent( tablePanel );
        line2.addComponent( userPanel );
        line.addComponent( line2 );

        VerticalLayout space = new VerticalLayout();
        space.setHeight( 60, Sizeable.UNITS_PIXELS );
        addComponent( line );
        addComponent( space );
        setExpandRatio( line, 1 );
    }

}
