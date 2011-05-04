/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.AdminApplication;
import com.enonic.cms.admin.AdminWindow;
import com.enonic.cms.admin.image.EmbeddedImageFactory;
import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.admin.window.PopupWindowFactory;
import com.enonic.cms.core.security.IAccordionPresentation;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.UserServicesService;

@VaadinComponent
public class TablePanel
        extends Table
{
    private static final String TYPE = "type";

    private static final String DISPLAY_NAME = "display name";

    private static final String QUALIFIED_NAME = "qualified name";

    private static final String LAST_MODIFIED = "last modified";

    private static final Action ACTION_EDIT = new Action( "Edit" );

    private static final Action ACTION_DELETE = new Action( "Delete" );

    private static final Action ACTION_COPY = new Action( "Copy" );

    private static final Action ACTION_CHANGEPWD = new Action( "Change password" );

    @Autowired
    private EmbeddedImageFactory imageFactory;

    @Autowired
    private UserServicesService userServicesService;

    @Autowired
    private AdminWindow adminWindow;

    @Autowired
    private UserPanel userPanel;

    @Autowired
    private PopupWindowFactory windowFactory;

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-table" );
        setSelectable( true );
        setSizeFull();

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty( TYPE, Embedded.class, null );
        container.addContainerProperty( DISPLAY_NAME, String.class, null );
        container.addContainerProperty( QUALIFIED_NAME, String.class, null );
        container.addContainerProperty( LAST_MODIFIED, String.class, null );

        this.addActionHandler( new Action.Handler()
        {

            @Override
            public Action[] getActions( Object o, Object o1 )
            {
                if ( o instanceof User )
                {
                    return new Action[]{ACTION_COPY, ACTION_EDIT, ACTION_DELETE, ACTION_CHANGEPWD};
                }
                else
                {
                    return new Action[]{ACTION_COPY, ACTION_EDIT, ACTION_DELETE};
                }
            }

            @Override
            public void handleAction( Action action, Object sender, Object target )
            {
                if (action.equals( ACTION_DELETE )){
                    if (target instanceof UserEntity){
                        UserEntity user = (UserEntity)target;
                        user = (UserEntity) userServicesService.getUserByKey( user.getKey() );
                        Window deleteWindow = windowFactory.createDeleteWindow( user );
                        adminWindow.addWindow( deleteWindow );
                    }
                }
            }
        } );
        this.addListener( new ItemClickEvent.ItemClickListener()
        {
            @Override
            public void itemClick( ItemClickEvent itemClickEvent )
            {
                String userName = itemClickEvent.getItem().getItemProperty( QUALIFIED_NAME ).toString();
                userPanel.showUser( userName );
            }
        } );
        setContainerDataSource( container );
    }

    public void showIssues( List<IAccordionPresentation> issues )
    {
        String caption = issues.isEmpty() ? "" : String.format( "%s matches", issues.size() );
        setCaption( caption );

        Container container = getContainerDataSource();
        container.removeAllItems();

        for ( IAccordionPresentation issue : issues )
        {
            Item item = container.addItem( issue );

            Embedded icon = null;
            if ( issue instanceof User )
            {
                if ( ( (UserEntity) issue ).getPhoto() != null )
                {
                    byte[] photoBytes = ( (UserEntity) issue ).getPhoto();
                    icon = imageFactory.createEmbeddedImage( photoBytes);

                }
                else
                {
                    icon = imageFactory.createEmbeddedImage( AdminApplication.PATH_TO_USER_ICON );
                }
                icon.setHeight( 45, Sizeable.UNITS_PIXELS );
                icon.setWidth( 45, Sizeable.UNITS_PIXELS );
                item.getItemProperty( TYPE ).setValue( icon );
            }
            else if ( issue instanceof GroupEntity )
            {
                icon = new Embedded( "", new ThemeResource( AdminApplication.PATH_TO_GROUP_ICON ) );
                icon.setHeight( 45, Sizeable.UNITS_PIXELS );
                icon.setWidth( 45, Sizeable.UNITS_PIXELS );
                item.getItemProperty( TYPE ).setValue( icon );
            }

            item.getItemProperty( DISPLAY_NAME ).setValue( issue.getDisplayName() );

            item.getItemProperty( QUALIFIED_NAME ).setValue( issue.getQualifiedName().toString() );

            item.getItemProperty( LAST_MODIFIED ).setValue( issue.getISODate() );
        }
    }
}
