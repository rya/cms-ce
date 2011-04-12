/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.resultset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;


public class RelatedContentResultSetImpl
    implements RelatedContentResultSet
{
    /**
     * The distinct set of related Content.
     */
    private Set<ContentKey> contentKeySet = new LinkedHashSet<ContentKey>();

    private Set<ContentEntity> contentSet = new LinkedHashSet<ContentEntity>();

    /**
     * The distinct map of RelatedContent, mapped by content key.
     */
    private Multimap<ContentKey, RelatedContent> multipleRelatedContentByContentKey = LinkedHashMultimap.create();

    private Multimap<ContentVersionKey, RelatedChildContent> relatedChildrenByVersionKey = LinkedHashMultimap.create();

    private Map<ContentKey, List<RelatedParentContent>> rootRelatedParents = new HashMap<ContentKey, List<RelatedParentContent>>();

    private Map<ContentVersionKey, List<RelatedChildContent>> rootRelatedChildren =
        new HashMap<ContentVersionKey, List<RelatedChildContent>>();

    public int size()
    {
        return contentSet.size();
    }

    public boolean isEmpty()
    {
        return contentSet.isEmpty();
    }

    public Collection<RelatedContent> getDistinctCollectionOfRelatedContent()
    {
        Map<ContentKey, RelatedContent> relatedContentDistinctByContentKey = new LinkedHashMap<ContentKey, RelatedContent>();
        for ( RelatedContent rc : multipleRelatedContentByContentKey.values() )
        {
            if ( !relatedContentDistinctByContentKey.containsKey( rc.getContent().getKey() ) )
            {
                relatedContentDistinctByContentKey.put( rc.getContent().getKey(), rc );
            }
        }
        return relatedContentDistinctByContentKey.values();
    }

    public Set<ContentEntity> getDinstinctSetOfContent()
    {
        return contentSet;
    }

    public Collection<ContentKey> getContentKeys()
    {
        return Collections.unmodifiableSet( contentKeySet );
    }

    public void add( RelatedContent relatedContent )
    {
        doAdd( relatedContent );
    }

    public void addAll( Collection<RelatedContent> collection )
    {
        for ( RelatedContent node : collection )
        {
            doAdd( node );
        }
    }

    private void doAdd( RelatedContent relatedContent )
    {
        contentKeySet.add( relatedContent.getContent().getKey() );
        contentSet.add( relatedContent.getContent() );

        if ( relatedContent instanceof RelatedChildContent )
        {
            RelatedChildContent relatedChildContent = (RelatedChildContent) relatedContent;
            relatedChildrenByVersionKey.put( relatedChildContent.getParentVersionKey(), relatedChildContent );
        }
        multipleRelatedContentByContentKey.put( relatedContent.getContent().getKey(), relatedContent );
        multipleRelatedContentByContentKey.get( relatedContent.getContent().getKey() ).size();
    }


    public Iterable<RelatedParentContent> getRootRelatedParents( ContentEntity content )
    {
        List<RelatedParentContent> list = rootRelatedParents.get( content.getKey() );
        if ( list == null )
        {
            return new ArrayList<RelatedParentContent>();
        }
        return list;
    }

    public Iterable<RelatedChildContent> getRootRelatedChildren( ContentVersionEntity contentVersion )
    {
        List<RelatedChildContent> list = rootRelatedChildren.get( contentVersion.getKey() );
        if ( list == null )
        {
            return new ArrayList<RelatedChildContent>();
        }
        return list;
    }

    public void addRootRelatedChild( RelatedChildContent child )
    {
        List<RelatedChildContent> list = rootRelatedChildren.get( child.getParentVersionKey() );
        if ( list == null )
        {
            list = new ArrayList<RelatedChildContent>();
            rootRelatedChildren.put( child.getParentVersionKey(), list );
        }
        list.add( child );
    }

    public void addRootRelatedParent( RelatedParentContent parent )
    {
        List<RelatedParentContent> list = rootRelatedParents.get( parent.getChildContentKey() );
        if ( list == null )
        {
            list = new ArrayList<RelatedParentContent>();
            rootRelatedParents.put( parent.getChildContentKey(), list );
        }
        list.add( parent );
    }

    public RelatedContent getRelatedContent( ContentKey contentKey )
    {
        final Collection<RelatedContent> collection = multipleRelatedContentByContentKey.get( contentKey );
        if ( collection.isEmpty() )
        {
            return null;
        }
        return collection.iterator().next();
    }

    public void overwriteRootRelatedChild( RelatedChildContent overwritingRCC )
    {
        contentKeySet.add( overwritingRCC.getContent().getKey() );
        contentSet.add( overwritingRCC.getContent() );

        multipleRelatedContentByContentKey.put( overwritingRCC.getContent().getKey(), overwritingRCC );
        relatedChildrenByVersionKey.put( overwritingRCC.getParentVersionKey(), overwritingRCC );

        List<RelatedChildContent> children = rootRelatedChildren.get( overwritingRCC.getParentVersionKey() );
        if ( children == null )
        {
            children = new ArrayList<RelatedChildContent>();
            rootRelatedChildren.put( overwritingRCC.getParentVersionKey(), children );
        }
        if ( !children.contains( overwritingRCC ) )
        {
            children.add( overwritingRCC );
        }
        else
        {
            for ( int i = 0; i < children.size(); i++ )
            {
                if ( overwritingRCC.equals( children.get( i ) ) )
                {
                    children.set( i, overwritingRCC );
                }
            }
        }
    }

    public void overwrite( RelatedContentResultSet set )
    {
        Preconditions.checkArgument( set instanceof RelatedContentResultSetImpl,
                                     "Given set must be of " + RelatedContentResultSetImpl.class.getSimpleName() );

        RelatedContentResultSetImpl setImpl = (RelatedContentResultSetImpl) set;

        contentKeySet.addAll( setImpl.getContentKeys() );
        contentSet.addAll( setImpl.getDinstinctSetOfContent() );

        // multiple related content by contentKey
        for ( RelatedContent relatedContent : setImpl.multipleRelatedContentByContentKey.values() )
        {
            if ( !this.multipleRelatedContentByContentKey.containsEntry( relatedContent.getContent().getKey(), relatedContent ) )
            {
                this.multipleRelatedContentByContentKey.put( relatedContent.getContent().getKey(), relatedContent );
            }
        }

        // related children by versionKey
        for ( RelatedChildContent relatedChildContent : setImpl.relatedChildrenByVersionKey.values() )
        {
            this.relatedChildrenByVersionKey.put( relatedChildContent.getParentVersionKey(), relatedChildContent );
        }

        // root related parents
        for ( RelatedParentContent overwritingRPC : setImpl.doGetAllRootRelatedParents() )
        {
            List<RelatedParentContent> parents = rootRelatedParents.get( overwritingRPC.getChildContentKey() );
            if ( parents == null )
            {
                parents = new ArrayList<RelatedParentContent>();
                rootRelatedParents.put( overwritingRPC.getChildContentKey(), parents );
            }
            if ( !parents.contains( overwritingRPC ) )
            {
                parents.add( overwritingRPC );
            }
        }

        // root related children
        for ( RelatedChildContent overwritingRCC : setImpl.doGetAllRootRelatedChildren() )
        {
            List<RelatedChildContent> children = rootRelatedChildren.get( overwritingRCC.getParentVersionKey() );
            if ( children == null )
            {
                children = new ArrayList<RelatedChildContent>();
                rootRelatedChildren.put( overwritingRCC.getParentVersionKey(), children );
            }
            if ( !children.contains( overwritingRCC ) )
            {
                children.add( overwritingRCC );
            }
        }
    }

    public void retainRelatedRootChildren( ContentVersionKey parent, Collection<ContentKey> children )
    {
        List<RelatedChildContent> relatedChildContents = rootRelatedChildren.get( parent );
        if ( relatedChildContents == null )
        {
            return;
        }

        List<RelatedChildContent> rcContentsToRemove = new ArrayList<RelatedChildContent>();
        for ( RelatedChildContent relatedChildContent : relatedChildContents )
        {
            if ( !children.contains( relatedChildContent.getContent().getKey() ) )
            {
                rcContentsToRemove.add( relatedChildContent );
            }
        }

        relatedChildContents.removeAll( rcContentsToRemove );

        doRemoveRelatedChildContent( parent, rcContentsToRemove );
    }

    private void doRemoveRelatedChildContent( ContentVersionKey parent, Collection<RelatedChildContent> rcContentsToRemove )
    {
        relatedChildrenByVersionKey.removeAll( parent );

        for ( RelatedChildContent rccToRemove : rcContentsToRemove )
        {
            doRemoveFromRelatedContentByContentKey( rccToRemove );
        }

        contentKeySet.retainAll( multipleRelatedContentByContentKey.keys() );

        List<ContentEntity> contentToRemove = new ArrayList<ContentEntity>();
        for ( ContentEntity content : contentSet )
        {
            if ( !contentKeySet.contains( content.getKey() ) )
            {
                contentToRemove.add( content );
            }
        }
        contentSet.removeAll( contentToRemove );

        for ( RelatedChildContent rccToRemove : rcContentsToRemove )
        {
            final ContentVersionEntity version = rccToRemove.getContent().getMainVersion();
            final Collection<RelatedChildContent> versionsChildren = relatedChildrenByVersionKey.get( version.getKey() );
            if ( versionsChildren == null || versionsChildren.isEmpty() )
            {
                continue;
            }

            doRemoveRelatedChildContent( version.getKey(), Lists.newArrayList( versionsChildren ) );
        }
    }

    private void doRemoveFromRelatedContentByContentKey( RelatedChildContent rccToRemove )
    {
        List<RelatedChildContent> rccToRemoveFrom_relatedContentByContentKey = new ArrayList<RelatedChildContent>();
        for ( RelatedContent rc : multipleRelatedContentByContentKey.values() )
        {
            if ( rc instanceof RelatedChildContent )
            {
                RelatedChildContent rcc = (RelatedChildContent) rc;
                if ( rcc.getParentVersionKey().equals( rccToRemove.getParentVersionKey() ) &&
                    rcc.getContent().equals( rccToRemove.getContent() ) )
                {
                    rccToRemoveFrom_relatedContentByContentKey.add( rcc );
                }
            }
        }
        for ( RelatedChildContent itemToRemove : rccToRemoveFrom_relatedContentByContentKey )
        {
            multipleRelatedContentByContentKey.remove( itemToRemove.getContent().getKey(), itemToRemove );
        }
    }

    private Iterable<RelatedParentContent> doGetAllRootRelatedParents()
    {
        List<RelatedParentContent> allRootRelatedParents = new ArrayList<RelatedParentContent>();
        for ( List<RelatedParentContent> list : rootRelatedParents.values() )
        {
            allRootRelatedParents.addAll( list );
        }
        return allRootRelatedParents;
    }

    private Iterable<RelatedChildContent> doGetAllRootRelatedChildren()
    {
        List<RelatedChildContent> allRootRelatedChildren = new ArrayList<RelatedChildContent>();
        for ( List<RelatedChildContent> list : rootRelatedChildren.values() )
        {
            allRootRelatedChildren.addAll( list );
        }
        return allRootRelatedChildren;
    }
}
