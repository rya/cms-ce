package com.enonic.cms.admin.tabs.accounts;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

import com.enonic.cms.core.security.user.User;

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

/*
        for ( int i = 0; i < 100; i++ )
        {
            Item item = container.addItem(i);
            item.getItemProperty( TYPE ).setValue("ico");
            item.getItemProperty( DISPLAY_NAME ).setValue("user" + i);
            item.getItemProperty( QUALIFIED_NAME ).setValue("AD/usr" + i);
            item.getItemProperty( LAST_MODIFIED ).setValue("2011-04-20");
        }
*/

        setContainerDataSource( container );
    }

    public void showUsers( List<User> users )
    {
        Container container = getContainerDataSource();
        container.removeAllItems();
        int count = 0;

        for ( User user : users )
        {
            Object id = container.addItem();
            container
                    .getContainerProperty(id, TYPE)
                    .setValue(user.getType().getName());

            container
                    .getContainerProperty(id, DISPLAY_NAME)
                    .setValue(user.getType().getName());

            container
                    .getContainerProperty(id, QUALIFIED_NAME)
                    .setValue(user.getType().getName());

            container
                    .getContainerProperty(id, LAST_MODIFIED)
                    .setValue(user.getType().getName());


//            Item item = container.addItem(count ++);
//            item.getItemProperty( TYPE ).setValue(user.getType().getName());
//            item.getItemProperty( DISPLAY_NAME ).setValue(user.getDisplayName());
//            item.getItemProperty( QUALIFIED_NAME ).setValue(user.getQualifiedName());
//            item.getItemProperty( LAST_MODIFIED ).setValue( DateUtil.formatISODate( user.getTimestamp() ) );
        }

        setContainerDataSource( container );

//        String content = caption + " (" + user.getName() + ")";
//        username.setPropertyDataSource( new ObjectProperty<String>( content, String.class ) );
        //username.setCaption( content );
    }
}
