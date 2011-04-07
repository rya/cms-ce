/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class implements the relation tree.
 */
public final class RelationTree
{

    private final static class Entry
        implements RelationNode
    {

        private final Object key;

        private Entry parent;

        private final List<RelationNode> children;

        private Object data;

        private boolean selected;

        private EntrySet set;

        private Entry()
        {
            this( null );
        }

        private Entry( Object key )
        {
            this.key = key;
            this.parent = null;
            this.children = new LinkedList<RelationNode>();
            this.data = null;
            this.selected = false;
        }

        public Object getKey()
        {
            return this.key;
        }

        public int getChildCount()
        {
            return this.children.size();
        }

        public int getTotalChildCount()
        {
            int count = this.children.size();

            for ( RelationNode aChildren : this.children )
            {
                count += aChildren.getTotalChildCount();
            }

            return count;
        }

        public List getChildren()
        {
            return Collections.unmodifiableList( this.children );
        }

        private boolean isAncestor( RelationNode node )
        {
            return this.parent != null && ( this.parent == node || this.parent.isAncestor( node ) );
        }

        private boolean isAncestorOrSelf( RelationNode node )
        {
            return isAncestor( node ) || ( node == this );
        }

        public Object accept( RelationVisitor visitor )
        {
            if ( visitor != null )
            {
                return visitor.visit( this );
            }
            else
            {
                return null;
            }
        }

        public Object getData()
        {
            return this.data;
        }

        public void setData( Object data )
        {
            this.data = data;
        }

        public void addChild( Entry entry )
        {
            if ( !isAncestorOrSelf( entry ) )
            {
                entry.parent = this;
                this.children.add( entry );
                clearEntrySet();
            }
        }

        private Entry getRootEntry()
        {
            if ( this.parent != null )
            {
                return this.parent.getRootEntry();
            }
            else
            {
                return this;
            }
        }

        private EntrySet getEntrySet()
        {
            Entry root = getRootEntry();

            if ( root.set == null )
            {
                root.set = new EntrySet();
                root.set.addEntry( root );
            }

            return root.set;
        }

        private void clearEntrySet()
        {
            Entry root = getRootEntry();

            if ( root.set != null )
            {
                root.set = null;
            }
        }

        public RelationNode getNode( Object key )
        {
            if ( key == null )
            {
                return getRootEntry();
            }
            else
            {
                return getEntrySet().getEntry( key );
            }
        }

        public boolean isSelected()
        {
            return this.selected;
        }

        private void setSelected( boolean selected )
        {
            this.selected = selected;
        }

        public void selectLevels( int levels )
        {
            if ( levels >= 0 )
            {
                setSelected( true );
                for ( Object aChildren : this.children )
                {
                    ( (Entry) aChildren ).selectLevels( levels - 1 );
                }
            }
        }

        public Set findSelectedKeys()
        {
            HashSet set = new HashSet();
            findSelected( set, true );
            return set;
        }

        private void findSelected( Set set, boolean keys )
        {
            if ( this.selected )
            {
                if ( this.key != null )
                {
                    set.add( keys ? this.key : this );
                }
            }

            for ( Object aChildren : this.children )
            {
                ( (Entry) aChildren ).findSelected( set, keys );
            }
        }

        public int hashCode()
        {
            return this.key.hashCode();
        }
    }

    private final static class EntrySet
    {

        private final Map<Object, Entry> map;

        public EntrySet()
        {
            this.map = new HashMap<Object, Entry>();
        }

        public void addEntry( Entry entry )
        {
            addEntry( entry.key, entry );
            addEntries( entry.children );
        }

        public void addEntries( Collection entries )
        {
            if ( entries != null )
            {
                for ( Object entry : entries )
                {
                    addEntry( (Entry) entry );
                }
            }
        }

        private void addEntry( Object key, Entry entry )
        {
            if ( key != null )
            {
                this.map.put( key, entry );
            }
        }

        public Entry getEntry( Object key )
        {
            return this.map.get( key );
        }
    }

    private final HashMap<Object, Object> keyMap;

    private final LinkedList<Object> orderedChildrenList;

    private RelationNode root;

    public RelationTree()
    {
        this.keyMap = new HashMap<Object, Object>();
        this.orderedChildrenList = new LinkedList<Object>();
    }

    public RelationNode getRoot()
    {
        if ( this.root == null )
        {
            this.root = buildRoot( this.keyMap, this.orderedChildrenList );
        }

        return this.root;
    }

    public RelationNode getNode( Object key )
    {
        RelationNode root = getRoot();

        if ( key != null )
        {
            return root.getNode( key );
        }
        else
        {
            return root;
        }
    }

    private static RelationNode buildRoot( Map keys, List orderList )
    {
        // Create nodes
        HashMap<Object, Entry> nodeMap = new HashMap<Object, Entry>();
        for ( Object key : keys.keySet() )
        {
            if ( !nodeMap.containsKey( key ) )
            {
                nodeMap.put( key, new Entry( key ) );
            }
        }

        // Attach nodes to parent
        Entry root = new Entry();
        for ( Object key : orderList )
        {
            Object parent = keys.get( key );

            Entry keyEntry = nodeMap.get( key );
            Entry parentEntry = root;

            if ( parent != null )
            {
                parentEntry = nodeMap.get( parent );
            }

            if ( parentEntry != null )
            {
                parentEntry.addChild( keyEntry );
            }
        }

        return root;
    }

    public boolean addChild( Object child )
    {
        return addChild( null, child );
    }

    public boolean addChild( Object parent, Object child )
    {
        if ( this.keyMap.containsKey( child ) )
        {
            return false;
        }
        else
        {
            this.keyMap.put( child, parent );
            this.orderedChildrenList.add( child );
            this.root = null;
            return true;
        }
    }

    public boolean setRoot( Object key )
    {
        if ( this.keyMap.containsKey( key ) )
        {
            this.keyMap.put( key, null );
            return true;
        }
        else
        {
            return false;
        }
    }
}
