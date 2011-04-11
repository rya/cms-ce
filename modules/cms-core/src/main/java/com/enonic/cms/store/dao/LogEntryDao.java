/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import com.enonic.cms.core.log.LogEntryEntity;
import com.enonic.cms.core.log.LogEntryKey;
import com.enonic.cms.core.log.LogEntrySpecification;

import java.util.List;


public interface LogEntryDao
    extends EntityDao<LogEntryEntity>
{

    List<LogEntryKey> findBySpecification( LogEntrySpecification specification, String orderBy );

    LogEntryEntity findByKey( LogEntryKey key );


}
