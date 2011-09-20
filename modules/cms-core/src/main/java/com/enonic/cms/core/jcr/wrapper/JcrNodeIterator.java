/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import java.util.Iterator;

public interface JcrNodeIterator
    extends Iterator<JcrNode>
{

    public JcrNode nextNode();

    public void skip( long skipNum );

    public long getSize();

    public long getPosition();
}
