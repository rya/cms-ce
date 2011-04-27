/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Table;

import com.enonic.cms.core.security.IAccordionPresentation;

@Component
@Scope("vaadin")
public class TablePanel
        extends Table
{
    private static final String TYPE = "type";

    private static final String DISPLAY_NAME = "display name";

    private static final String QUALIFIED_NAME = "qualified name";

    private static final String LAST_MODIFIED = "last modified";

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
            Object id = container.addItem();
            container.getContainerProperty( id, TYPE ).setValue( issue.getTypeName() );

            container.getContainerProperty( id, DISPLAY_NAME ).setValue( issue.getDisplayName() );

            container.getContainerProperty( id, QUALIFIED_NAME ).setValue( issue.getQualifiedName().toString() );

            container.getContainerProperty( id, LAST_MODIFIED ).setValue( issue.getISODate() );
        }
    }
}