/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import java.util.LinkedHashSet;

public class VerticalEventMulticaster
    implements MenuHandlerListener, VerticalEventListener
{

    protected LinkedHashSet<VerticalEventListener> listenerSet = new LinkedHashSet<VerticalEventListener>();

    public VerticalEventMulticaster()
    {
        super();
    }

    public void add( VerticalEventListener l )
    {
        listenerSet.add( l );
    }

    public boolean hasListeners()
    {
        return listenerSet.size() > 0;
    }


    public void createdMenuItem( MenuHandlerEvent e )
    {
        for ( VerticalEventListener l : listenerSet )
        {
            ( (MenuHandlerListener) l ).createdMenuItem( e );
        }
    }

    public void removedMenuItem( MenuHandlerEvent e )
    {
        for ( VerticalEventListener l : listenerSet )
        {
            ( (MenuHandlerListener) l ).removedMenuItem( e );
        }
    }

    public void updatedMenuItem( MenuHandlerEvent e )
    {
        for ( VerticalEventListener l : listenerSet )
        {
            ( (MenuHandlerListener) l ).updatedMenuItem( e );
        }
    }

}
