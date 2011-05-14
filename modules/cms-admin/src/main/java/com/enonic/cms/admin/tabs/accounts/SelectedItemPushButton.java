package com.enonic.cms.admin.tabs.accounts;

import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

import com.enonic.cms.core.security.IAccordionPresentation;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/13/11
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectedItemPushButton
        extends Panel
        implements MouseEvents.ClickListener
{


    private Boolean selected;
    private IAccordionPresentation selectedEntry;

    public SelectedItemPushButton( IAccordionPresentation entry, Embedded buttonIcon )
    {
        super();
        selected = false;
        this.selectedEntry = entry;
        setWidth( 200, Sizeable.UNITS_PIXELS );
        setHeight( 30, Sizeable.UNITS_PIXELS );
        setScrollable( false );
        HorizontalLayout hl = new HorizontalLayout();
        setContent( hl );
        hl.setSizeFull();
        Label caption = new Label( entry.getDisplayName() );
        addComponent( caption);
        hl.setExpandRatio( caption, 1.0f );
        hl.setComponentAlignment( caption, Alignment.MIDDLE_CENTER );
        buttonIcon.setCaption( null );
        addComponent( buttonIcon );
        buttonIcon.addListener( new MouseEvents.ClickListener(){

            @Override
            public void click( MouseEvents.ClickEvent clickEvent )
            {
                if (getParent() instanceof MultipleSelectionPanel){
                    MultipleSelectionPanel parent = (MultipleSelectionPanel) getParent();
                    parent.removeItem( selectedEntry );
                }
            }
        });
        hl.setComponentAlignment( buttonIcon, Alignment.TOP_RIGHT );
        addListener( (MouseEvents.ClickListener) this );
    }

    @Override
    public void click( MouseEvents.ClickEvent clickEvent )
    {
        if (selected){
            setStyleName( "" );
        }else{
            setStyleName( "selectedPanel" );
        }

        selected = !selected;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof SelectedItemPushButton )
        {
            return selectedEntry.equals( ( (SelectedItemPushButton) obj ).getEntry() );
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return selectedEntry != null ? selectedEntry.hashCode() : 0;
    }

    public IAccordionPresentation getEntry()
    {
        return selectedEntry;
    }

    public boolean isSelected(){
        return selected;
    }
}
