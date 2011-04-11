/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.core.structure.access.MenuItemAccessResolver;

import com.enonic.cms.domain.CaseInsensitiveString;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.SiteProperties;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

import static org.easymock.classextension.EasyMock.createNiceMock;

public class SiteXMLCreatorTest
    extends AbstractSiteXmlCreatorTest
{
    private static final Logger LOG = LoggerFactory.getLogger( SiteXMLCreatorTest.class.getName() );

    private SiteXmlCreator siteXmlCreator;


    private SiteEntity site_1;

    private MenuItemEntity mi_1;

    private MenuItemEntity mi_1_1;

    private MenuItemEntity mi_1_1_1;

    private MenuItemEntity mi_1_2;

    private MenuItemEntity mi_1_2_1;

    private MenuItemEntity mi_1_2_2;

    private MenuItemEntity mi_1_2_2_1;

    private MenuItemEntity mi_2;

    private MenuItemEntity mi_2_1;

    private MenuItemEntity oneMenuItemDeepInTheHugeMenu;

    protected void setUp()
        throws Exception
    {
        menuItemAccessResolver = createNiceMock( MenuItemAccessResolver.class );

        standardUser = new UserEntity();
        standardUser.setKey( new UserKey( "KEY" ) );
        standardUser.setDisplayName( "Fullname" );
        standardUser.setName( "uid" );

    }

    public void testCreateLegacyGetMenu()
        throws JDOMException, IOException
    {
        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-Menu-fixture1-result-1.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenu( site, new SiteProperties( null ) ) ) );
    }

    public void xtestCreateLegacyGetMenuByMenuItem()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-Menu-fixture1-result-2.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setActiveMenuItem( mi_1_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenu( site, new SiteProperties( null ) ) ) );
    }

    public void xtestCreateLegacyGetMenuByMenuItemWithOneLevelOnly()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-Menu-fixture1-result-3.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setActiveMenuItem( mi_1_1 );
        siteXmlCreator.setMenuItemLevels( 1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenu( site, new SiteProperties( null ) ) ) );
    }

    public void xtestCreateLegacyGetSubMenu()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-1.xml" );

        SiteEntity site = createSiteFixture1();
        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    public void xtestCreateLegacyGetSubMenu2()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-2.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    public void xtestCreateLegacyGetSubMenu3()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-3.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        siteXmlCreator.setMenuItemInBranch( mi_1_2_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    public void xtestCreateLegacyGetSubMenu4()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-4.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_2_2_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    public void xtestCreateLegacyGetSubMenuWithLevels()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-5.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemLevels( 1 );
        siteXmlCreator.setMenuItemInBranch( mi_1_2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    public void xtestCreateLegacyGetSubMenuWithLevels2()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-6.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        siteXmlCreator.setMenuItemLevels( 2 );
        siteXmlCreator.setMenuItemInBranch( mi_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    public void xtestCreateLegacyGetSubMenuWithLevels3()
        throws JDOMException, IOException
    {

        String expectedXml = getXml( "/com/enonic/cms/core/structure/SiteXMLCreatorTest-SubMenu-fixture1-result-7.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        siteXmlCreator.setMenuItemLevels( 2 );
        siteXmlCreator.setMenuItemInBranch( mi_1_2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetSubMenu( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1 +
     *    11 +
     *       111 + menuItemInBranch
     *    12 +
     *       121 +
     *       122 +
     *          1221 +
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchSimple()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-1.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        siteXmlCreator.setMenuItemInBranch( mi_1_1_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1 +
     *    11 +
     *       111 + menuItemInBranch
     *    12 +
     *       121 +
     *       122 +
     *          1221 +
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchSimple2()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-1b.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_2_1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1 +
     *    11 +
     *       111  menuItemInBranch
     *    12 +
     *       121
     *       122
     *          1221
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithMaxLevel()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-2.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        siteXmlCreator.setMenuItemInBranch( mi_1_1_1 );

        siteXmlCreator.setMenuItemLevels( 2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1
     *    11 +
     *       111 + menuItemInBranch
     *    12 +
     *       121 +
     *       122 +
     *          1221 +
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithStartLevel()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-3.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_1_1 );
        siteXmlCreator.setBranchStartLevel( 1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1
     *    11
     *       111 + menuItemInBranch
     *    12
     *       121
     *       122
     *          1221
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithStartLevel2()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-4.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_1_1 );
        siteXmlCreator.setBranchStartLevel( 2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1
     *    11
     *       111
     *    12
     *       121 + menuItemInBranch
     *       122 +
     *          1221 +
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithStartLevel3()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-5.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_2_1 );
        siteXmlCreator.setBranchStartLevel( 2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1
     *    11
     *       111
     *    12
     *       121
     *       122 menuItemInBranch
     *          1221 +
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithStartLevel4()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-6.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_2_2 );
        siteXmlCreator.setBranchStartLevel( 3 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1
     *    11
     *       111
     *    12
     *       121 +
     *       122 +
     *          1221 menuItemInBranch
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithStartLevelAndMaxLevel()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-7.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_2_2_1 );
        siteXmlCreator.setBranchStartLevel( 2 );

        siteXmlCreator.setMenuItemLevels( 1 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    /**
     * Expected marked with +:<br/>
     * <pre>
     * 1
     *    11 +
     *       111 +
     *    12 +
     *       121 +
     *       122 +
     *          1221 menuItemInBranch
     * 2
     *    21
     * </pre>
     */
    public void xtestCreateLegacyGetMenuBranchWithStartLevelAndMaxLevel2()
        throws JDOMException, IOException
    {

        String expectedXml = getXml(
                "/com/enonic/cms/core/structure/SiteXMLCreatorTest-MenuBranch-fixture1-result-8.xml" );

        SiteEntity site = createSiteFixture1();

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( mi_1_2_2_1 );
        siteXmlCreator.setBranchStartLevel( 1 );
        siteXmlCreator.setMenuItemLevels( 2 );

        assertEquals( expectedXml, getFormattedXmlString( siteXmlCreator.createLegacyGetMenuBranch( site ) ) );
    }

    public void xtestSpeedCreateLegacyGetMenu()
    {

        SiteEntity site = createHugeSiteFixture( 5, 7, 4 );

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( oneMenuItemDeepInTheHugeMenu );
        LOG.info( "Created huge site with {} menu items", keyCounter);

        long start = System.currentTimeMillis();
        siteXmlCreator.createLegacyGetMenu( site, new SiteProperties( null ) );
        long end = System.currentTimeMillis();

        LOG.info( "SiteXmlCreator.createLegacyGetMenu took {} ms", end - start );
    }

    public void xtestSpeedCreateLegacyGetMenu2()
    {

        SiteEntity site = createHugeSiteFixture( 5, 4, 4 );

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( oneMenuItemDeepInTheHugeMenu );
        LOG.info( "Created slightly huge site with {} menu items", keyCounter );

        long start = System.currentTimeMillis();
        siteXmlCreator.createLegacyGetMenu( site, new SiteProperties( null ) );
        long end = System.currentTimeMillis();
        LOG.info( "SiteXmlCreator.createLegacyGetMenu took {} ms", end - start );

    }

    public void xtestSpeedCreateLegacyGetSubMenu()
    {

        SiteEntity site = createHugeSiteFixture( 5, 7, 4 );

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );

        siteXmlCreator.setMenuItemInBranch( oneMenuItemDeepInTheHugeMenu );

        siteXmlCreator.setActiveMenuItem( oneMenuItemDeepInTheHugeMenu );

        LOG.info( "Created huge site with " + keyCounter + " menu items" );

        long start = System.currentTimeMillis();
        siteXmlCreator.createLegacyGetSubMenu( site );
        long end = System.currentTimeMillis();
        LOG.info( "SiteXmlCreator.createLegacyGetSubMenu took " + ( end - start ) + " ms" );

    }

    public void xtestSpeedCreateLegacyGetMenuBranch()
    {

        SiteEntity site = createHugeSiteFixture( 5, 7, 4 );

        siteXmlCreator = new SiteXmlCreator( menuItemAccessResolver );
        siteXmlCreator.setMenuItemInBranch( oneMenuItemDeepInTheHugeMenu );
        siteXmlCreator.setActiveMenuItem( oneMenuItemDeepInTheHugeMenu );
        LOG.info( "Created huge site with " + keyCounter + " menu items" );

        long start = System.currentTimeMillis();
        siteXmlCreator.createLegacyGetMenuBranch( site );
        long end = System.currentTimeMillis();
        LOG.info( "SiteXmlCreator.createLegacyGetMenuBranch took " + ( end - start ) + " ms" );

    }

    /**
     * Creates a site with the following menu items:<br/>
     * <pre>
     * 1
     *    11
     *       111
     *    12
     *       121
     *       122
     *          1221
     * 2
     *    21
     * </pre>
     */
    private SiteEntity createSiteFixture1()
    {

        site_1 = new SiteEntity();
        site_1.setKey( 1 );
        site_1.setLanguage( createLanguage( "1", "no", "Norwegian" ) );

        mi_1 = createMenuItem( "1", "mi 1", null, site_1 );

        mi_1_1 = createMenuItem( "11", "mi 1.1", mi_1, site_1 );
        mi_1_1_1 = createMenuItem( "111", "mi 1.1.1", mi_1_1, site_1 );

        mi_1_2 = createMenuItem( "12", "mi 1.2", mi_1, site_1 );
        mi_1_2_1 = createMenuItem( "121", "mi 1.2.1", mi_1_2, site_1 );
        mi_1_2_2 = createMenuItem( "122", "mi 1.2.2", mi_1_2, site_1 );
        mi_1_2_2_1 = createMenuItem( "1221", "mi 1.2.2.1", mi_1_2_2, site_1 );

        mi_2 = createMenuItem( "2", "mi 2", null, site_1 );
        mi_2_1 = createMenuItem( "21", "mi 2.1", mi_2, site_1 );

        Map<CaseInsensitiveString, MenuItemEntity> topMenuItems = new LinkedHashMap<CaseInsensitiveString, MenuItemEntity>();
        topMenuItems.put( new CaseInsensitiveString( mi_1.getName() ), mi_1 );
        topMenuItems.put( new CaseInsensitiveString( mi_2.getName() ), mi_2 );
        site_1.setTopMenuItems( topMenuItems );

        return site_1;
    }


    private int keyCounter = 0;

    private SiteEntity createHugeSiteFixture( int firstRows, int rows, int depth )
    {

        SiteEntity site = new SiteEntity();
        site.setKey( 1 );
        site.setLanguage( createLanguage( "1", "no", "Norwegian" ) );

        Map<CaseInsensitiveString, MenuItemEntity> topMenuItems = new LinkedHashMap<CaseInsensitiveString, MenuItemEntity>();
        for ( int i = 1; i <= firstRows; i++ )
        {
            String key = String.valueOf( ++keyCounter );
            String name = "mi " + key;
            MenuItemEntity mi = createMenuItem( key, name, null, site, true );
            topMenuItems.put( new CaseInsensitiveString( mi.getName() ), mi );

            addMenuItems( site, mi, rows, depth );
        }
        site.setTopMenuItems( topMenuItems );
        return site;
    }

    private void addMenuItems( SiteEntity site, MenuItemEntity parent, int totalChildren, int levels )
    {

        if ( levels <= 0 )
        {
            return;
        }

        for ( int i = 1; i <= totalChildren; i++ )
        {
            String key = String.valueOf( ++keyCounter );

            String name = parent.getName() + "-" + key;
            MenuItemEntity mi = createMenuItem( key, name, parent, site );
            addMenuItems( site, mi, totalChildren, levels - 1 );
            if ( i == 1 && levels == 1 )
            {
                oneMenuItemDeepInTheHugeMenu = mi;
            }
        }
    }

}
