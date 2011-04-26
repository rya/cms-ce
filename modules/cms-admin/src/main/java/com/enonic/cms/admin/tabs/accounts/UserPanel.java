/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.core.security.user.User;

@Component
@Scope("vaadin")
public class UserPanel
        extends VerticalLayout
{
    private Label username;

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-left-panel" );
        setSizeFull();

        VerticalLayout columns = new VerticalLayout();
        columns.setSpacing( true );
        columns.setMargin( true );

        VerticalLayout column1 = new VerticalLayout();

        column1.addComponent( createPath() );

        Label label = new Label( "<h2>User Name</h2>" );
        label.setContentMode( Label.CONTENT_XHTML );
        column1.addComponent( label );

        columns.addComponent( column1 );

        VerticalLayout column2 = new VerticalLayout();
        columns.addComponent( column2 );

        addComponent( columns );
    }

    private AbstractLayout createPath()
    {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing( true );
        layout.addComponent( new Link("Userstore", null) );
        layout.addComponent( new Label(">") );
        layout.addComponent( new Link("AD", null) );
        layout.addComponent( new Label(">") );
        username = new Label( "" );
        layout.addComponent( username );
        return layout;
    }


    public void showUser( User user, String caption )
    {
        String content = caption + " (" + user.getName() + ")";
        username.setPropertyDataSource(new ObjectProperty<String>(content, String.class)  );

    }
}