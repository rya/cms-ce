/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.List;
import java.util.Set;

/**
 * This interface defines the relation node.
 */
public interface RelationNode
{

    public Object getKey();

    public int getChildCount();

    public int getTotalChildCount();

    public Object getData();

    public void setData( Object data );

    public List getChildren();

    public Set findSelectedKeys();

    public Object accept( RelationVisitor visitor );

    public RelationNode getNode( Object key );

    public boolean isSelected();

    public void selectLevels( int levels );

}
