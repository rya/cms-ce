/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Embedded;
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
        implements Table.HeaderClickListener, Action.Handler, ItemClickEvent.ItemClickListener,
        Table.ValueChangeListener
{
    private static final String CHECKBOX = "checkbox";

    private static final String TYPE = "type";

    private static final String DISPLAY_NAME = "display name";

    private static final String QUALIFIED_NAME = "qualified name";

    private static final String LAST_MODIFIED = "last modified";

    private static final Action ACTION_EDIT = new Action( "Edit" );

    private static final Action ACTION_DELETE = new Action( "Delete" );

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

    private Boolean allChecked = Boolean.FALSE;

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-table" );
        setSelectable( true );
        setMultiSelect( true );
        setImmediate( true );
        setSizeFull();
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty( CHECKBOX, CheckBox.class, new CheckBox() );
        setColumnHeader( CHECKBOX, "" );
        setColumnIcon( CHECKBOX, new ThemeResource( "images/uncheckedbox.gif" ) );
        container.addContainerProperty( TYPE, Embedded.class, null );
        container.addContainerProperty( DISPLAY_NAME, String.class, null );
        container.addContainerProperty( QUALIFIED_NAME, String.class, null );
        container.addContainerProperty( LAST_MODIFIED, String.class, null );

        addActionHandler( this );
        addListener( (ItemClickEvent.ItemClickListener) this );
        addListener( (HeaderClickListener) this );
        addListener( (Table.ValueChangeListener) this );
        setContainerDataSource( container );
    }

    public void showIssues( List<IAccordionPresentation> issues )
    {
        String caption = issues.isEmpty() ? "" : String.format( "%s matches", issues.size() );
        setCaption( caption );

        Container container = getContainerDataSource();
        container.removeAllItems();
        setValue( null );

        for ( final IAccordionPresentation issue : issues )
        {
            Item item = container.addItem( issue );

            Embedded icon = null;
            if ( issue instanceof User )
            {
                if ( ( (UserEntity) issue ).getPhoto() != null )
                {
                    byte[] photoBytes = ( (UserEntity) issue ).getPhoto();
                    icon = imageFactory.createEmbeddedImage( photoBytes );

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
            CheckBox ch = new CheckBox();
            ch.setImmediate( true );
            ch.addListener( new ValueChangeListener()
            {
                @Override
                public void valueChange( Property.ValueChangeEvent valueChangeEvent )
                {
                    Set newItemIds = new HashSet( (Collection) getValue() );
                    if ( Boolean.TRUE.equals( valueChangeEvent.getProperty().getValue() ) )
                    {
                        newItemIds.add( issue );
                    }
                    else
                    {
                        newItemIds.remove( issue );
                    }
                    if ( newItemIds.size() != 0 )
                    {
                        setValue( newItemIds );
                    }
                    else
                    {
                        setValue( null );
                    }
                }
            } );
            item.getItemProperty( CHECKBOX ).setValue( ch );
            item.getItemProperty( DISPLAY_NAME ).setValue( issue.getDisplayName() );

            item.getItemProperty( QUALIFIED_NAME ).setValue( issue.getQualifiedName().toString() );

            item.getItemProperty( LAST_MODIFIED ).setValue( issue.getISODate() );
        }
    }

    /*
     * Events handlers
     */

    @Override
    public void headerClick( HeaderClickEvent headerClickEvent )
    {
        if ( CHECKBOX.equals( headerClickEvent.getPropertyId() ) )
        {
            allChecked = !allChecked;
            if ( allChecked )
            {
                setColumnIcon( CHECKBOX, new ThemeResource( "images/checkedbox.gif" ) );
            }
            else
            {
                setColumnIcon( CHECKBOX, new ThemeResource( "images/uncheckedbox.gif" ) );
            }
            for ( Object o : getItemIds() )
            {
                CheckBox ch = (CheckBox) getItem( o ).getItemProperty( CHECKBOX ).getValue();
                ch.setValue( allChecked );
            }
        }
    }

    @Override
    public Action[] getActions( Object o, Object o1 )
    {
        if ( o instanceof User )
        {
            return new Action[]{ACTION_EDIT, ACTION_DELETE, ACTION_CHANGEPWD};
        }
        else
        {
            return new Action[]{ACTION_EDIT, ACTION_DELETE};
        }
    }

    @Override
    public void handleAction( Action action, Object sender, Object target )
    {
        if ( action.equals( ACTION_DELETE ) )
        {
            if ( target instanceof UserEntity )
            {
                UserEntity user = (UserEntity) target;
                user = (UserEntity) userServicesService.getUserByKey( user.getKey() );
                Window deleteWindow = windowFactory.createDeleteWindow( user );
                adminWindow.addWindow( deleteWindow );
            }
        }
        else if ( action.equals( ACTION_CHANGEPWD ) )
        {
            if ( target instanceof UserEntity )
            {
                UserEntity user = (UserEntity) target;
                user = (UserEntity) userServicesService.getUserByKey( user.getKey() );
                Window pwdWindow = windowFactory.createChangePwdWindow( user );
                adminWindow.addWindow( pwdWindow );
            }
        }
    }

    @Override
    public void itemClick( ItemClickEvent itemClickEvent )
    {
        String userName = itemClickEvent.getItem().getItemProperty( QUALIFIED_NAME ).toString();
        userPanel.showUser( userName );
    }

    @Override
    public void valueChange( Property.ValueChangeEvent valueChangeEvent )
    {
        Set<?> value = (Set<?>) getValue();
        Collection itemIds = getItemIds();
        if ( null == value || value.size() == 0 )
        {

        }
        else
        {
            for ( Object itemId : itemIds )
            {
                CheckBox ch = (CheckBox) getItem( itemId ).getItemProperty( CHECKBOX ).getValue();
                if ( value.contains( itemId ) )
                {
                    ch.setValue( Boolean.TRUE );
                }
                else
                {
                    ch.setValue( Boolean.FALSE );
                }
            }
        }
    }
}
