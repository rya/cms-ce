/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

@Component
@Scope("prototype")
public class TablePanel extends Table
{
    private static final String TYPE = "type";
    private static final String DISPLAY_NAME = "display name";
    private static final String QUALIFIED_NAME = "qualified name";
    private static final String LAST_MODIFIED = "last modified";

    @PostConstruct
    private void init()
    {
        setCaption( "100 matches " );
        setStyleName( "accounts-table" );
        setSelectable( true );
        setWidth( "450px" );
        setHeight( "500px" );

        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty( TYPE, String.class, null );
        container.addContainerProperty( DISPLAY_NAME, String.class, null );
        container.addContainerProperty( QUALIFIED_NAME, String.class, null );
        container.addContainerProperty( LAST_MODIFIED, String.class, null );

        for ( int i = 0; i < 100; i++ )
        {
            Item item = container.addItem(i);
            item.getItemProperty( TYPE ).setValue("ico");
            item.getItemProperty( DISPLAY_NAME ).setValue("user" + i);
            item.getItemProperty( QUALIFIED_NAME ).setValue("AD/usr" + i);
            item.getItemProperty( LAST_MODIFIED ).setValue("2011-04-20");
        }

        setContainerDataSource( container );
    }
}
