/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import java.util.NoSuchElementException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class JcrNodeIteratorTest
{

    @Test
    public void testIteratorNextOneElement()
            throws RepositoryException
    {
        NodeIterator nodeIt = createIterator( "node_name" );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        assertTrue( nodeIt.hasNext() );

        JcrNode nodeOne = jcrNodeIterator.nextNode();
        assertEquals( "node_name", nodeOne.getName() );
    }

    @Test
    public void testIteratorNext()
            throws RepositoryException
    {
        NodeIterator nodeIt = createIterator( "node1", "node2", "node3" );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        assertTrue( jcrNodeIterator.hasNext() );
        assertEquals( jcrNodeIterator.nextNode().getName(), "node1" );

        assertTrue( jcrNodeIterator.hasNext() );
        assertEquals( jcrNodeIterator.nextNode().getName(), "node2" );

        assertTrue( jcrNodeIterator.hasNext() );
        assertEquals( jcrNodeIterator.nextNode().getName(), "node3" );

        assertFalse( jcrNodeIterator.hasNext() );

        try
        {
            jcrNodeIterator.next();
            fail( "Expected exception: " + NoSuchElementException.class.getName() );
        }
        catch ( NoSuchElementException e )
        {
            // DO NOTHING
        }
    }

    @Test
    public void testIteratorNextNode()
            throws RepositoryException
    {
        NodeIterator nodeIt = createIterator( "node1", "node2", "node3" );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        assertTrue( jcrNodeIterator.hasNext() );
        assertEquals( jcrNodeIterator.nextNode().getName(), "node1" );

        assertTrue( jcrNodeIterator.hasNext() );
        assertEquals( jcrNodeIterator.nextNode().getName(), "node2" );

        assertTrue( jcrNodeIterator.hasNext() );
        assertEquals( jcrNodeIterator.nextNode().getName(), "node3" );

        assertFalse( jcrNodeIterator.hasNext() );

        try
        {
            jcrNodeIterator.nextNode();
            fail( "Expected exception: " + NoSuchElementException.class.getName() );
        }
        catch ( NoSuchElementException e )
        {
            // DO NOTHING
        }
    }

    @Test
    public void testSkip()
            throws RepositoryException
    {
        NodeIterator nodeIt = createIterator( "node1", "node2", "node3" );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        jcrNodeIterator.skip( 2 );

        verify( nodeIt ).skip( 2 );
    }

    @Test
    public void testRemove()
            throws RepositoryException
    {
        NodeIterator nodeIt = createIterator( "node1", "node2", "node3" );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        jcrNodeIterator.remove();

        verify( nodeIt ).remove();
    }

    @Test
    public void testGetSize()
            throws RepositoryException
    {
        String[] nodeNames = new String[]{"node1", "node2", "node3"};
        NodeIterator nodeIt = createIterator( nodeNames );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        assertEquals( jcrNodeIterator.getSize(), nodeNames.length );
    }

    @Test
    public void testGetPosition()
            throws RepositoryException
    {
        NodeIterator nodeIt = createIterator( "node1", "node2", "node3" );
        JcrNodeIterator jcrNodeIterator = JcrWrappers.wrap( nodeIt );

        jcrNodeIterator.getPosition();

        verify( nodeIt ).getPosition();
    }

    private Node createNode( String nodeName )
            throws RepositoryException
    {
        Node node = Mockito.mock( Node.class );
        Mockito.when( node.getName() ).thenReturn( nodeName );
        return node;
    }

    private NodeIterator createIterator( String... nodeNames )
            throws RepositoryException
    {
        Node[] nodes = new Node[nodeNames.length];
        for ( int i = 0; i < nodeNames.length; i++ )
        {
            nodes[i] = createNode( nodeNames[i] );
        }
        return createIterator( nodes );
    }

    private NodeIterator createIterator( Node... nodes )
    {
        NodeIterator nodeIt = Mockito.mock( NodeIterator.class );

        OngoingStubbing<Boolean> returnChainHasNext = null;
        OngoingStubbing<Node> returnChainNext = null;

        // mock hasNext()
        for ( Node node : nodes )
        {
            if ( returnChainHasNext == null )
            {
                returnChainHasNext = Mockito.when( nodeIt.hasNext() ).thenReturn( true );
            }
            else
            {
                returnChainHasNext = returnChainHasNext.thenReturn( true );
            }
        }
        if ( returnChainHasNext == null )
        {
            Mockito.when( nodeIt.hasNext() ).thenReturn( false );
        }
        else
        {
            returnChainHasNext.thenReturn( false );
        }

        // mock nextNode()
        for ( Node node : nodes )
        {
            if ( returnChainNext == null )
            {

                returnChainNext = Mockito.when( nodeIt.nextNode() ).thenReturn( node );
            }
            else
            {
                returnChainNext = returnChainNext.thenReturn( node );
            }
        }
        if ( returnChainNext == null )
        {

            Mockito.when( nodeIt.nextNode() ).thenThrow( new NoSuchElementException() );
        }
        else
        {
            returnChainNext.thenThrow( new NoSuchElementException() );
        }

        Mockito.when( nodeIt.getSize() ).thenReturn( (long) nodes.length );

        return nodeIt;
    }
}
