/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.Date;

import com.enonic.cms.core.content.category.CategoryEntity;
import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.security.user.UserEntity;


public interface ImportJob
{
    ImportResult start();

    CategoryEntity getCategoryToImportTo();

    DateTime getDefaultPublishFrom();

    DateTime getDefaultPublishTo();

    UserEntity getImporter();

    ImportResult getImportResult();

    void registerImportedContent( ContentKey contentKey );

    CtyImportConfig getImportConfig();

    ContentKey resolveExistingContentBySyncValue( final ImportDataEntry importDataEntry );

    UserEntity getAssignee();

    String getAssignmentDescription();

    Date getAssignmentDueDate();
}
