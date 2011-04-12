/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.security.group;

import com.enonic.cms.core.security.group.QualifiedGroupname;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Sep 25, 2009
 */
public class QualifiedGroupnameTest
{
    @Test
    public void parseGroupInUserstore()
    {
        final QualifiedGroupname qnameWithColonSeparator = QualifiedGroupname.parse( "enonic:Kundeservice" );

        assertEquals( false, qnameWithColonSeparator.isGlobal() );
        assertEquals( "Kundeservice", qnameWithColonSeparator.getGroupname() );
        assertEquals( "enonic", qnameWithColonSeparator.getUserStoreName() );
        assertEquals( null, qnameWithColonSeparator.getUserStoreKey() );

        final QualifiedGroupname qnameWithSlashSeparator = QualifiedGroupname.parse( "enonic\\Kundeservice" );
        assertEquals( false, qnameWithSlashSeparator.isGlobal() );
        assertEquals( "Kundeservice", qnameWithSlashSeparator.getGroupname() );
        assertEquals( "enonic", qnameWithSlashSeparator.getUserStoreName() );
        assertEquals( null, qnameWithSlashSeparator.getUserStoreKey() );

        final QualifiedGroupname qnameWithUserstoreKey = QualifiedGroupname.parse( "#123\\Kundeservice" );
        assertEquals( false, qnameWithUserstoreKey.isGlobal() );
        assertEquals( "Kundeservice", qnameWithUserstoreKey.getGroupname() );
        assertEquals( new UserStoreKey( 123 ), qnameWithUserstoreKey.getUserStoreKey() );
        assertEquals( null, qnameWithUserstoreKey.getUserStoreName() );
    }

    @Test
    public void parseGlobalGroup()
    {
        QualifiedGroupname qualifiedGroupname = QualifiedGroupname.parse( "Kundeservice" );
        assertEquals( true, qualifiedGroupname.isGlobal() );
        assertEquals( "Kundeservice", qualifiedGroupname.getGroupname() );
        assertEquals( null, qualifiedGroupname.getUserStoreName() );
        assertEquals( null, qualifiedGroupname.getUserStoreKey() );
    }
}
