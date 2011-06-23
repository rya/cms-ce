package com.enonic.cms.business.core.content;

import junit.framework.TestCase;

public class ContentNameForCopiesIteratorTest
        extends TestCase
{
    public void testCurrentName()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage" );
        assertEquals( iterator.currentName(), "MyPage(0)" );
    }

    public void testNext()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage" );
        assertEquals( iterator.next(), "MyPage(1)" );
    }

    public void testOriginal_0()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage" );
        assertEquals( iterator.next(), "MyPage(1)" );
        assertEquals( iterator.next(), "MyPage(2)" );
    }


    public void testOriginal_1()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage(1)" );
        assertEquals( iterator.next(), "MyPage(2)" );
        assertEquals( iterator.next(), "MyPage(3)" );
    }

    public void testOriginal_2()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage(2)" );
        assertEquals( iterator.next(), "MyPage(1)" );
        assertEquals( iterator.next(), "MyPage(3)" );
    }

    public void testUnbalanced1()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage2)" );
        assertEquals( iterator.next(), "MyPage2)(1)" );
        assertEquals( iterator.next(), "MyPage2)(2)" );
    }

    public void testUnbalanced2()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage(2" );
        assertEquals( iterator.next(), "MyPage(2(1)" );
        assertEquals( iterator.next(), "MyPage(2(2)" );
    }

    public void testUnbalanced3()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage(2))" );
        assertEquals( iterator.next(), "MyPage(2))(1)" );
        assertEquals( iterator.next(), "MyPage(2))(2)" );
    }

    public void testUnbalanced4()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage(((2)" );
        assertEquals( iterator.next(), "MyPage(((1)" );
        assertEquals( iterator.next(), "MyPage(((3)" );
    }


    public void testBalancedWithText()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage(original)" );
        assertEquals( iterator.next(), "MyPage(original)(1)" );
        assertEquals( iterator.next(), "MyPage(original)(2)" );
    }


    public void testIterator()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage" );
        assertNotNull( iterator.iterator() );
    }

    public void testHasNext()
            throws Exception
    {
        ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage" );
        assertTrue( iterator.hasNext() );
        assertTrue( iterator.hasNext() );
        assertTrue( iterator.hasNext() );
        assertTrue( iterator.hasNext() );
        assertTrue( iterator.hasNext() );
    }

    public void testRemove()
            throws Exception
    {
        try
        {
            ContentNameForCopiesIterator iterator = new ContentNameForCopiesIterator( "MyPage" );
            iterator.remove();

            fail();
        }
        catch ( UnsupportedOperationException e )
        {

        }
    }
}
