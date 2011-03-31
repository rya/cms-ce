/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import org.hibernate.Session;

public interface EntityChangeListener
{
    public void entityInserted( Session session, Object entity );

    public void entityUpdated( Session session, Object entity );

    public void entityDeleted( Session session, Object entity );
}
