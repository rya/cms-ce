/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;

public final class GrpGrpMembershipTable
    extends Table
{
    private static final GrpGrpMembershipTable GrpGrpMembership = new GrpGrpMembershipTable( "tGrpGrpMembership", "null", "null" );

    public ForeignKeyColumn ggm_grp_hKey =
        new ForeignKeyColumn( "ggm_grp_hKey", "null", true, true, Constants.COLUMN_CHAR, null, "tGroup", "grp_hKey", false, -1 );

    public ForeignKeyColumn ggm_mbr_grp_hKey =
        new ForeignKeyColumn( "ggm_mbr_grp_hKey", "null", true, true, Constants.COLUMN_CHAR, null, "tGroup", "grp_hKey", false, -1 );

    private GrpGrpMembershipTable( String tableName, String elementName, String parentName )
    {
        super( tableName, elementName, parentName );
        addColumn( ggm_grp_hKey );
        addColumn( ggm_mbr_grp_hKey );
    }

    public static GrpGrpMembershipTable getInstance()
    {
        return GrpGrpMembership;
    }

}