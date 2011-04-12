/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.util.Date;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;

import static org.junit.Assert.*;


public class ContentEntityTest
{

    @Test
    public void testAssignmentOverdue()
    {
        ContentEntity content = new ContentEntity();

        Date assignmentDate = new DateTime().minus( new Period().withSeconds( 2 ) ).toDate();
        content.setAssignmentDueDate( assignmentDate );
        assertTrue( content.isAssignmentOverdue() );

        assignmentDate = new DateTime().plus( new Period().withHours( 2 ) ).toDate();
        content.setAssignmentDueDate( assignmentDate );
        assertFalse( content.isAssignmentOverdue() );
    }

    @Test
    public void testIsAvailableWhenOnlyPublishFromIsSet()
    {

        DateTime publishFrom = new DateTime( 2008, 12, 1, 16, 0, 0, 0 );

        ContentEntity content = new ContentEntity();
        ContentVersionEntity version = new ContentVersionEntity();
        version.setKey( new ContentVersionKey( 101 ) );
        version.setStatus( ContentStatus.APPROVED );
        content.setDeleted( false );
        content.setAvailableFrom( publishFrom.toDate() );
        content.setMainVersion( version );
        version.setContent( content );

        Date date = new DateTime( 2008, 12, 1, 16, 0, 0, 0 ).toDate();
        assertTrue( content.isOnline( date ) );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );

        date = new DateTime( 2008, 12, 1, 16, 11, 0, 0 ).toDate();
        assertTrue( content.isOnline( date ) );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );

        date = new DateTime( 2008, 12, 1, 10, 0, 0, 0 ).toDate();
        assertFalse( content.isOnline( date ) );
        assertFalse( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISH_WAITING );

        date = new DateTime( 2008, 12, 1, 15, 59, 59, 999 ).toDate();
        assertFalse( content.isOnline( date ) );
        assertFalse( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISH_WAITING );
    }

    @Test
    public void testIsAvailableWhenBothPublishFromAndToIsSet()
    {

        DateTime publishFrom = new DateTime( 2008, 12, 1, 16, 0, 0, 0 );
        DateTime publishTo = new DateTime( 2008, 12, 2, 12, 0, 0, 0 );

        ContentEntity content = new ContentEntity();
        ContentVersionEntity version = new ContentVersionEntity();
        version.setKey( new ContentVersionKey( 101 ) );
        version.setStatus( ContentStatus.APPROVED );
        content.setDeleted( false );
        content.setAvailableFrom( publishFrom.toDate() );
        content.setAvailableTo( publishTo.toDate() );
        content.setMainVersion( version );
        version.setContent( content );

        Date date = new DateTime( 2008, 12, 1, 16, 0, 0, 0 ).toDate();
        assertTrue( content.isOnline( date ) );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );

        date = new DateTime( 2008, 12, 1, 15, 59, 59, 999 ).toDate();
        assertFalse( content.isOnline( date ) );
        assertFalse( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISH_WAITING );

        date = new DateTime( 2008, 12, 2, 12, 0, 0, 0 ).toDate();
        assertFalse( content.isOnline( date ) );
        assertFalse( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISH_EXPIRED );

        date = new DateTime( 2008, 12, 2, 12, 0, 0, 1 ).toDate();
        assertFalse( content.isOnline( date ) );
        assertFalse( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISHED );
        assertTrue( content.getMainVersion().getState( date ) == ContentVersionEntity.STATE_PUBLISH_EXPIRED );
    }

    @Test
    public void testPublishTo()
    {
        DateTime publishFrom = new DateTime( 2010, 4, 19, 16, 30, 0, 0 );
        DateTime publishTo = new DateTime( 2010, 4, 19, 16, 32, 0, 0 );

        ContentEntity content = new ContentEntity();
        ContentVersionEntity version = new ContentVersionEntity();
        version.setKey( new ContentVersionKey( 101 ) );
        version.setStatus( ContentStatus.APPROVED );
        content.setDeleted( false );
        content.setAvailableFrom( publishFrom.toDate() );
        content.setAvailableTo( publishTo.toDate() );
        content.setMainVersion( version );
        version.setContent( content );

        Date justAfterPublishTo = new DateTime( 2010, 4, 19, 16, 32, 1, 1 ).toDate();
        assertFalse( content.isOnline( justAfterPublishTo ) );
        assertTrue( content.getMainVersion().getState( justAfterPublishTo ) == ContentVersionEntity.STATE_PUBLISH_EXPIRED );

        Date atPublishTo = new DateTime( 2010, 4, 19, 16, 32, 0, 0 ).toDate();
        assertFalse( content.isOnline( atPublishTo ) );
        assertTrue( content.getMainVersion().getState( atPublishTo ) == ContentVersionEntity.STATE_PUBLISH_EXPIRED );

        Date justBeforePublishTo = new DateTime( 2010, 4, 19, 16, 31, 59, 999 ).toDate();
        assertTrue( content.isOnline( justBeforePublishTo ) );
        assertTrue( content.getMainVersion().getState( justBeforePublishTo ) == ContentVersionEntity.STATE_PUBLISHED );
    }

    @Test
    public void testPublishFrom()
    {
        DateTime publishFrom = new DateTime( 2010, 4, 19, 16, 30, 0, 0 );
        DateTime publishTo = new DateTime( 2010, 4, 19, 16, 32, 0, 0 );

        ContentEntity content = new ContentEntity();
        ContentVersionEntity version = new ContentVersionEntity();
        version.setKey( new ContentVersionKey( 101 ) );
        version.setStatus( ContentStatus.APPROVED );
        content.setDeleted( false );
        content.setAvailableFrom( publishFrom.toDate() );
        content.setAvailableTo( publishTo.toDate() );
        content.setMainVersion( version );
        version.setContent( content );

        Date justAfterPublishFrom = new DateTime( 2010, 4, 19, 16, 30, 1, 1 ).toDate();
        assertTrue( content.isOnline( justAfterPublishFrom ) );
        assertTrue( content.getMainVersion().getState( justAfterPublishFrom ) == ContentVersionEntity.STATE_PUBLISHED );

        Date atPublishFrom = new DateTime( 2010, 4, 19, 16, 30, 0, 0 ).toDate();
        assertTrue( content.isOnline( atPublishFrom ) );
        assertTrue( content.getMainVersion().getState( atPublishFrom ) == ContentVersionEntity.STATE_PUBLISHED );

        Date justBeforePublishFrom = new DateTime( 2010, 4, 19, 16, 29, 59, 999 ).toDate();
        assertFalse( content.isOnline( justBeforePublishFrom ) );
        assertTrue( content.getMainVersion().getState( justBeforePublishFrom ) == ContentVersionEntity.STATE_PUBLISH_WAITING );
    }

    @Test
    public void testSetAvailableFrom()
    {
        ContentEntity content = new ContentEntity();

        boolean modified = content.setAvailableFrom( null );
        assertFalse( modified );

        modified = content.setAvailableFrom( new DateTime( 2008, 1, 1, 1, 1, 1, 1 ).toDate() );
        assertTrue( modified );

        modified = content.setAvailableFrom( new DateTime( 2008, 1, 1, 1, 1, 1, 1 ).toDate() );
        assertFalse( modified );

        modified = content.setAvailableFrom( new DateTime( 2005, 1, 1, 1, 1, 1, 1 ).toDate() );
        assertTrue( modified );

        modified = content.setAvailableFrom( null );
        assertTrue( modified );
    }

    @Test
    public void testSetAvailableTo()
    {
        ContentEntity content = new ContentEntity();

        boolean modified = content.setAvailableTo( null );
        assertFalse( modified );

        modified = content.setAvailableTo( new DateTime( 2008, 1, 1, 1, 1, 1, 1 ).toDate() );
        assertTrue( modified );

        modified = content.setAvailableTo( new DateTime( 2008, 1, 1, 1, 1, 1, 1 ).toDate() );
        assertFalse( modified );

        modified = content.setAvailableTo( new DateTime( 2005, 1, 1, 1, 1, 1, 1 ).toDate() );
        assertTrue( modified );

        modified = content.setAvailableTo( null );
        assertTrue( modified );
    }
}
