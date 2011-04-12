/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Jun 16, 2009
 */
public class FormItemEntriesGroupTest
{
    @Test
    public void testOrder()
    {
        FormItemsGroup formItemsGroup = new FormItemsGroup( "group", createFormItems() );

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

        formItems.put( "group[2].item1", "4" );
        formItems.put( "group[1].item1", "1" );
        formItems.put( "group[1].item2", "2" );
        formItems.put( "group[2].item2", "3" );
        formItems.put( "group[3].item2", "6" );
        formItems.put( "group[3].item1", "5" );
        return formItems;
    }

}
