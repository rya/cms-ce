/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;

public interface MenuHandlerListener
    extends VerticalEventListener
{

    /**
     * This method should be called whenever a menu is created.
     *
     * @param e The event object that was emitted.
     */
    void createdMenuItem( MenuHandlerEvent e )
        throws VerticalCreateException;

    /**
     * This method should be called whenever a menu is updated.
     *
     * @param e The event object that was emitted.
     */
    void updatedMenuItem( MenuHandlerEvent e )
        throws VerticalUpdateException;

    /**
     * This method should be called whenever a menu is removed.
     *
     * @param e The event object that was emitted.
     */
    void removedMenuItem( MenuHandlerEvent e )
        throws VerticalRemoveException;
}
