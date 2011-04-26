/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Component
@Scope("prototype")
public class AccordionPanel
        extends VerticalLayout
{
    @Autowired
    private FilterTreePanel filterTreePanel;

    @PostConstruct
    private void init()
    {
        addStyleName( "accounts-left-panel" );
        setWidth( "180px" );

        Accordion accordion = new Accordion();
        accordion.setHeight( "500px" );
        accordion.setWidth( "100%" );
        accordion.addTab( filterTreePanel, "Filter", getIcon() );
        accordion.addTab( new VerticalLayout(), "Recent", getIcon() );

        addComponent( accordion );
    }


}
