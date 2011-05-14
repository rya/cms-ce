/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import javax.annotation.PostConstruct;

import com.enonic.cms.admin.AdminWindow;
import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.admin.tabs.accounts.AccordionPanel;
import com.enonic.cms.admin.tabs.accounts.DetailsPanel;
import com.enonic.cms.admin.tabs.accounts.MultipleSelectionPanel;
import com.enonic.cms.admin.tabs.accounts.NewItemMenu;
import com.enonic.cms.admin.tabs.accounts.TablePanel;
import com.enonic.cms.admin.tabs.accounts.UserPanel;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

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

    @Autowired
    private OptionGroup selectView;

    @Autowired
    private NewItemMenu newItemMenu;

    @Autowired
    private DetailsPanel detailsPanel;

    @Autowired
    private MultipleSelectionPanel multipleSelectionPanel;

    @PostConstruct
    private void init()
    {
        AbsoluteLayout top = new AbsoluteLayout();
        top.setWidth( "99%" );
        top.setHeight( "40px" );

        Label label = new Label( "<h2>Browse Accounts</h2>" );
        label.setContentMode( Label.CONTENT_XHTML );
        top.addComponent( label );

        addComponent( top );
        addComponent( selectView );
        setSpacing( true );
        setComponentAlignment( selectView, Alignment.TOP_CENTER );
        setExpandRatio( selectView, 0 );
        setExpandRatio( top, 0 );

        HorizontalSplitPanel line = new HorizontalSplitPanel();
        line.setSplitPosition( 200, Sizeable.UNITS_PIXELS );
        line.setSizeFull();
        // Filter component
        VerticalLayout filterComponent = new VerticalLayout();
        filterComponent.setSpacing( true );
        filterComponent.setHeight( 100, Sizeable.UNITS_PERCENTAGE );
        filterComponent.addComponent( newItemMenu );
        filterComponent.addComponent( accordionPanel );
        filterComponent.setExpandRatio( accordionPanel, 1.0f );
        line.addComponent( filterComponent );

        VerticalSplitPanel line2 = new VerticalSplitPanel();
        line2.setSizeFull();
        line2.setSplitPosition( 350, Sizeable.UNITS_PIXELS );
        line2.addComponent( tablePanel );
        line2.addComponent( detailsPanel );
        line.addComponent( line2 );

        VerticalLayout space = new VerticalLayout();
        space.setHeight( 60, Sizeable.UNITS_PIXELS );
        addComponent( line );
        addComponent( space );
        setExpandRatio( line, 1 );
    }

}
