package com.enonic.cms.admin.window;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.image.EmbeddedImageFactory;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/5/11
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChangePasswordUserWindow extends PopupUserWindow
{
    public ChangePasswordUserWindow( UserEntity user, EmbeddedImageFactory imageFactory )
    {
        super( "Change password", user, imageFactory );
        setHeight( 350, Sizeable.UNITS_PIXELS );
    }

    @Override
    protected Component createControlButtons()
    {
        final Window currentWindow = this;
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSpacing( true );

        //Creating rows for window control elements
        HorizontalLayout row1 = new HorizontalLayout();
        HorizontalLayout row2 = new HorizontalLayout();
        HorizontalLayout row3 = new HorizontalLayout();
        row1.setWidth( 100, Sizeable.UNITS_PERCENTAGE );
        row2.setWidth( 100, Sizeable.UNITS_PERCENTAGE );
        row3.setWidth( 100, Sizeable.UNITS_PERCENTAGE );

        //Creating control elements
        Label newPassLabel = new Label("New Password*");
        newPassLabel.setWidth( 120, Sizeable.UNITS_PIXELS );
        Label confPass = new Label("Confirm Password*");
        confPass.setWidth( 120, Sizeable.UNITS_PIXELS );
        PasswordField pass = new PasswordField();
        PasswordField passRepeat = new PasswordField();
        Button cancelButton = createButton( "Cancel" );
        cancelButton.addListener( new Button.ClickListener(){
            @Override
            public void buttonClick( Button.ClickEvent clickEvent )
            {
                currentWindow.getParent().removeWindow( currentWindow );
            }
        } );
        Button changeButton = createButton( "Change" );

        //Add elements to rows
        row1.addComponent( newPassLabel );
        row1.addComponent( pass );
        row2.addComponent( confPass );
        row2.addComponent( passRepeat );
        row3.addComponent( cancelButton );
        row3.addComponent( changeButton );

        //Add rows to result layout
        vLayout.addComponent( row1 );
        vLayout.addComponent( row2 );
        vLayout.addComponent( row3 );
        return vLayout;
    }
}
