/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class JcrWrappersTest
{

    @Test
    public void testWrappers()
    {
        Repository repo = Mockito.mock( Repository.class );
        JcrRepository wrappedJcrRepository = JcrWrappers.wrap( repo );
        assertNotNull( wrappedJcrRepository );

        Session session = Mockito.mock( Session.class );
        JcrSession wrappedJcrSession = JcrWrappers.wrap( session );
        assertNotNull( wrappedJcrSession );

        Node node = Mockito.mock( Node.class );
        JcrNode wrappedJcrNode = JcrWrappers.wrap( node );
        assertNotNull( wrappedJcrNode );

        NodeIterator nodeIterator = Mockito.mock( NodeIterator.class );
        JcrNodeIterator wrappedJcrNodeIterator = JcrWrappers.wrap( nodeIterator );
        assertNotNull( wrappedJcrNodeIterator );
    }

    @Test
    public void testWrapUnwrap()
    {
        Session session = Mockito.mock( Session.class );
        JcrSession wrappedJcrSession = JcrWrappers.wrap( session );
        Session unwrappedSession = JcrWrappers.unwrap( wrappedJcrSession );
        assertEquals( session, unwrappedSession );

        Node node = Mockito.mock( Node.class );
        JcrNode wrappedJcrNode = JcrWrappers.wrap( node );
        Node unwrappedNode = JcrWrappers.unwrap( wrappedJcrNode );
        assertEquals( node, unwrappedNode );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnwrapUnsupportedJcrNode()
    {
        JcrNode externalWrappedJcrNode = Mockito.mock( JcrNode.class );
        JcrWrappers.unwrap( externalWrappedJcrNode );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnwrapUnsupportedJcrSession()
    {
        JcrSession externalWrappedJcrSession = Mockito.mock( JcrSession.class );
        JcrWrappers.unwrap( externalWrappedJcrSession );
    }
}
