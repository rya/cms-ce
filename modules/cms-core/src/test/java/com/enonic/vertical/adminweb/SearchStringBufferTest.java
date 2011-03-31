/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SearchStringBufferTest
    extends TestCase
{

    public void testWithDefaultOperator()
    {
        SearchStringBuffer buffer = new SearchStringBuffer();

        buffer.appendAttachments( "attach" );
        buffer.appendCreated( " = ", "date(\"2001-01-01\")" );
        buffer.appendData( "Enonic's data" );
        buffer.appendKey( "123" );
        buffer.appendModifier( "456" );
        buffer.appendOwner( "789" );
        buffer.appendRaw( "raw" );
        buffer.appendStatus( "246" );
        buffer.appendTimestamp( " = ", "date(\"2002-02-02\")" );
        buffer.appendTitle( "title" );

        Assert.assertEquals(
            "fulltext CONTAINS \"attach\" AND @created = date(\"2001-01-01\") AND data/* CONTAINS \"Enonic's data\" AND @key = 123 AND modifier/key = 456 AND owner/key = 789 AND (raw) AND @status = 246 AND @timestamp = date(\"2002-02-02\") AND title CONTAINS \"title\"",
            buffer.toString() );

    }
}