/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.List;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.security.user.UserEntity;

public interface ImportService
{
    boolean importData( ImportDataReader importDataReader, ImportJob importJob );

    boolean importDataWithoutRequiresNewPropagation( ImportDataReader importDataReader, ImportJob importJob );

    void archiveContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult );

    void archiveContentWithoutRequiresNewPropagation( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult );

    void deleteContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult );

    void deleteContentWithoutRequiresNewPropagation( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult );
}
