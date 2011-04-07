/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.Date;

import org.joda.time.DateTime;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contenttype.CtyImportConfig;
import com.enonic.cms.domain.content.imports.ImportDataEntry;
import com.enonic.cms.domain.content.imports.ImportResult;
import com.enonic.cms.domain.security.user.UserEntity;


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
