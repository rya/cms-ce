package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.OptionGroup;

import com.enonic.cms.admin.spring.VaadinComponent;

/**
 * This class represents select view menu on accounts tab
 *
 * @author Viktar Fiodarau
 */
@VaadinComponent
public class SelectViewMenu
        extends OptionGroup
{
    private static String OVERVIEW_MENU_ITEM = "Overview";

    private static String BROWSE_MENU_ITEM = "Browse";

    private static String VIEWS_MENU_ITEM = "Views";

    private static String STYLE_NAME = "selectView";

    @PostConstruct
    public void init()
    {
        setHeight( 25, Sizeable.UNITS_PIXELS );
        addItem( OVERVIEW_MENU_ITEM );
        addItem( BROWSE_MENU_ITEM );
        addItem( VIEWS_MENU_ITEM );
        setStyleName( STYLE_NAME );
        setImmediate( true );
        addListener( new Property.ValueChangeListener()
        {
            public void valueChange( Property.ValueChangeEvent event )
            {
                getWindow().showNotification( "Selected city: " + event.getProperty() );

            }
        } );
    }
}
