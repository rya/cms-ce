package com.enonic.cms.core.structure.menuitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;

/**
 * Oct 17, 2010
 */
public class ContentsInSectionOrderer
{
    public int orderSpace;

    private List<ContentKey> wantedOrder;

    private MenuItemEntity section;

    private final List<SectionContentEntity> approvedSectionContentsInWantedOrder = new ArrayList<SectionContentEntity>();

    public ContentsInSectionOrderer( List<ContentKey> wantedOrder, MenuItemEntity section, int orderSpace )
    {
        this.wantedOrder = wantedOrder;
        this.section = section;
        this.orderSpace = orderSpace;

        initializeSectionContentsInWantedOrder();
    }

    private void initializeSectionContentsInWantedOrder()
    {
        final Map<ContentKey, SectionContentEntity> sectionContentsByContentKey = new HashMap<ContentKey, SectionContentEntity>();
        for ( SectionContentEntity sectionContent : section.getSectionContents() )
        {
            sectionContentsByContentKey.put( sectionContent.getContent().getKey(), sectionContent );
        }

        for ( ContentKey contentKey : this.wantedOrder )
        {
            SectionContentEntity currentSC = sectionContentsByContentKey.get( contentKey );
            if ( currentSC != null && currentSC.isApproved() )
            {
                approvedSectionContentsInWantedOrder.add( currentSC );
            }
        }
    }

    public Set<SectionContentEntity> order()
    {
        final Set<SectionContentEntity> changedSectionContent = new HashSet<SectionContentEntity>();

        if ( allHaveSameOrder( approvedSectionContentsInWantedOrder ) )
        {
            resetOrder();
            changedSectionContent.addAll( approvedSectionContentsInWantedOrder );
            return changedSectionContent;
        }

        for ( int i = 0; i < approvedSectionContentsInWantedOrder.size(); i++ )
        {
            final SectionContentEntity currSC = approvedSectionContentsInWantedOrder.get( i );
            final SectionContentEntity prevSC = i > 0 ? approvedSectionContentsInWantedOrder.get( i - 1 ) : null;
            final SectionContentEntity nextSC =
                i < approvedSectionContentsInWantedOrder.size() - 1 ? approvedSectionContentsInWantedOrder.get( i + 1 ) : null;
            final SectionContentEntity nextNextSC =
                i < approvedSectionContentsInWantedOrder.size() - 2 ? approvedSectionContentsInWantedOrder.get( i + 2 ) : null;

            if ( !inOrder( prevSC, currSC, nextSC ) )
            {
                // order no is after next one, must adjust this one
                int order;

                if ( prevSC == null )
                {
                    assert nextSC != null;
                    order = nextSC.getOrder() - orderSpace;
                }
                else if ( nextSC == null )
                {
                    order = prevSC.getOrder() + orderSpace;
                }
                else
                {
                    order = resolveNewOrderBetween( prevSC, nextSC );
                    int spaceBetweenPreviousOrderAndThisOrder = measureSpace( prevSC.getOrder(), order );

                    if ( nextNextSC != null )
                    {
                        int nextNewOrder = resolveNewOrderBetween( currSC, nextNextSC );
                        int spaceBetweenThisOrderAndNextNextOrder = measureSpace( currSC.getOrder(), nextNewOrder );
                        if ( spaceBetweenThisOrderAndNextNextOrder > spaceBetweenPreviousOrderAndThisOrder )
                        {
                            // more space in between the next, so let us skip there..
                            continue;
                        }
                    }
                }

                if ( order != currSC.getOrder() )
                {
                    currSC.setOrder( order );
                    changedSectionContent.add( currSC );
                }
            }
        }

        if ( !validOrderNumbers( approvedSectionContentsInWantedOrder ) )
        {
            resetOrder();
            changedSectionContent.addAll( approvedSectionContentsInWantedOrder );
        }

        return changedSectionContent;
    }

    private int measureSpace( int orderA, int orderB )
    {
        return orderB - orderA;
    }

    private boolean inOrder( final SectionContentEntity prevSC, final SectionContentEntity currSC, final SectionContentEntity nextSC )
    {
        if ( prevSC != null && prevSC.getOrder() >= currSC.getOrder() )
        {
            return false;
        }
        else if ( nextSC != null && nextSC.getOrder() <= currSC.getOrder() )
        {
            return false;
        }

        return true;
    }

    private int resolveNewOrderBetween( SectionContentEntity prevSC, SectionContentEntity nextSC )
    {
        Preconditions.checkNotNull( prevSC );
        Preconditions.checkNotNull( nextSC );

        if ( nextSC.getOrder() <= prevSC.getOrder() )
        {
            return prevSC.getOrder() + orderSpace;
        }
        else
        {
            return resolveNewOrderBetween( prevSC.getOrder(), nextSC.getOrder() );
        }
    }

    private int resolveNewOrderBetween( int orderPrev, int orderNext )
    {
        int space = orderNext - orderPrev;
        int newOrder = orderPrev + ( space / 2 );
        if ( !( newOrder > orderPrev && newOrder < orderNext ) )
        {
            return orderPrev + orderSpace;
        }
        return newOrder;
    }

    private boolean validOrderNumbers( List<SectionContentEntity> sectionContents )
    {
        Integer prevOrderNo = null;
        for ( SectionContentEntity sc : sectionContents )
        {
            if ( prevOrderNo != null )
            {
                if ( sc.getOrder() <= prevOrderNo )
                {
                    return false;
                }
            }

            prevOrderNo = sc.getOrder();
        }

        return true;
    }

    private void resetOrder()
    {
        int prevOrderNo = 0;
        for ( SectionContentEntity sc : approvedSectionContentsInWantedOrder )
        {
            int newOrder = prevOrderNo + orderSpace;
            sc.setOrder( newOrder );
            prevOrderNo = newOrder;
        }
    }

    private boolean allHaveSameOrder( List<SectionContentEntity> list )
    {
        if ( list.isEmpty() )
        {
            return false;
        }
        else if ( list.size() == 1 )
        {
            return false;
        }

        int currentOrder = list.get( 0 ).getOrder();
        for ( int i = 1; i < list.size(); i++ )
        {
            if ( currentOrder != list.get( i ).getOrder() )
            {
                return false;
            }
        }

        return true;
    }
}
