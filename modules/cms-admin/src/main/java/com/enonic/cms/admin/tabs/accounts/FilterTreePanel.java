/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.core.security.group.GroupStorageService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.UserServicesService;


@Component
@Scope("vaadin")
public class FilterTreePanel
        extends VerticalLayout
{
    @Autowired
    private TablePanel tablePanel;

    @Autowired
    private transient UserServicesService userServicesService;

    @Autowired
    private transient GroupStorageService groupStorageService;

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-spacer" );
        setSizeFull();
        setMargin( true );

        VerticalLayout column = new VerticalLayout();
        column.setSizeFull();

        final TextField search = new TextField();
        search.setWidth( "100%" );
        search.setInputPrompt( "search" );
        search.setStyleName( "accounts-search-text" );

        // init table
        List<User> users = userServicesService.findAll();
        tablePanel.showUsers(users);

        column.addComponent( search );
        column.setExpandRatio( search, 0 );

        createBoldLabel( column, "Type"  );
        // Users
        Long count = userServicesService.count();
        String present = String.format("Users (%s)", count);
        createCheckBox( column, present  );

        // Groups
        count = groupStorageService.count();
        present = String.format("Groups (%s)", count);
        createCheckBox( column, present  );

        createBoldLabel( column, "Userstores"  );
        createCheckBox( column, "AD (10)"  );
        createCheckBox( column, "Community (134)"  );

        for (int i=0;i<10;i++)
        createCheckBox( column, "LDAP2 (43)"  );

        addComponent( column );
        setComponentAlignment( column, Alignment.TOP_LEFT );
    }


    private void createBoldLabel( VerticalLayout column, String name )
    {
        Label label = new Label( name );
        label.setStyleName( "accounts-check-title" );
        column.addComponent( label );
        column.setExpandRatio( label, 0 );
        column.setComponentAlignment( label, Alignment.TOP_LEFT );
    }

    private void createCheckBox( VerticalLayout column, String title )
    {
        CheckBox checkBox = new CheckBox( title );
        checkBox.setStyleName( "accounts-check-filter" );

        column.addComponent( checkBox );
        column.setExpandRatio( checkBox, 0 );
        column.setComponentAlignment( checkBox, Alignment.TOP_LEFT );
    }
}
