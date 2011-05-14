package com.enonic.cms.admin.tabs.accounts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.admin.image.EmbeddedImageFactory;
import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.core.security.IAccordionPresentation;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/13/11
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
@VaadinComponent
public class MultipleSelectionPanel
        extends GridLayout
{

    private static Integer MAX_IN_ROW = 2;

    private static Integer MAX_IN_COL = 3;

    private static String CLOSE_ICON_PATH = "images/close-icon.png";

    @Autowired
    private EmbeddedImageFactory imageFactory;

    private Map<IAccordionPresentation, SelectedItemPushButton> selectedItems;

    public MultipleSelectionPanel( )
    {
        super(MAX_IN_COL, MAX_IN_ROW);
    }

    @PostConstruct
    public void init()
    {
        selectedItems = new HashMap<IAccordionPresentation, SelectedItemPushButton>();
        setMargin( true );
        setSpacing( true );
        Button deleteButton = createButton( "Delete" );
    }

    public void addItem( IAccordionPresentation item )
    {
        if ( !selectedItems.containsKey( item ) )
        {
            Embedded closeIcon = imageFactory.createEmbeddedImage( CLOSE_ICON_PATH );
            SelectedItemPushButton newButton = new SelectedItemPushButton( item, closeIcon );
            selectedItems.put( item, newButton );
            if ( selectedItems.size() <= MAX_IN_COL * MAX_IN_ROW )
            {
                addComponent( newButton );
            }
        }
    }

    public void removeItem( IAccordionPresentation item )
    {
        if ( selectedItems.containsKey( item ) )
        {
            selectedItems.remove( item );
            removeAllComponents();
            for ( SelectedItemPushButton button : selectedItems.values() )
            {
                if ( getComponentCount() <= MAX_IN_COL * MAX_IN_ROW )
                {
                    addComponent( button );
                }
            }
        }
    }


    private Button createButton( String caption )
    {
        Button button = new Button( caption );
        button.setWidth( 100, Sizeable.UNITS_PIXELS );
        return button;
    }


}
