/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.structure.menuitem;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.structure.menuitem.*;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;

import com.enonic.cms.core.security.SecurityHolder;

import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.structure.menuitem.ApproveSectionContentCommand;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessException;
import com.enonic.cms.core.structure.menuitem.RemoveContentFromSectionCommand;
import com.enonic.cms.core.structure.menuitem.UnapproveSectionContentCommand;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class MenuItemServiceImplTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private MenuItemService menuItemService;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Before
    public void setUp()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        SecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.flushAndClearHibernateSesssion();

        // Create an article conent type that will be used in the section:
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "article", "heading" );
        ctyconf.startBlock( "intro" );
        ctyconf.addInput( "heading", "text", "contentdata/intro/heading", "heading", true );
        ctyconf.addInput( "teaser", "text", "contentdata/intro/teaser", "teaser" );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save( factory.createContentType( "MenuItem", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        // Create users that have all and no rights to work with the sections.
        GroupEntity userGroup1 = factory.createGroupInUserstore( "aru_group", GroupType.USERSTORE_GROUP, "testuserstore" );
        GroupEntity userGroup2 = factory.createGroupInUserstore( "nru_group", GroupType.USERSTORE_GROUP, "testuserstore" );
        GroupEntity userGroup3 = factory.createGroupInUserstore( "publish_group", GroupType.USERSTORE_GROUP, "testuserstore" );
        fixture.save( userGroup1, userGroup2, userGroup3 );

        fixture.save( factory.createUser( "aru", "All Rights User", UserType.NORMAL, "testuserstore", userGroup1 ) );
        fixture.save( factory.createUser( "nru", "No Rights User", UserType.NORMAL, "testuserstore", userGroup2 ) );
        fixture.save( factory.createUser( "publish", "User with publish only", UserType.NORMAL, "testuserstore", userGroup3 ) );

        fixture.flushAndClearHibernateSesssion();

        // Create a unit and a category in the archive to store the articles in, including access rights on the category.
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createUnit( "Archive" ) );
        fixture.save( factory.createCategory( "Articles", "article", "Archive", "aru", "aru" ) );

        fixture.save( factory.createCategoryAccessForUser( "Articles", "aru", "read, admin_browse, create, delete" ) );
        fixture.save( factory.createCategoryAccessForUser( "Articles", "nru", "read" ) );

        // Create a site and a section page for testing working with sections.
        fixture.save( factory.createSite( "The Newspaper", new Date(), null, "en" ) );

        fixture.save(
            factory.createPageMenuItem( "Hello World!", 10, "This is the top level menu item", "Hello World!", "The Newspaper", "aru",
                                        "aru", false, null, "en", null, null, null, false, null ) );
//        fixture.save(
//            factory.createMenuItemAccess( "Hello World!", 10, fixture.findUserByName( "aru" ).getUserGroup(), "read" ) );
        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void testRemoveContentFromSection()
    {
        // Data for test:
        MenuItemEntity menuItemPolitics =
            factory.createSectionMenuItem( "News", 20, "Unordered", "News", "The Newspaper", "aru", "aru", "en", "Hello World!", 10, false,
                                           null, false, null );
        fixture.save( menuItemPolitics );

        MenuItemAccessEntity aruMenuAR = factory.createMenuItemAccess( "News", 20, fixture.findGroupByName( "aru_group" ),
                                                                       "read, create, update, delete, add, publish" );
        MenuItemAccessEntity nruMenuAR = factory.createMenuItemAccess( "News", 20, fixture.findGroupByName( "nru_group" ), "read" );
        fixture.save( aruMenuAR, nruMenuAR );

        ContentEntity contentPolitics1 = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        ContentEntity contentPolitics2 = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        fixture.save( contentPolitics1, contentPolitics2 );
        ContentKey politicsKey1 = contentPolitics1.getKey();
        ContentKey politicsKey2 = contentPolitics2.getKey();

        SectionContentEntity sectionContent1 = factory.createContentSection( "News", 20, politicsKey1, true, 1 );
        SectionContentEntity sectionContent2 = factory.createContentSection( "News", 20, politicsKey2, true, 2 );
        fixture.save( sectionContent1, sectionContent2 );
        fixture.flushAndClearHibernateSesssion();

        // Assert that all data is set up correctly
        MenuItemEntity section = fixture.findMenuItemByName( "News", 20 );
        assertEquals( 2, section.getSectionContents().size() );

        // Execute remove command
        RemoveContentFromSectionCommand allRightsRemoveCommand = new RemoveContentFromSectionCommand();
        allRightsRemoveCommand.setRemover( fixture.findUserByName( "aru" ).getKey() );
        allRightsRemoveCommand.setSection( fixture.findMenuItemByName( "News", 20 ).getMenuItemKey() );
        allRightsRemoveCommand.addContentToRemove( contentPolitics1.getKey() );
        menuItemService.removeContentFromSection( allRightsRemoveCommand );

        fixture.flushAndClearHibernateSesssion();

        // Assert result (Only 1 content left in section, and correct content)
        section = fixture.findMenuItemByName( "News", 20 );
        Set<SectionContentEntity> testResultAfterRemovingOneContent = section.getSectionContents();
        assertEquals( 1, testResultAfterRemovingOneContent.size() );
        SectionContentEntity testDetailResult = testResultAfterRemovingOneContent.iterator().next();
        assertEquals( politicsKey2, testDetailResult.getContent().getKey() );

        // Verify that a user with only list rights can not remove from the section.
        RemoveContentFromSectionCommand noRightsRemoveCommand = new RemoveContentFromSectionCommand();
        noRightsRemoveCommand.setRemover( fixture.findUserByName( "nru" ).getKey() );
        noRightsRemoveCommand.setSection( fixture.findMenuItemByName( "News", 20 ).getMenuItemKey() );
        noRightsRemoveCommand.addContentToRemove( contentPolitics2.getKey() );
        try
        {
            menuItemService.removeContentFromSection( noRightsRemoveCommand );
            fail( "Not in the catch block.  The removeContentFromSection method should throw a MenuItemAccessException in this case." );
        }
        catch ( MenuItemAccessException e )
        {
            // All ok!
        }

        fixture.flushAndClearHibernateSesssion();

        // Assert result
        section = fixture.findMenuItemByName( "News", 20 );
        Set<SectionContentEntity> testResultAfterFailingToRemoveOneContent = section.getSectionContents();
        assertEquals( 1, testResultAfterFailingToRemoveOneContent.size() );
        SectionContentEntity testDetailResult2 = testResultAfterFailingToRemoveOneContent.iterator().next();
        assertEquals( politicsKey2, testDetailResult2.getContent().getKey() );
    }

    @Test
    public void testReorderingContentInASection()
    {
        MenuItemEntity menuItemArchive =
            factory.createSectionMenuItem( "Sports", 30, "Ordered", "Sports", "The Newspaper", "aru", "aru", "en", "Hello World!", 10, true,
                                           new Date(), false, null );
        fixture.save( menuItemArchive );

        MenuItemAccessEntity aruMenuAR = factory.createMenuItemAccess( "Sports", 30, fixture.findGroupByName( "aru_group" ),
                                                                       "read, create, update, delete, add, publish" );
        MenuItemAccessEntity nruMenuAR = factory.createMenuItemAccess( "Sports", 30, fixture.findGroupByName( "nru_group" ), "read" );
        fixture.save( aruMenuAR, nruMenuAR );

        ContentEntity sportsContentChess = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        ContentEntity sportsContentFootball = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        ContentEntity sportsContentGolf = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        ContentEntity sportsContentIcehockey = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        fixture.save( sportsContentChess, sportsContentFootball, sportsContentGolf, sportsContentIcehockey );

        int chessContentKey = sportsContentChess.getKey().toInt();
        int footballContentKey = sportsContentFootball.getKey().toInt();
        int golfContentKey = sportsContentGolf.getKey().toInt();
        int icehockeyContentKey = sportsContentIcehockey.getKey().toInt();

        SectionContentEntity secCntChess = factory.createContentSection( "Sports", 30, sportsContentChess.getKey(), true, 1 );
        SectionContentEntity secCntFootball = factory.createContentSection( "Sports", 30, sportsContentFootball.getKey(), true, 2 );
        SectionContentEntity secCntGolf = factory.createContentSection( "Sports", 30, sportsContentGolf.getKey(), true, 3 );
        SectionContentEntity secCntIcehockey = factory.createContentSection( "Sports", 30, sportsContentIcehockey.getKey(), true, 4 );
        fixture.save( secCntChess, secCntFootball, secCntGolf, secCntIcehockey );
        fixture.flushAndClearHibernateSesssion();

        // Assert that all data is set up correctly
        MenuItemEntity testSection = fixture.findMenuItemByName( "Sports", 30 );
        Set<SectionContentEntity> storedSectionContents = testSection.getSectionContents();
        assertEquals( 4, storedSectionContents.size() );
        Iterator<SectionContentEntity> secCntIterator = storedSectionContents.iterator();
        boolean chessContentChecked = false, footballContentChecked = false, golfContentChecked = false, icehockeyContentChecked = false;
        while ( secCntIterator.hasNext() )
        {
            SectionContentEntity secCntLoopHolder = secCntIterator.next();
            int loopHolderKey = secCntLoopHolder.getContent().getKey().toInt();
            if ( loopHolderKey == chessContentKey )
            {
                assertEquals( 1, secCntLoopHolder.getOrder() );
                chessContentChecked = true;
            }
            else if ( loopHolderKey == footballContentKey )
            {
                assertEquals( 2, secCntLoopHolder.getOrder() );
                footballContentChecked = true;
            }
            else if ( loopHolderKey == golfContentKey )
            {
                assertEquals( 3, secCntLoopHolder.getOrder() );
                golfContentChecked = true;
            }
            else if ( loopHolderKey == icehockeyContentKey )
            {
                assertEquals( 4, secCntLoopHolder.getOrder() );
                icehockeyContentChecked = true;
            }
        }
        assertTrue( chessContentChecked && footballContentChecked && golfContentChecked && icehockeyContentChecked );

        // Execute reorder command
        ApproveSectionContentCommand approveCommand = new ApproveSectionContentCommand();
        approveCommand.setUpdater( fixture.findUserByName( "aru" ).getKey() );
        approveCommand.setSection( fixture.findMenuItemByName( "Sports", 30 ).getMenuItemKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentFootball.getKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentChess.getKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentIcehockey.getKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentGolf.getKey() );
        menuItemService.approveSectionContent( approveCommand );

        fixture.flushAndClearHibernateSesssion();

        // Assert that all data is updated correctly
        verifyFinalReorderingResult( chessContentKey, footballContentKey, golfContentKey, icehockeyContentKey );

        // Execute reorder command without enough rights.
        approveCommand = new ApproveSectionContentCommand();
        approveCommand.setUpdater( fixture.findUserByName( "nru" ).getKey() );
        approveCommand.setSection( fixture.findMenuItemByName( "Sports", 30 ).getMenuItemKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentIcehockey.getKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentGolf.getKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentChess.getKey() );
        approveCommand.addApprovedContentToUpdate( sportsContentFootball.getKey() );
        try
        {
            menuItemService.approveSectionContent( approveCommand );
            fail( "Not in the catch block.  Updating the order of the content should not be allowed for this user." );
        }
        catch ( MenuItemAccessException e )
        {
            // All Ok
        }

        // Assert that data has not been changed:
        verifyFinalReorderingResult( chessContentKey, footballContentKey, golfContentKey, icehockeyContentKey );
    }

    @Test
    public void testApprovingAndUnapprovingContentInSection()
    {
        // Data for test:
        MenuItemEntity menuItemPolitics =
            factory.createSectionMenuItem( "Opinion", 40, "Unordered", "Opinion", "The Newspaper", "aru", "aru", "en", "Hello World!", 10,
                                           false, null, false, null );
        fixture.save( menuItemPolitics );

        MenuItemAccessEntity allRightsUserAccess = factory.createMenuItemAccess( "Opinion", 40, fixture.findGroupByName( "aru_group" ),
                                                                                 "read, create, update, delete, add, publish" );
        MenuItemAccessEntity readOnlyUserAccess =
            factory.createMenuItemAccess( "Opinion", 40, fixture.findGroupByName( "nru_group" ), "read" );

        MenuItemAccessEntity publishOnlyUserAccess =
            factory.createMenuItemAccess( "Opinion", 40, fixture.findGroupByName( "publish_group" ), "read, publish" );

        fixture.save( allRightsUserAccess, readOnlyUserAccess, publishOnlyUserAccess );

        ContentEntity contentOpinionPro = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        ContentEntity contentOpinionAgainst = factory.createContent( "Articles", "en", "aru", "0", new Date() );
        fixture.save( contentOpinionPro, contentOpinionAgainst );
        ContentKey proKey = contentOpinionPro.getKey();
        ContentKey againstKey = contentOpinionAgainst.getKey();

        SectionContentEntity sectionContentPro = factory.createContentSection( "Opinion", 40, proKey, true, 1 );
        SectionContentEntity sectionContentAgainst = factory.createContentSection( "Opinion", 40, againstKey, true, 2 );
        fixture.save( sectionContentPro, sectionContentAgainst );
        fixture.flushAndClearHibernateSesssion();

        // Assert that all data is set up correctly
        MenuItemEntity section = fixture.findMenuItemByName( "Opinion", 40 );
        assertEquals( 2, section.getSectionContents().size() );

        final UserEntity publishOnlyUser = fixture.findUserByName( "publish" );

        // Unapprove content
        UnapproveSectionContentCommand unapproveCommand = new UnapproveSectionContentCommand();
        unapproveCommand.setUpdater( publishOnlyUser.getKey() );
        unapproveCommand.setSection( fixture.findMenuItemByName( "Opinion", 40 ).getMenuItemKey() );
        unapproveCommand.addUnapprovedContentToUpdate( contentOpinionAgainst.getKey() );
        menuItemService.unapproveSectionContent( unapproveCommand );
        fixture.flushAndClearHibernateSesssion();

        // Assert that all data is updated correctly
        MenuItemEntity testSection = fixture.findMenuItemByName( "Opinion", 40 );
        Set<SectionContentEntity> storedSectionContents = testSection.getSectionContents();
        assertEquals( 2, storedSectionContents.size() );
        Iterator<SectionContentEntity> secCntIterator = storedSectionContents.iterator();
        boolean proContentChecked = false, againstContentChecked = false;
        while ( secCntIterator.hasNext() )
        {
            SectionContentEntity secCntLoopHolder = secCntIterator.next();
            int loopHolderKey = secCntLoopHolder.getContent().getKey().toInt();
            if ( loopHolderKey == contentOpinionAgainst.getKey().toInt() )
            {
                assertEquals( false, secCntLoopHolder.isApproved() );
                againstContentChecked = true;
            }
            else if ( loopHolderKey == contentOpinionPro.getKey().toInt() )
            {
                assertEquals( true, secCntLoopHolder.isApproved() );
                proContentChecked = true;
            }
        }
        assertTrue( proContentChecked && againstContentChecked );

        // Switch approved and unapproved content:
        unapproveCommand = new UnapproveSectionContentCommand();
        unapproveCommand.setUpdater( publishOnlyUser.getKey() );
        unapproveCommand.setSection( fixture.findMenuItemByName( "Opinion", 40 ).getMenuItemKey() );
        unapproveCommand.addUnapprovedContentToUpdate( contentOpinionPro.getKey() );
        menuItemService.unapproveSectionContent( unapproveCommand );

        ApproveSectionContentCommand approveCommand = new ApproveSectionContentCommand();
        approveCommand.setUpdater( publishOnlyUser.getKey() );
        approveCommand.setSection( fixture.findMenuItemByName( "Opinion", 40 ).getMenuItemKey() );
        approveCommand.addApprovedContentToUpdate( contentOpinionAgainst.getKey() );
        menuItemService.approveSectionContent( approveCommand );

        fixture.flushAndClearHibernateSesssion();

        // Assert that all data is updated correctly
        VerifyFinalResultOfApproveUnapproveTest( contentOpinionPro.getKey().toInt(), contentOpinionAgainst.getKey().toInt() );

        // Try switching the approved unapproved setting, with a user that does not have the necessary rights to do so:
        unapproveCommand = new UnapproveSectionContentCommand();
        unapproveCommand.setUpdater( fixture.findUserByName( "nru" ).getKey() );
        unapproveCommand.setSection( fixture.findMenuItemByName( "Opinion", 40 ).getMenuItemKey() );
        unapproveCommand.addUnapprovedContentToUpdate( contentOpinionAgainst.getKey() );
        try
        {
            menuItemService.unapproveSectionContent( unapproveCommand );
            fail( "Not in the catch block.  Unapproving content should not be allowed for this user." );
        }
        catch ( MenuItemAccessException e )
        {
            // All OK
        }

        approveCommand = new ApproveSectionContentCommand();
        approveCommand.setUpdater( fixture.findUserByName( "nru" ).getKey() );
        approveCommand.setSection( fixture.findMenuItemByName( "Opinion", 40 ).getMenuItemKey() );
        approveCommand.addApprovedContentToUpdate( contentOpinionPro.getKey() );
        try
        {
            menuItemService.approveSectionContent( approveCommand );
            fail( "Not in the catch block.  Approving content should not be allowed for this user." );
        }
        catch ( MenuItemAccessException e )
        {
            // All OK
        }

        // That nothing has changed
        VerifyFinalResultOfApproveUnapproveTest( contentOpinionPro.getKey().toInt(), contentOpinionAgainst.getKey().toInt() );
    }

    @Test (expected = MenuItemAccessException.class)
      public void testApproveContentAccessRight_noPublish()
      {
          // Data for test:
          MenuItemEntity menuItemPolitics =
              factory.createSectionMenuItem( "Opinion", 40, "Unordered", "Opinion", "The Newspaper", "aru", "aru", "en", "Hello World!", 10,
                                             false, null, false, null );
          fixture.save( menuItemPolitics );

          MenuItemAccessEntity allRightsUserAccess = factory.createMenuItemAccess( "Opinion", 40, fixture.findGroupByName( "aru_group" ),
                                                                                   "read, create, update, delete, add, publish" );
          MenuItemAccessEntity readOnlyUserAccess =
              factory.createMenuItemAccess( "Opinion", 40, fixture.findGroupByName( "nru_group" ), "read, update, delete, add" );

          fixture.save( allRightsUserAccess, readOnlyUserAccess );

          ContentEntity contentOpinionPro = factory.createContent( "Articles", "en", "aru", "0", new Date() );
          ContentEntity contentOpinionAgainst = factory.createContent( "Articles", "en", "aru", "0", new Date() );
          fixture.save( contentOpinionPro, contentOpinionAgainst );
          ContentKey proKey = contentOpinionPro.getKey();
          ContentKey againstKey = contentOpinionAgainst.getKey();

          SectionContentEntity sectionContentPro = factory.createContentSection( "Opinion", 40, proKey, true, 1 );
          SectionContentEntity sectionContentAgainst = factory.createContentSection( "Opinion", 40, againstKey, true, 2 );
          fixture.save( sectionContentPro, sectionContentAgainst );
          fixture.flushAndClearHibernateSesssion();

          // Assert that all data is set up correctly
          MenuItemEntity section = fixture.findMenuItemByName( "Opinion", 40 );
          assertEquals( 2, section.getSectionContents().size() );

          final UserEntity noPublishRightUser = fixture.findUserByName( "nru" );

          // Try unapprove content
          UnapproveSectionContentCommand unapproveCommand = new UnapproveSectionContentCommand();
          unapproveCommand.setUpdater( noPublishRightUser.getKey() );
          unapproveCommand.setSection( fixture.findMenuItemByName( "Opinion", 40 ).getMenuItemKey() );
          unapproveCommand.addUnapprovedContentToUpdate( contentOpinionAgainst.getKey() );
          menuItemService.unapproveSectionContent( unapproveCommand );
          fixture.flushAndClearHibernateSesssion();

      }

    @Test
    public void testGetPageKeyByPath()
    {
        String keys;

        MenuItemEntity menuItem1 =
            factory.createSectionMenuItem( "name1", 40, "menuName1", "displayName1", "The Newspaper",
                                           "aru", "aru", "en", "Hello World!", 10,
                                           false, null, false, null );
        fixture.save( menuItem1 );

        MenuItemEntity menuItem2 =
            factory.createSectionMenuItem( "name2", 40, "menuName2", "displayName2", "The Newspaper",
                                           "aru", "aru", "en", "Hello World!", 10,
                                           false, null, false, null );
        fixture.save( menuItem2 );


        MenuItemEntity section = fixture.findMenuItemByName( "Hello World!", 10 );

        Collection<MenuItemEntity> children = section.getChildren();
        assertEquals( 2, children.size() );



        keys = menuItemService.getPageKeyByPath( section, "/" );
        assertEquals( keys, "" + section.getKey() );

        keys = menuItemService.getPageKeyByPath( section, "/Hello World!" );
        assertEquals( keys, "" + section.getKey() );

        keys = menuItemService.getPageKeyByPath( section, "/Hello World!/name1" );
        assertEquals( keys, "" + menuItem1.getKey());

        keys = menuItemService.getPageKeyByPath( section, "/Hello World!/name2" );
        assertEquals( keys, "" + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( section, "/Hello World!/name2/nope" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "/../" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "/nope" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "/Hello World!/" );
        assertEquals( keys, "" + menuItem1.getKey() + "," + menuItem2.getKey());



        keys = menuItemService.getPageKeyByPath( section, "." );
        assertEquals( keys, "" + section.getKey());

        keys = menuItemService.getPageKeyByPath( section, "./" );
        assertEquals( keys, "" + menuItem1.getKey() + "," + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( section, "./name2" );
        assertEquals( keys, "" + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( section, "./../Hello World!/name2" );
        assertEquals( keys, "" + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( section, "./../../Hello World!/name2" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, ".." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "../." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "../" );
        assertEquals( keys, "" + section.getKey());

        keys = menuItemService.getPageKeyByPath( section, "../nope" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "../../" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "../.." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "../../.." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "nope" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( section, "./nope" );
        assertEquals( keys, "");




        keys = menuItemService.getPageKeyByPath( menuItem1, "." );
        assertEquals( keys, "" + menuItem1.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, ".." );
        assertEquals( keys, "" + section.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./.." );
        assertEquals( keys, "" + section.getKey());


        keys = menuItemService.getPageKeyByPath( menuItem1, "../" );
        assertEquals( keys, "" + menuItem1.getKey() + "," + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./../" );
        assertEquals( keys, "" + menuItem1.getKey() + "," + menuItem2.getKey());


        keys = menuItemService.getPageKeyByPath( menuItem1, "./.././././" );
        assertEquals( keys, "" + menuItem1.getKey() + "," + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./././.././././" );
        assertEquals( keys, "" + menuItem1.getKey() + "," + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "../name1" );
        assertEquals( keys, "" + menuItem1.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "../name2" );
        assertEquals( keys, "" + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./../name1" );
        assertEquals( keys, "" + menuItem1.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./../name2" );
        assertEquals( keys, "" + menuItem2.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "../../Hello World!" );
        assertEquals( keys, "" + section.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "./../../Hello World!" );
        assertEquals( keys, "" + section.getKey());

        keys = menuItemService.getPageKeyByPath( menuItem1, "../.." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, "../../.." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, "nope" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, "./nope" );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, "./nope/." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, "./nope/.." );
        assertEquals( keys, "");

        keys = menuItemService.getPageKeyByPath( menuItem1, "../nope" );
        assertEquals( keys, "");
    }


    private void verifyFinalReorderingResult( int chessContentKey, int footballContentKey, int golfContentKey, int icehockeyContentKey )
    {
        MenuItemEntity testSection;
        Set<SectionContentEntity> storedSectionContents;
        Iterator<SectionContentEntity> secCntIterator;
        testSection = fixture.findMenuItemByName( "Sports", 30 );
        storedSectionContents = testSection.getSectionContents();
        assertEquals( 4, storedSectionContents.size() );
        secCntIterator = storedSectionContents.iterator();
        boolean chessContentChecked = false;
        boolean footballContentChecked = false;
        boolean golfContentChecked = false;
        boolean icehockeyContentChecked = false;
        while ( secCntIterator.hasNext() )
        {
            SectionContentEntity secCntLoopHolder = secCntIterator.next();
            int loopHolderKey = secCntLoopHolder.getContent().getKey().toInt();
            if ( loopHolderKey == chessContentKey )
            {
                assertEquals( 2, secCntLoopHolder.getOrder() );
                chessContentChecked = true;
            }
            else if ( loopHolderKey == footballContentKey )
            {
                assertEquals( 1, secCntLoopHolder.getOrder() );
                footballContentChecked = true;
            }
            else if ( loopHolderKey == golfContentKey )
            {
                assertEquals( 4, secCntLoopHolder.getOrder() );
                golfContentChecked = true;
            }
            else if ( loopHolderKey == icehockeyContentKey )
            {
                assertEquals( 3, secCntLoopHolder.getOrder() );
                icehockeyContentChecked = true;
            }
        }
        assertTrue( chessContentChecked && footballContentChecked && golfContentChecked && icehockeyContentChecked );
    }

    private void VerifyFinalResultOfApproveUnapproveTest( int proKey, int againstKey )
    {
        MenuItemEntity testSection;
        Set<SectionContentEntity> storedSectionContents;
        Iterator<SectionContentEntity> secCntIterator;
        boolean proContentChecked = false;
        boolean againstContentChecked = false;
        testSection = fixture.findMenuItemByName( "Opinion", 40 );
        storedSectionContents = testSection.getSectionContents();
        assertEquals( 2, storedSectionContents.size() );
        secCntIterator = storedSectionContents.iterator();
        while ( secCntIterator.hasNext() )
        {
            SectionContentEntity secCntLoopHolder = secCntIterator.next();
            int loopHolderKey = secCntLoopHolder.getContent().getKey().toInt();
            if ( loopHolderKey == againstKey )
            {
                assertEquals( true, secCntLoopHolder.isApproved() );
                againstContentChecked = true;
            }
            else if ( loopHolderKey == proKey )
            {
                assertEquals( false, secCntLoopHolder.isApproved() );
                proContentChecked = true;
            }
        }
        assertTrue( proContentChecked && againstContentChecked );
    }
}
