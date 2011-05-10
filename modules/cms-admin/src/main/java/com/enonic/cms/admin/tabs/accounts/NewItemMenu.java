package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.MenuBar;

import com.enonic.cms.admin.spring.VaadinComponent;

/**
 * This class represents new item menu
 *
 * @author Viktar Fiodarau
 */
@VaadinComponent
public class NewItemMenu extends MenuBar
{
    private static String NEW_USER_ITEM = "New (User)";

    private static String NEW_GROUP_ITEM = "New (Group)";

    private static String MENU_TITLE = "New";
    @PostConstruct
    public void init(){
        MenuBar.MenuItem newItem = addItem( MENU_TITLE, null, null );
        newItem.addItem( NEW_USER_ITEM, null, null );
        newItem.addItem( NEW_GROUP_ITEM, null, null );
        setWidth( 100, Sizeable.UNITS_PERCENTAGE );

    }
}
