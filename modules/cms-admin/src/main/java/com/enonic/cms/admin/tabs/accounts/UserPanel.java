/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("vaadin")
public class UserPanel
        extends HorizontalLayout
{

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-left-panel" );
        setSizeFull();

        setSpacing(true);
        setMargin(true);

        VerticalLayout column1 = new VerticalLayout();

        column1.addComponent( createBreadcrumb() );
        column1.addComponent(createUserDataSection());

        addComponent(column1);

        addComponent(createButtonLayout());

    }

    private static AbstractLayout createBreadcrumb()
    {
        HorizontalLayout breadCrumbLayout = new HorizontalLayout();
        breadCrumbLayout.setSpacing(true);
        Button usButton = new Button("Userstore");
        usButton.setStyleName(BaseTheme.BUTTON_LINK);
        Button nameUsButton = new Button("AD");
        nameUsButton.setStyleName(BaseTheme.BUTTON_LINK);
        Label gtLabel = new Label(">");
        Label gtLabel1 = new Label(">");
        Label nameLabel = new Label("tsi");
        breadCrumbLayout.addComponent(usButton);
        breadCrumbLayout.addComponent(gtLabel);
        breadCrumbLayout.addComponent(nameUsButton);
        breadCrumbLayout.addComponent(gtLabel1);
        breadCrumbLayout.addComponent(nameLabel);
        return breadCrumbLayout;
    }

    private static com.vaadin.ui.Component createUserDataSection(){
        GridLayout gridLayout = new GridLayout(3, 4);
        gridLayout.setSpacing(true);
        Label nameLabel = new Label("<h2>Thomas Sigdestad</h2>");
        nameLabel.setContentMode( Label.CONTENT_XHTML );
        Label emailTitle = new Label("E-mail:");
        Label emailLabel = new Label("tsi@enonic.com");
        Label usTitle = new Label("Userstore:");
        Label usLabel = new Label("AD");
        Label usIdTitle = new Label("Userid:");
        Label usIdLabel = new Label("tsi");
        Label pic = new Label("pic");
        gridLayout.addComponent(nameLabel, 0, 0, 2, 0);
        gridLayout.addComponent(pic, 0, 1, 0, 3);
        gridLayout.addComponent(emailTitle, 1, 1);
        gridLayout.addComponent(emailLabel, 2, 1);
        gridLayout.addComponent(usIdTitle, 1, 2);
        gridLayout.addComponent(usIdLabel, 2, 2);
        gridLayout.addComponent(usTitle, 1, 3);
        gridLayout.addComponent(usLabel, 2, 3);

        return gridLayout;
    }

    private static com.vaadin.ui.Component createButtonLayout(){
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSpacing(true);
        vLayout.setMargin(true);
        vLayout.addComponent(createButton("Edit"));
        vLayout.addComponent(createButton("Delete"));
        vLayout.addComponent(createButton("Copy"));
        vLayout.addComponent(createButton("Change pwd"));
        return vLayout;
    }

	    private static Button createButton(String caption){
        Button button = new Button(caption);
        button.setWidth(100, Sizeable.UNITS_PIXELS);
        return button;
    }

}
