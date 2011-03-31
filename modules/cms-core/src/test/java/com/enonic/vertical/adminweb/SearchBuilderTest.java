/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SearchBuilderTest
    extends TestCase
{

    public void testSimpleUserInput()
    {
        SearchStringBuffer userInput;

        userInput = SearchBuilder.buildFromUserInput( "A", false, false, false );
        Assert.assertEquals( "", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", true, false, false );
        Assert.assertEquals( "(title CONTAINS \"A\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", false, true, false );
        Assert.assertEquals( "(data/* CONTAINS \"A\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", false, false, true );
        Assert.assertEquals( "(fulltext CONTAINS \"A\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", true, true, false );
        Assert.assertEquals( "(title CONTAINS \"A\" OR data/* CONTAINS \"A\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", true, false, true );
        Assert.assertEquals( "(title CONTAINS \"A\" OR fulltext CONTAINS \"A\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", false, true, true );
        Assert.assertEquals( "(data/* CONTAINS \"A\" OR fulltext CONTAINS \"A\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A", true, true, true );
        Assert.assertEquals( "(title CONTAINS \"A\" OR data/* CONTAINS \"A\" OR fulltext CONTAINS \"A\")", userInput.toString() );
    }

    public void testAdvancedUserInput()
    {
        SearchStringBuffer userInput;

        userInput = SearchBuilder.buildFromUserInput( "A B", false, false, false );
        Assert.assertEquals( "", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", true, false, false );
        Assert.assertEquals( "(title CONTAINS \"A\") AND (title CONTAINS \"B\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", false, true, false );
        Assert.assertEquals( "(data/* CONTAINS \"A\") AND (data/* CONTAINS \"B\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", false, false, true );
        Assert.assertEquals( "(fulltext CONTAINS \"A\") AND (fulltext CONTAINS \"B\")", userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", true, true, false );
        Assert.assertEquals( "(title CONTAINS \"A\" OR data/* CONTAINS \"A\") AND (title CONTAINS \"B\" OR data/* CONTAINS \"B\")",
                             userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", true, false, true );
        Assert.assertEquals( "(title CONTAINS \"A\" OR fulltext CONTAINS \"A\") AND (title CONTAINS \"B\" OR fulltext CONTAINS \"B\")",
                             userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", false, true, true );
        Assert.assertEquals( "(data/* CONTAINS \"A\" OR fulltext CONTAINS \"A\") AND (data/* CONTAINS \"B\" OR fulltext CONTAINS \"B\")",
                             userInput.toString() );

        userInput = SearchBuilder.buildFromUserInput( "A B", true, true, true );
        Assert.assertEquals(
            "(title CONTAINS \"A\" OR data/* CONTAINS \"A\" OR fulltext CONTAINS \"A\") AND (title CONTAINS \"B\" OR data/* CONTAINS \"B\" OR fulltext CONTAINS \"B\")",
            userInput.toString() );
    }

}