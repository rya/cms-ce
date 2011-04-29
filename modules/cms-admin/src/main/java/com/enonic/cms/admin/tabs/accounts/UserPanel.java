/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.UserServicesService;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.List;

@VaadinComponent
public class UserPanel
        extends HorizontalLayout
{

    private static final String PATH_TO_USER_ICON = "images/no_avatar.gif";

    @Autowired
    private transient UserServicesService userServicesService;

    private UserPanelBean userPanelBean;

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-left-panel" );
        setSizeFull();

        this.userPanelBean = new UserPanelBean();
        setSpacing( true );
        setMargin( true );

        VerticalLayout column1 = new VerticalLayout();

        column1.addComponent( createBreadcrumb() );
        column1.addComponent( createUserDataSection() );

        addComponent( column1 );
        setExpandRatio( column1, 2.0f );
        com.vaadin.ui.Component buttonPane = createButtonLayout();
        addComponent(buttonPane);
        setExpandRatio(buttonPane, 1.0f);

    }

    private AbstractLayout createBreadcrumb()
    {
        HorizontalLayout breadCrumbLayout = new HorizontalLayout();
        breadCrumbLayout.setSpacing( true );
        Button usButton = new Button( "Userstore" );
        usButton.setStyleName( BaseTheme.BUTTON_LINK );
        Button nameUsButton = new Button( "AD" );
        nameUsButton.setStyleName( BaseTheme.BUTTON_LINK );
        Label gtLabel = new Label( ">" );
        Label gtLabel1 = new Label( ">" );
        Property nameProperty = new ObjectProperty<UserPanelBean>( userPanelBean )
        {
            @Override
            public String toString()
            {
                return getValue().getName();
            }
        };
        Label nameLabel = new Label( nameProperty);
        breadCrumbLayout.addComponent( usButton );
        breadCrumbLayout.addComponent( gtLabel );
        breadCrumbLayout.addComponent( nameUsButton );
        breadCrumbLayout.addComponent( gtLabel1 );
        breadCrumbLayout.addComponent( nameLabel );
        return breadCrumbLayout;
    }

    private com.vaadin.ui.Component createUserDataSection()
    {
        GridLayout gridLayout = new GridLayout( 3, 4 );
        gridLayout.setSpacing( true );
        Property displayNameProperty = new ObjectProperty<UserPanelBean>( userPanelBean )
        {
            @Override
            public String toString()
            {
                return getValue().getDisplayName();
            }
        };
        Label nameLabel = new Label( displayNameProperty );
        nameLabel.setContentMode( Label.CONTENT_XHTML );
        Label emailTitle = new Label( "E-mail:" );
        Property emailProperty = new ObjectProperty<UserPanelBean>( userPanelBean )
        {
            @Override
            public String toString()
            {
                return getValue().getEmail();
            }
        };
        Label emailLabel = new Label( emailProperty );
        Label usTitle = new Label( "Userstore:" );
        Property usProperty = new ObjectProperty<UserPanelBean>( userPanelBean )
        {
            @Override
            public String toString()
            {
                return getValue().getUserStoreName();
            }
        };
        Label usLabel = new Label( usProperty );
        Label usIdTitle = new Label( "Userid:" );
        Property usIdProperty = new ObjectProperty<UserPanelBean>( userPanelBean )
        {
            @Override
            public String toString()
            {
                return getValue().getName();
            }
        };
        Label usIdLabel = new Label( usIdProperty );
        Embedded icon = new Embedded("", new ThemeResource( PATH_TO_USER_ICON ));
        gridLayout.addComponent( nameLabel, 0, 0, 2, 0 );
        gridLayout.addComponent( icon, 0, 1, 0, 3 );
        gridLayout.addComponent( emailTitle, 1, 1 );
        gridLayout.addComponent( emailLabel, 2, 1 );
        gridLayout.addComponent( usIdTitle, 1, 2 );
        gridLayout.addComponent( usIdLabel, 2, 2 );
        gridLayout.addComponent( usTitle, 1, 3 );
        gridLayout.addComponent( usLabel, 2, 3 );

        return gridLayout;
    }

    private com.vaadin.ui.Component createButtonLayout()
    {
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSpacing( true );
        vLayout.setMargin( true );
        vLayout.addComponent( createButton( "Edit" ) );
        vLayout.addComponent( createButton( "Delete" ) );
        vLayout.addComponent( createButton( "Copy" ) );
        vLayout.addComponent( createButton( "Change pwd" ) );
        return vLayout;
    }

    private Button createButton( String caption )
    {
        Button button = new Button( caption );
        button.setWidth( 100, Sizeable.UNITS_PIXELS );
        return button;
    }

    public void showUser( String userName )
    {
        List<User> users = userServicesService.findByCriteria( userName, "name", true );
        if ( users.size() > 0 )
        {
            User user = users.get( 0 );
            userPanelBean.setDisplayName( user.getDisplayName() );
            userPanelBean.setEmail( user.getEmail() );
            userPanelBean.setName( user.getName() );
            userPanelBean.setUserStoreName( user.getQualifiedName().getUserStoreName() );
            requestRepaintAll();
        }
    }

}

class UserPanelBean
{
    private String userStoreName = "";

    private String displayName = "";

    private String name = "";

    private String email = "";

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public void setUserStoreName( String userStoreName )
    {
        if ((userStoreName != null) && (userStoreName.trim().equals( "" ))){
            this.userStoreName = userStoreName;
        }else{
            this.userStoreName = "<none>";
        }
    }

    public String getDisplayName()
    {
        return "<h2>" + displayName + "</h2>";
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }


}