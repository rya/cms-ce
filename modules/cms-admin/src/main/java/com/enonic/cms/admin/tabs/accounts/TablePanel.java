/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import java.util.List;

import javax.annotation.PostConstruct;

import com.enonic.cms.admin.spring.VaadinComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Table;

import com.enonic.cms.core.security.IAccordionPresentation;
import com.enonic.cms.core.security.user.User;

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
    private UserPanel userPanel;

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-table" );
        setSelectable( true );
        setSizeFull();

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty( TYPE, String.class, null );
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
            public void handleAction( Action action, Object o, Object o1 )
            {
                //TODO: add handlers for each action
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

            item.getItemProperty(TYPE).setValue(issue.getTypeName());

            item.getItemProperty(DISPLAY_NAME).setValue(issue.getDisplayName());

            item.getItemProperty(QUALIFIED_NAME).setValue(issue.getQualifiedName().toString());

            item.getItemProperty(LAST_MODIFIED).setValue(issue.getISODate());
        }
    }
}