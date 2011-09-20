/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.NodeIterator;

class JcrNodeIteratorWrapper
    implements JcrNodeIterator
{

    private final NodeIterator nodeIterator;

    JcrNodeIteratorWrapper( NodeIterator nodeIterator )
    {
        this.nodeIterator = nodeIterator;
    }

    @Override
    public JcrNode nextNode()
    {
        return JcrWrappers.wrap( nodeIterator.nextNode() );
    }

    @Override
    public void skip( long skipNum )
    {
        nodeIterator.skip( skipNum );
    }

    @Override
    public long getSize()
    {
        return nodeIterator.getSize();
    }

    @Override
    public long getPosition()
    {
        return nodeIterator.getPosition();
    }

    @Override
    public boolean hasNext()
    {
        return nodeIterator.hasNext();
    }

    @Override
    public JcrNode next()
    {
        return nextNode();
    }

    @Override
    public void remove()
    {
        nodeIterator.remove();
    }
}
