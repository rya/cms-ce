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

    public RelationNode getParent();

    public boolean isRoot();

    public boolean hasParent();

    public boolean hasChildren();

    public int getChildCount();

    public int getTotalChildCount();

    public Object getData();

    public void setData( Object data );

    public List getChildren();

    public boolean isAncestor( RelationNode node );

    public boolean isAncestorOrSelf( RelationNode node );

    public int getLevel();

    public boolean isChild( RelationNode node );

    public Set findSelectedKeys();

    public Set findSelectedNodes();

    public Object accept( RelationVisitor visitor );

    public RelationNode getRoot();

    public RelationNode getNode( Object key );

    public boolean isSelected();

    public void setSelected( boolean selected );

    public void selectAll();

    public void selectLevels( int levels );

    public void clearSelected();
}
