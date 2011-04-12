/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.resource;

import java.util.List;

import com.enonic.cms.core.resource.FileResourceData;
import com.enonic.cms.core.resource.FileResourceName;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.resource.FileResource;

public interface FileResourceService
{
    FileResource getResource( FileResourceName name );

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean createFolder( FileResourceName name );

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean createFile( FileResourceName name, FileResourceData data );

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean deleteResource( FileResourceName name );

    List<FileResourceName> getChildren( FileResourceName name );

    FileResourceData getResourceData( FileResourceName name );

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean setResourceData( FileResourceName name, FileResourceData data );

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean moveResource( FileResourceName from, FileResourceName to );

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    boolean copyResource( FileResourceName from, FileResourceName to );
}
