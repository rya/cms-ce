/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.dbmodel;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.View;

public final class SectionView
    extends View
{
    public Column mei_lKey = new Column( "mei_lKey", "@menuitemkey", Constants.COLUMN_INTEGER );

    public Column hack_key = new Column( "hack_key", "@key", Constants.COLUMN_INTEGER );

    public Column mei_men_lKey = new Column( "mei_men_lKey", "@menukey", Constants.COLUMN_INTEGER );

    public Column mei_lParent = new Column( "mei_lParent", "@supersectionkey", Constants.COLUMN_INTEGER );

    public Column mei_sName = new Column( "mei_sName", "@name", Constants.COLUMN_VARCHAR );

    public Column mei_sDescription = new Column( "mei_sDescription", "description", Constants.COLUMN_VARCHAR );

    public Column mei_bOrderedSection = new Column( "mei_bOrderedSection", "@ordered", Constants.COLUMN_BOOLEAN );

    public Column usr_hOwner = new Column( "usr_hOwner", "owner/@key", Constants.COLUMN_CHAR );

    public Column usr_sOwnerUID = new Column( "usr_sOwnerUID", "owner/@uid", Constants.COLUMN_VARCHAR );

    public Column usr_sOwnerName = new Column( "usr_sOwnerName", "owner", Constants.COLUMN_VARCHAR );

    public Column usr_hModifier = new Column( "usr_hModifier", "modifier/@key", Constants.COLUMN_CHAR );

    public Column usr_sModifierUID = new Column( "usr_sModifierUID", "modifier/@uid", Constants.COLUMN_VARCHAR );

    public Column usr_sModifierName = new Column( "usr_sModifierName", "modifier", Constants.COLUMN_VARCHAR );

    public Column mei_dteTimestamp = new Column( "mei_dteTimestamp", "@timestamp", Constants.COLUMN_CURRENT_TIMESTAMP );

    private final static String SQL = "select mei_lkey, mei_lkey as hack_key, mei_men_lKey, " +
        "(select mi2.mei_lkey from tMenuItem mi2 where mi2.mei_lkey = mi1.mei_lparent and mi2.mei_bSection = 1) as mei_lParent, " +
        "mei_sName, mei_sDescription, mei_bOrderedSection, mei_usr_hOwner as usr_hOwner, " +
        "o.usr_sUID as usr_sOwnerUID, o.usr_sFullName as usr_sOwnerName, " +
        "mei_usr_hModifier as usr_hModifier, m.usr_sUID as usr_sModifierUID, m.usr_sFullName as usr_sModifierName, mei_dteTimestamp from tMenuItem mi1 " +
        "join tUser o on mei_usr_hOwner = o.usr_hKey join tUser m on mei_usr_hModifier = m.usr_hKey " + "where mei_bSection = 1";

    private static final SectionView Section = new SectionView( "vSection" );

    private SectionView( String tableName )
    {
        super( tableName, "section", "sections", SQL, 13 );
        addColumn( mei_lKey );
        addColumn( hack_key );
        addColumn( mei_men_lKey );
        addColumn( mei_lParent );
        addColumn( mei_sName );
        addColumn( mei_sDescription );
        addColumn( mei_bOrderedSection );
        addColumn( usr_hOwner );
        addColumn( usr_sOwnerUID );
        addColumn( usr_sOwnerName );
        addColumn( usr_hModifier );
        addColumn( usr_sModifierUID );
        addColumn( usr_sModifierName );
        addColumn( mei_dteTimestamp );
    }

    public static SectionView getInstance()
    {
        return Section;
    }

}