package com.enonic.cms.admin.tab.controls;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 4/25/11
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberPanel extends Panel {

    private AbstractOrderedLayout layout;

    private User user;

    public MemberPanel(){
        super();
        layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        VerticalLayout dataLayout = new VerticalLayout();
        dataLayout.setMargin(true);
        dataLayout.setSpacing(true);
        dataLayout.addComponent(createBreadCrumb());
        dataLayout.addComponent(createUserDataSection());
        setContent(layout);
        layout.addComponent(dataLayout);
        layout.addComponent(createButtonLayout());
    }

    private Component createButtonLayout(){
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSpacing(true);
        vLayout.setMargin(true);
        vLayout.addComponent(createButton("Edit"));
        vLayout.addComponent(createButton("Delete"));
        vLayout.addComponent(createButton("Copy"));
        vLayout.addComponent(createButton("Change pwd"));
        return vLayout;
    }

    private Button createButton(String caption){
        Button button = new Button(caption);
        button.setWidth(100, Sizeable.UNITS_PIXELS);
        return button;
    }

    private Component createBreadCrumb() {
        HorizontalLayout breadCrumbLayout = new HorizontalLayout();
        breadCrumbLayout.setMargin(true);
        breadCrumbLayout.setSpacing(true);
        Property nameProperty = new MethodProperty<User>(getUser(), "name");

        Property usProperty = new ObjectProperty<UserEntity>((UserEntity) getUser()){
            @Override
            public String toString(){
                return this.getValue().getUserStore().getName();
            }
        };
        Button usButton = new Button("Userstore");
        usButton.setStyleName(BaseTheme.BUTTON_LINK);
        Button nameUsButton = new Button();
        nameUsButton.setStyleName(BaseTheme.BUTTON_LINK);
        nameUsButton.setPropertyDataSource(usProperty);
        Label gtLabel = new Label(">");
        Label nameLabel = new Label(nameProperty);
        breadCrumbLayout.addComponent(usButton);
        breadCrumbLayout.addComponent(gtLabel);
        breadCrumbLayout.addComponent(nameUsButton);
        breadCrumbLayout.addComponent(gtLabel);
        breadCrumbLayout.addComponent(nameLabel);
        return breadCrumbLayout;
    }

    private Component createUserDataSection(){
        GridLayout gridLayout = new GridLayout(3, 4);
        gridLayout.setSpacing(true);
        gridLayout.setMargin(true);
        Property userNameProperty = new MethodProperty<User>(getUser(), "displayName");
        Property emailProperty = new MethodProperty<User>(getUser(), "email");
        Property usIdProperty = new MethodProperty<User>(getUser(), "name");
        Property usProperty = new ObjectProperty<UserEntity>((UserEntity) getUser()){
            @Override
            public String toString(){
                return this.getValue().getUserStore().getName();
            }
        };
        Label nameLabel = new Label(userNameProperty);
        Label emailTitle = new Label("E-mail:");
        Label emailLabel = new Label(emailProperty);
        Label usTitle = new Label("Userstore:");
        Label usLabel = new Label(usProperty);
        Label usIdTitle = new Label("Userid:");
        Label usIdLabel = new Label(usIdProperty);
        Label pic = new Label("pic");
        gridLayout.addComponent(nameLabel, 0, 0, 2, 0);
        gridLayout.addComponent(pic, 0, 1, 0, 3);
        gridLayout.addComponent(emailTitle, 1, 1);
        gridLayout.addComponent(emailLabel, 2, 1);
        gridLayout.addComponent(usIdTitle, 2, 1);
        gridLayout.addComponent(usIdLabel, 2, 2);
        gridLayout.addComponent(usTitle, 1, 3);
        gridLayout.addComponent(usLabel, 2, 3);

        return gridLayout;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
