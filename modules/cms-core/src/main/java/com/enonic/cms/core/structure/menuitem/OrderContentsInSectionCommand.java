package com.enonic.cms.core.structure.menuitem;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentKey;

public class OrderContentsInSectionCommand
{
    private List<ContentKey> wantedOrder = new ArrayList<ContentKey>();

    OrderContentsInSectionCommand()
    {
    }

    public List<ContentKey> getWantedOrder()
    {
        return wantedOrder;
    }

    public void addContent( ContentKey content )
    {
        this.wantedOrder.add( content );
    }

    public void setWantedOrder( List<ContentKey> wantedOrder )
    {
        this.wantedOrder = wantedOrder;
    }
}
