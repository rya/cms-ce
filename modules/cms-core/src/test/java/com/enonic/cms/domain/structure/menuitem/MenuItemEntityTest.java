/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.menuitem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;
import org.jdom.JDOMException;

import junit.framework.TestCase;

import com.enonic.cms.framework.util.JDOMUtil;

import static org.junit.Assert.*;


public class MenuItemEntityTest
    extends TestCase
{

    public void testGetRequestParameterValue()
        throws IOException, JDOMException
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setXmlData( null );
        assertNull( menuItem.getRequestParameterValue( "does-not-exist" ) );

        String xmlData = "";
        xmlData += "<data>";
        xmlData += "</data>";
        menuItem.setXmlData( JDOMUtil.parseDocument( xmlData ) );
        assertNull( menuItem.getRequestParameterValue( "does-not-exist" ) );

        xmlData = "";
        xmlData += "<data>";
        xmlData += "<parameters>";
        xmlData += "</parameters>";
        xmlData += "</data>";
        menuItem.setXmlData( JDOMUtil.parseDocument( xmlData ) );
        assertNull( menuItem.getRequestParameterValue( "does-not-exist" ) );

        xmlData = "";
        xmlData += "<data>";
        xmlData += "<parameters>";
        xmlData += "    <parameter name='exists'>value</parameter>";
        xmlData += "</parameters>";
        xmlData += "</data>";
        menuItem.setXmlData( JDOMUtil.parseDocument( xmlData ) );
        assertNull( menuItem.getRequestParameterValue( "does-not-exist" ) );
        assertEquals( "value", menuItem.getRequestParameterValue( "exists" ) );
    }

    public void testAddRequestParameter()
    {
        // override = url
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.addRequestParameter( "p1", "v1", "url" );
        assertEquals( "p1", menuItem.getRequestParameter( "p1" ).getName() );
        assertEquals( "v1", menuItem.getRequestParameter( "p1" ).getValue() );
        assertEquals( true, menuItem.getRequestParameter( "p1" ).isOverridableByRequest() );

        // override = true
        menuItem = new MenuItemEntity();
        menuItem.addRequestParameter( "p1", "v1", "true" );
        assertEquals( "p1", menuItem.getRequestParameter( "p1" ).getName() );
        assertEquals( "v1", menuItem.getRequestParameter( "p1" ).getValue() );
        assertEquals( true, menuItem.getRequestParameter( "p1" ).isOverridableByRequest() );

        // override = false or null
        menuItem = new MenuItemEntity();
        menuItem.addRequestParameter( "p1", "v1", null );
        menuItem.addRequestParameter( "p2", "v2", "false" );
        assertEquals( "v1", menuItem.getRequestParameter( "p1" ).getValue() );
        assertEquals( false, menuItem.getRequestParameter( "p1" ).isOverridableByRequest() );
        assertEquals( "v2", menuItem.getRequestParameter( "p2" ).getValue() );
        assertEquals( false, menuItem.getRequestParameter( "p2" ).isOverridableByRequest() );
    }

    public void testRemoveRequestParameters()
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.addRequestParameter( "p1", "v1", "url" );
        assertEquals( "p1", menuItem.getRequestParameter( "p1" ).getName() );
        assertEquals( "v1", menuItem.getRequestParameter( "p1" ).getValue() );
        assertEquals( true, menuItem.getRequestParameter( "p1" ).isOverridableByRequest() );

        menuItem.removeRequestParameters();

        assertEquals( 0, menuItem.getRequestParameters().size() );
        assertEquals( null, menuItem.getRequestParameter( "p1" ) );
        assertEquals( null, menuItem.getRequestParameterValue( "p1" ) );
    }

    public void testXmlDataIsUpdatedAfterAddRequestParameter()
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.addRequestParameter( "p1", "v1", "url" );
        assertEquals( "p1", menuItem.getRequestParameter( "p1" ).getName() );
        assertEquals( "v1", menuItem.getRequestParameter( "p1" ).getValue() );
        assertEquals( true, menuItem.getRequestParameter( "p1" ).isOverridableByRequest() );

        assertEquals( "v1", JDOMUtil.evaluateSingleXPathValueAsString( "/data/parameters/parameter[ @name = 'p1' ]",
                                                                       menuItem.getXmlDataAsClonedJDomDocument() ) );
    }

    public void testXmlDataIsUpdatedAfterRemovedRequestParameters()
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.addRequestParameter( "p1", "v1", "url" );
        assertEquals( "p1", menuItem.getRequestParameter( "p1" ).getName() );
        assertEquals( "v1", menuItem.getRequestParameter( "p1" ).getValue() );
        assertEquals( true, menuItem.getRequestParameter( "p1" ).isOverridableByRequest() );

        assertEquals( "v1", JDOMUtil.evaluateSingleXPathValueAsString( "/data/parameters/parameter[ @name = 'p1' ]",
                                                                       menuItem.getXmlDataAsClonedJDomDocument() ) );

        menuItem.removeRequestParameters();
        assertEquals( null, JDOMUtil.evaluateSingleXPathValueAsString( "/data/parameters/parameter[ @name = 'p1' ]",
                                                                       menuItem.getXmlDataAsClonedJDomDocument() ) );
    }


    public void testGetChildByName()
    {
        MenuItemEntity parent = createMenuItem( "1", "parent", null );
        MenuItemEntity child2 = createMenuItem( "3", "child2", parent );

        assertEquals( child2, parent.getChildByName( "child2" ) );
    }

    public void testGetMenuItemPath()
    {

        MenuItemEntity child0 = createMenuItem( "0", "1", null );
        MenuItemEntity child1 = createMenuItem( "1", "2", child0 );
        MenuItemEntity child2 = createMenuItem( "2", "3", child1 );
        MenuItemEntity child3 = createMenuItem( "3", "4", child2 );

        List<MenuItemEntity> expectedPath = new ArrayList<MenuItemEntity>();
        expectedPath.add( child0 );
        expectedPath.add( child1 );
        expectedPath.add( child2 );
        expectedPath.add( child3 );

        assertListEquals( expectedPath, child3.getMenuItemPath() );
    }

    public void testGetPathAsString()
    {

        MenuItemEntity child0 = createMenuItem( "0", "1", null );
        MenuItemEntity child1 = createMenuItem( "1", "2", child0 );
        MenuItemEntity child2 = createMenuItem( "2", "3", child1 );
        MenuItemEntity child3 = createMenuItem( "3", "4", child2 );

        assertEquals( "/1", child0.getPathAsString() );
        assertEquals( "/1/2", child1.getPathAsString() );
        assertEquals( "/1/2/3", child2.getPathAsString() );
        assertEquals( "/1/2/3/4", child3.getPathAsString() );
    }

    public void testGetPath()
    {

        MenuItemEntity child0 = createMenuItem( "0", "1", null );
        MenuItemEntity child1 = createMenuItem( "1", "2", child0 );
        MenuItemEntity child2 = createMenuItem( "2", "3", child1 );
        MenuItemEntity child3 = createMenuItem( "3", "4", child2 );

        assertEquals( "/1", child0.getPath().toString() );
        assertEquals( "/1/2", child1.getPath().toString() );
        assertEquals( "/1/2/3", child2.getPath().toString() );
        assertEquals( "/1/2/3/4", child3.getPath().toString() );

    }

    public void testGetLevel()
    {

        MenuItemEntity child0 = createMenuItem( "0", "1", null );
        MenuItemEntity child1 = createMenuItem( "1", "2", child0 );
        MenuItemEntity child2 = createMenuItem( "2", "3", child1 );
        MenuItemEntity child3 = createMenuItem( "3", "4", child2 );

        assertEquals( 0, child0.getLevel() );
        assertEquals( 1, child1.getLevel() );
        assertEquals( 2, child2.getLevel() );
        assertEquals( 3, child3.getLevel() );
    }

    public void testGetParentAtLevel()
    {

        MenuItemEntity child0 = createMenuItem( "0", "1", null );
        MenuItemEntity child1 = createMenuItem( "1", "2", child0 );
        MenuItemEntity child2 = createMenuItem( "2", "3", child1 );
        MenuItemEntity child3 = createMenuItem( "3", "4", child2 );

        assertEquals( child0, child3.getParentAtLevel( 0 ) );
        assertEquals( child1, child3.getParentAtLevel( 1 ) );
        assertEquals( child2, child3.getParentAtLevel( 2 ) );
        try
        {
            assertEquals( child3, child3.getParentAtLevel( 3 ) );
            fail( "Expected IllegalArgumentException" );
        }
        catch ( IllegalArgumentException e )
        {
            // OK
        }
    }

    public void testGetChildren()
    {

        MenuItemEntity child0 = createMenuItem( "0", "1", null );
        MenuItemEntity child1 = createMenuItem( "1", "2", child0 );
        MenuItemEntity child2 = createMenuItem( "2", "3", child0 );
        MenuItemEntity child3 = createMenuItem( "3", "4", child0 );

        List<MenuItemEntity> expectedChildren = new ArrayList<MenuItemEntity>();
        expectedChildren.add( child1 );
        expectedChildren.add( child2 );
        expectedChildren.add( child3 );

        Collection<MenuItemEntity> actualChildren = child0.getChildren();
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new MenuItemEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new MenuItemEntity[actualChildren.size()] ) );
    }

    public void testGetChildrenWithLevel()
    {

        // setup data
        MenuItemEntity root = createMenuItem( "0", "root", null );
        MenuItemEntity mi_1 = createMenuItem( "1", "0.1", root );
        MenuItemEntity mi_1_1 = createMenuItem( "2", "0.1.1", mi_1 );
        MenuItemEntity mi_1_2 = createMenuItem( "3", "0.1.2", mi_1 );
        MenuItemEntity mi_1_2_1 = createMenuItem( "4", "0.1.2.1", mi_1_2 );
        MenuItemEntity mi_2 = createMenuItem( "5", "0.2", root );
        MenuItemEntity mi_2_1 = createMenuItem( "6", "0.2", mi_2 );

        // Test many levels deeper
        List<MenuItemEntity> expectedChildren = new ArrayList<MenuItemEntity>();
        expectedChildren.add( mi_1 );
        expectedChildren.add( mi_1_1 );
        expectedChildren.add( mi_1_2 );
        expectedChildren.add( mi_1_2_1 );
        expectedChildren.add( mi_2 );
        expectedChildren.add( mi_2_1 );

        Collection<MenuItemEntity> actualChildren = root.getDescendants( 100 );
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new MenuItemEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new MenuItemEntity[actualChildren.size()] ) );

        // Test level 1
        expectedChildren = new ArrayList<MenuItemEntity>();
        expectedChildren.add( mi_1 );
        expectedChildren.add( mi_2 );

        actualChildren = root.getDescendants( 1 );
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new MenuItemEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new MenuItemEntity[actualChildren.size()] ) );

        // Test level 0
        expectedChildren = new ArrayList<MenuItemEntity>();

        actualChildren = root.getDescendants( 0 );
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new MenuItemEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new MenuItemEntity[actualChildren.size()] ) );

        // Test level 2
        expectedChildren = new ArrayList<MenuItemEntity>();
        expectedChildren.add( mi_1 );
        expectedChildren.add( mi_1_1 );
        expectedChildren.add( mi_1_2 );
        expectedChildren.add( mi_2 );
        expectedChildren.add( mi_2_1 );

        actualChildren = root.getDescendants( 2 );
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new MenuItemEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new MenuItemEntity[actualChildren.size()] ) );

        // Test level 3
        expectedChildren = new ArrayList<MenuItemEntity>();
        expectedChildren.add( mi_1 );
        expectedChildren.add( mi_1_1 );
        expectedChildren.add( mi_1_2 );
        expectedChildren.add( mi_1_2_1 );
        expectedChildren.add( mi_2 );
        expectedChildren.add( mi_2_1 );

        actualChildren = root.getDescendants( 3 );
        assertNotNull( actualChildren );
        assertArrayEquals( expectedChildren.toArray( new MenuItemEntity[expectedChildren.size()] ),
                           actualChildren.toArray( new MenuItemEntity[actualChildren.size()] ) );
    }

    public void testGetLastUpdatedSectionContentTimestamp()
    {
        MenuItemEntity sectionMenuItem = new MenuItemEntity();
        sectionMenuItem.setType( MenuItemType.SECTION );
        sectionMenuItem.setSection( true );
        sectionMenuItem.setOrderedSection( false );
        SectionContentEntity sectionContent1 = createSectionContent( "1", sectionMenuItem, new Date( 3000 ), 0 );
        SectionContentEntity sectionContent2 = createSectionContent( "2", sectionMenuItem, new Date( 5000 ), 0 );
        SectionContentEntity sectionContent3 = createSectionContent( "3", sectionMenuItem, new Date( 2000 ), 0 );
        Set<SectionContentEntity> secCon = new HashSet<SectionContentEntity>();
        secCon.add( sectionContent1 );
        secCon.add( sectionContent2 );
        secCon.add( sectionContent3 );
        sectionMenuItem.setSectionContent( secCon );

        Date newestTimestamp = sectionMenuItem.getLastUpdatedSectionContentTimestamp();
        assertEquals( new Date( 5000 ), newestTimestamp );
    }

    private SectionContentEntity createSectionContent( String sectionContentKey, MenuItemEntity menuItem, Date timestamp, int order )
    {
        SectionContentEntity entity = new SectionContentEntity();
        entity.setKey( new SectionContentKey( sectionContentKey ) );
        entity.setMenuItem( menuItem );
        entity.setTimestamp( timestamp );
        entity.setOrder( order );
        return entity;
    }


    private MenuItemEntity createMenuItem( String key, String name, MenuItemEntity parent )
    {
        MenuItemEntity mi = new MenuItemEntity();
        mi.setKey( Integer.parseInt( key ) );
        mi.setName( name );
        if ( parent != null )
        {
            mi.setParent( parent );
            parent.addChild( mi );
        }
        return mi;
    }

    private void assertListEquals( List a, List b )
    {
        assertEquals( a.size(), b.size() );
        for ( int i = 0; i < a.size(); i++ )
        {
            Object oa = a.get( i );
            Object ob = b.get( i );
            assertEquals( oa, ob );
        }
    }
}
