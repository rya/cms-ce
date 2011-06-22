/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contentdata.custom;

import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import static org.junit.Assert.*;


public class FormItemEntriesGroupTest
{
    @Test
    public void testOrder()
    {
        FormItemsGroup formItemsGroup = new FormItemsGroup( "group", createFormItems() );

        assertEquals( 3, formItemsGroup.getGroupIndexes().size());
        assertOrder( formItemsGroup );
    }

    @Test
    public void testGetFormEntryConfigName()
    {
        FormItemsGroup formItemsGroup = new FormItemsGroup( "group", createFormItems() );

        String configName = formItemsGroup.getFormEntryConfigName( "group[1].item" );

        assertEquals( "item", configName );
    }

    @Test
    public void testUnMatchingName()
    {

        ExtendedMap formItems = new ExtendedMap();

        formItems.put( "Group[1].item", "1" );
        formItems.put( "group.item", "2" );
        formItems.put( "group.item", "3" );
        formItems.put( "group[1].item", "4" );

        FormItemsGroup groups = new FormItemsGroup( "group", formItems );

        assertEquals( 1, groups.getGroupIndexes().size() );
    }

    private void assertOrder( FormItemsGroup formItemsGroup )
    {
        int i = 1;
        for ( Integer groupIndex : formItemsGroup.getGroupIndexes() )
        {
            assertEquals( groupIndex, new Integer( i++ ) );
        }
    }

    private ExtendedMap createFormItems()
    {
        ExtendedMap formItems = new ExtendedMap();

        formItems.put( "group[2].item-1", "4" );
        formItems.put( "group[1].item-1", "1" );
        formItems.put( "group[1].item-2", "2" );
        formItems.put( "group[2].item-2", "3" );
        formItems.put( "group[3].item-2", "6" );
        formItems.put( "group[3].item-1", "5" );
        return formItems;
    }

}
