package com.enonic.cms.core.structure.menuitem;

import java.util.Comparator;

import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;

public class SectionContentComparatorByOrder
    implements Comparator<SectionContentEntity>
{
    public int compare( SectionContentEntity a, SectionContentEntity b )
    {
        if ( !a.isApproved() && b.isApproved() )
        {
            return -1;
        }
        else if ( !b.isApproved() && a.isApproved() )
        {
            return 1;
        }
        else if ( !a.isApproved() && !b.isApproved() )
        {
            if ( a.getKey().toInt() < b.getKey().toInt() )
            {
                return -1;
            }
            else if ( a.getKey().toInt() == b.getKey().toInt() )
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
        else if ( a.getOrder() < b.getOrder() )
        {
            return -1;
        }
        else if ( a.getOrder() == b.getOrder() )
        {
            if ( a.getKey().toInt() < b.getKey().toInt() )
            {
                return -1;
            }
            else if ( a.getKey().toInt() == b.getKey().toInt() )
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
        else
        {
            return 1;
        }
    }
}
