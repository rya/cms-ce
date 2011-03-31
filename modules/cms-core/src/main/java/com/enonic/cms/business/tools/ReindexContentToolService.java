/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.tools;

import java.util.List;


public interface ReindexContentToolService
{

    public void reindexAllContent( List<String> logEntries );
}
