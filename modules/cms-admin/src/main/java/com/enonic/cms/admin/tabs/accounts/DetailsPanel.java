package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.admin.spring.VaadinComponent;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/14/11
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
@VaadinComponent
public class DetailsPanel
        extends HorizontalLayout
{

    @Autowired
    private UserPanel userPanel;

    @Autowired
    private MultipleSelectionPanel multipleSelectionPanel;

    private VerticalLayout buttonLayout;

    private Label infoLabel;

    @PostConstruct
    public void init()
    {
        setSizeFull();
        addComponent( userPanel );
        buttonLayout = createButtonLayout();
    }

    public void switchToSingleMode()
    {
        removeAllComponents();
        addComponent( userPanel );
    }

    public void switchToMultipleMode()
    {
        removeAllComponents();
        addComponent( multipleSelectionPanel );
        setExpandRatio( multipleSelectionPanel, 2.0f );
        addComponent( buttonLayout );
        setExpandRatio( buttonLayout, 0.5f );
    }

    private VerticalLayout createButtonLayout()
    {
        VerticalLayout bl = new VerticalLayout();
        bl.setSizeFull();
        bl.setSpacing( true );
        bl.setMargin( true );
        Button deleteButton = createButton( "Delete" );
        deleteButton.addListener( new Button.ClickListener(){

            @Override
            public void buttonClick( Button.ClickEvent clickEvent )
            {
                multipleSelectionPanel.removeSelectedItems();
            }
        });
        bl.addComponent( deleteButton );
        return bl;
    }

    private Button createButton( String caption )
    {
        Button button = new Button( caption );
        button.setWidth( 100, Sizeable.UNITS_PIXELS );
        return button;
    }
}
