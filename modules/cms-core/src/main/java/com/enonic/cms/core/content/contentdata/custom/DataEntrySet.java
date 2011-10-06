/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.contenttype.CtySet;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public interface DataEntrySet
    extends DataEntry
{
    void add( DataEntry entry );

    int numberOfEntries();

    List<DataEntry> getEntries();

    DataEntry getEntry( String name );

    DataEntryConfig getInputConfig( String name );

    DataEntryConfig getInputConfigByRelateiveXPath( String relativeXpath );

    CtySet getContentTypeConfig();

    CtySet getConfig();

    Set<ContentKey> resolveRelatedContentKeys();

    List<BinaryDataEntry> getBinaryDataEntryList();

    boolean hasBinaryDataEntry( BinaryDataEntry subject );

    void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas );

    void turnBinaryKeysIntoPlaceHolders( Map<BinaryDataKey, Integer> indexByBinaryDataKey );

    List<GroupDataEntry> getGroupDataSets( final String name );
}
