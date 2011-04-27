/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("vaadin")
@TopLevelTab(title = "Accounts", order = 1024)
public class AccountsTab extends AbstractBaseTab
{
    @Autowired
    private AccordionPanel accordionPanel;

    @Autowired
    private TablePanel tablePanel;

    @Autowired
    private UserPanel userPanel;

    @PostConstruct
    private void init()
    {
        AbsoluteLayout top = new AbsoluteLayout();
        top.setWidth( "99%" );
        top.setHeight( "40px" );

        Label label = new Label( "<h2>Browse Accounts</h2>" );
        label.setContentMode( Label.CONTENT_XHTML );
        top.addComponent( label );

        ComboBox comboBox = new ComboBox();
        top.addComponent( comboBox, "top:5px; right:0px" );

        addComponent( top );
        setExpandRatio( top, 0 );

        HorizontalSplitPanel line = new HorizontalSplitPanel();
        line.setSplitPosition(200, Sizeable.UNITS_PIXELS);
        line.setSizeFull();

        line.addComponent( accordionPanel );
        HorizontalSplitPanel line2 = new HorizontalSplitPanel();
        line2.setSplitPosition(500, Sizeable.UNITS_PIXELS);
        line2.addComponent( tablePanel );
        line2.addComponent( userPanel );
        line.addComponent( line2 );

        addComponent( line );
        setExpandRatio( line, 1 );
    }

}
