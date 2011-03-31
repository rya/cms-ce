/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contenttype;

public interface CtyImportMappingContainer
{
    boolean addMapping( final CtyImportMappingConfig mapping );

    String getName();

    CtyImportConfig getImportConfig();
}
