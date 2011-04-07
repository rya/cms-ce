/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.HashSet;
import java.util.Set;

import org.jdom.Element;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.resultset.RelatedChildContent;
import com.enonic.cms.domain.content.resultset.RelatedContent;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedParentContent;


public class RelatedContentKeysXmlCreator
{
    private RelatedContentResultSet relatedContents;

    public RelatedContentKeysXmlCreator( RelatedContentResultSet relatedContents )
    {
        this.relatedContents = relatedContents;
    }

    public Element createForContent( final ContentVersionEntity contentVersion )
    {
        int addCount = 0;

        final Element rcKeysElement = new Element( "relatedcontentkeys" );

        HashSet<ContentEntity> contentAlreadyAdded = new HashSet<ContentEntity>();
        for ( RelatedParentContent relatedParent : relatedContents.getRootRelatedParents( contentVersion.getContent() ) )
        {
            Element relatedContentKeyElement = doCreateRelatedContentKeyElement( relatedParent.getContent(), "-1", contentAlreadyAdded );
            if ( relatedContentKeyElement != null )
            {
                rcKeysElement.addContent( relatedContentKeyElement );
                addCount++;
            }
        }

        contentAlreadyAdded.clear();
        for ( RelatedChildContent relatedChild : relatedContents.getRootRelatedChildren( contentVersion ) )
        {
            Element relatedContentKeyElement = doCreateRelatedContentKeyElement( relatedChild.getContent(), "1", contentAlreadyAdded );
            if ( relatedContentKeyElement != null )
            {
                rcKeysElement.addContent( relatedContentKeyElement );
                addCount++;
            }
        }

        rcKeysElement.setAttribute( "count", Integer.toString( addCount ) );

        return rcKeysElement;
    }

    public Element createForRelatedContent( final RelatedContent relatedContent )
    {
        int addCount = 0;

        final Element rcKeysEl = new Element( "relatedcontentkeys" );

        if ( relatedContent instanceof RelatedParentContent )
        {
            RelatedParentContent relatedContentAsParent = (RelatedParentContent) relatedContent;
            addCount += doAddRelatedContentKeys( relatedContentAsParent.getRelatedParents(), "-1", rcKeysEl );
            addCount += doAddRelatedContentKeys( relatedContentAsParent.getRelatedChildren(), "1", rcKeysEl );
        }
        else
        {
            RelatedChildContent relatedChildContent = (RelatedChildContent) relatedContent;
            addCount += doAddRelatedContentKeys( relatedChildContent.getRelatedChildren(), "1", rcKeysEl );
        }

        rcKeysEl.setAttribute( "count", Integer.toString( addCount ) );
        return rcKeysEl;
    }

    private int doAddRelatedContentKeys( Iterable<RelatedContent> related, String level, Element parentEl )
    {
        int addCount = 0;
        HashSet<ContentEntity> contentAlreadyAdded = new HashSet<ContentEntity>();
        for ( RelatedContent relatedChild : related )
        {
            Element relatedContentKeyElement = doCreateRelatedContentKeyElement( relatedChild.getContent(), level, contentAlreadyAdded );
            if ( relatedContentKeyElement != null )
            {
                parentEl.addContent( relatedContentKeyElement );
                addCount++;
            }
        }
        return addCount;
    }

    private Element doCreateRelatedContentKeyElement( final ContentEntity content, final String level,
                                                      Set<ContentEntity> contentAlreadyAdded )
    {
        if ( !contentAlreadyAdded.contains( content ) )
        {
            contentAlreadyAdded.add( content );
            final Element element = new Element( "relatedcontentkey" );
            element.setAttribute( "key", content.getKey().toString() );
            element.setAttribute( "versionkey", content.getMainVersion().getKey().toString() );
            element.setAttribute( "level", level );
            element.setAttribute( "contenttype", content.getContentType().getName() );
            return element;
        }
        else
        {
            return null;
        }
    }
}
