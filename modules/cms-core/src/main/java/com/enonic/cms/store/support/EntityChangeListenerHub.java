/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.util.concurrent.CopyOnWriteArrayList;

import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

public final class EntityChangeListenerHub
    implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener
{
    private final static EntityChangeListenerHub INSTANCE = new EntityChangeListenerHub();

    private final CopyOnWriteArrayList<EntityChangeListener> listeners;

    private EntityChangeListenerHub()
    {
        this.listeners = new CopyOnWriteArrayList<EntityChangeListener>();
    }

    public void onPostDelete( PostDeleteEvent event )
    {
        for ( EntityChangeListener listener : this.listeners )
        {
            listener.entityDeleted( event.getSession(), event.getEntity() );
        }
    }

    public void onPostInsert( PostInsertEvent event )
    {
        for ( EntityChangeListener listener : this.listeners )
        {
            listener.entityInserted( event.getSession(), event.getEntity() );
        }
    }

    public void onPostUpdate( PostUpdateEvent event )
    {
        for ( EntityChangeListener listener : this.listeners )
        {
            listener.entityUpdated( event.getSession(), event.getEntity() );
        }
    }

    public void addListener( EntityChangeListener listener )
    {
        this.listeners.add( listener );
    }

    public static EntityChangeListenerHub getInstance()
    {
        return INSTANCE;
    }
}
