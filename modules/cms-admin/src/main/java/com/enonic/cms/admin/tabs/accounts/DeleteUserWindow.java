package com.enonic.cms.admin.tabs.accounts;

import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.image.EmbeddedImageFactory;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/4/11
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteUserWindow extends PopupUserWindow
{
    public DeleteUserWindow (UserEntity user, EmbeddedImageFactory imageFactory){
        super("Delete User", user, imageFactory);
    }


    @Override
    protected Component createControlButtons()
    {
        final Window currentWindow = this;
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing( true );
        horizontalLayout.setWidth( 100, Sizeable.UNITS_PERCENTAGE );
        Button cancelButton = createButton( "Cancel" );
        cancelButton.addListener( new Button.ClickListener(){
            @Override
            public void buttonClick( Button.ClickEvent clickEvent )
            {
                currentWindow.getParent().removeWindow( currentWindow );
            }
        });
        Button deleteButton = createButton( "Delete" );
        horizontalLayout.addComponent( cancelButton );
        horizontalLayout.addComponent( deleteButton );
        return horizontalLayout;
    }
}
