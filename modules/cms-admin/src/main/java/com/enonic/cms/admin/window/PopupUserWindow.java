package com.enonic.cms.admin.window;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.AdminApplication;
import com.enonic.cms.admin.image.EmbeddedImageFactory;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/4/11
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PopupUserWindow
        extends Window
{
    private EmbeddedImageFactory imageFactory;

    public PopupUserWindow( String caption, UserEntity user, EmbeddedImageFactory imageFactory )
    {
        this.imageFactory = imageFactory;
        VerticalLayout windowLayout = new VerticalLayout();
        windowLayout.setSpacing( true );
        windowLayout.setMargin( true );
        windowLayout.setSpacing( true );
        Label windowCaption = new Label( "<h2>" + caption + "</h2>" );
        windowCaption.setContentMode( Label.CONTENT_XHTML );
        windowLayout.addComponent( windowCaption );
        windowLayout.setComponentAlignment( windowCaption, Alignment.MIDDLE_CENTER );
        windowLayout.addComponent( createUserDataBlock( user ) );
        windowLayout.addComponent( createControlButtons() );
        this.setContent( windowLayout );
        this.setBorder( 0 );
        this.setWidth( 400, Sizeable.UNITS_PIXELS );
        this.setHeight( 300, Sizeable.UNITS_PIXELS );
        this.setModal( true );
        this.setClosable( false );
        this.setResizable( false );
        this.setDraggable( false );
    }

    private Component createUserDataBlock( UserEntity user )
    {
        GridLayout gLayout = new GridLayout( 2, 2 );
        gLayout.setSpacing( true );
        Embedded userPhoto = null;
        if ( user.getPhoto() != null )
        {
            userPhoto = imageFactory.createEmbeddedImage( user.getPhoto() );
        }
        else
        {
            userPhoto = imageFactory.createEmbeddedImage( AdminApplication.PATH_TO_USER_ICON );
        }
        userPhoto.setHeight( 100, Sizeable.UNITS_PIXELS );
        userPhoto.setWidth( 100, Sizeable.UNITS_PIXELS );
        gLayout.addComponent( userPhoto, 0, 0, 0, 1 );
        Label userName = new Label(
                user.getDisplayName() + " (" + user.getQualifiedName().getUserStoreName() + "\\" + user.getName() +
                        ")" );
        Label userEmail = new Label( user.getEmail() );
        gLayout.addComponent( userName, 1, 0 );
        gLayout.addComponent( userEmail, 1, 1 );
        return gLayout;
    }

    protected Button createButton( String caption )
    {
        Button button = new Button( caption );
        button.setWidth( 100, Sizeable.UNITS_PIXELS );
        return button;
    }

    protected abstract Component createControlButtons();
}
