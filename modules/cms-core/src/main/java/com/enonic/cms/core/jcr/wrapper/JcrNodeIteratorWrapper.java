package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

class JcrNodeIteratorWrapper
    implements JcrNodeIterator
{

    private NodeIterator nodeIterator;

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
        return JcrWrappers.wrap( (Node) nodeIterator.next() );
    }

    @Override
    public void remove()
    {
        nodeIterator.remove();
    }
}
