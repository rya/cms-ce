/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import com.enonic.cms.admin.spring.VaadinComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.VerticalLayout;

@VaadinComponent
public class AccordionPanel
        extends Accordion
{
    @Autowired
    private FilterTreePanel filterTreePanel;

    public AccordionPanel()
    {
        setSizeFull();
    }

    @PostConstruct
    private void init()
    {
        addStyleName( "accounts-left-panel" );

        addTab( filterTreePanel, "Filter", getIcon() );
        addTab( new VerticalLayout(), "Recent", getIcon() );
    }


}
