/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.util.HashMap;

import com.enonic.esl.sql.model.Table;
import com.enonic.vertical.engine.dbmodel.ContentVersionView;
import com.enonic.vertical.engine.dbmodel.ContentView;
import com.enonic.vertical.engine.dbmodel.SectionContentView;
import com.enonic.vertical.engine.dbmodel.VerticalDatabase;

/**
 * Maps all the main database tables to integer numbers and allows lookups by the number
 */
public class Types
{

    public final static int BINARYDATA = 0;

    public final static int CONTENT = 1;

    public final static int CONTENTHANDLER = 2;

    public final static int CONTENTTYPE = 3;

    public final static int CONTENTOBJECT = 4;

    public final static int LOGENTRY = 5;

    public final static int MENU = 6;

    public final static int MENUITEM = 7;

    public final static int PAGE = 8;

    public final static int PAGETEMPLATE = 9;

    //public final static int RESOURCE	 		=  10;

    public final static int USER = 11;

    public final static int SECTION = 12;

    public final static int CATEGORY = 13;

    public final static int CONTENTVIEW = 14;

    public final static int SECTIONCONTENT = 16;

    //public final static int SITE			 	=  17;

    public final static int UNIT = 18;

    public final static int DOMAIN = 19;

    public final static int GROUP = 20;

    //public final static int GROUPVIEW		 	=  21;
    // public final static int MENUITEMVIEW = 22;

    //public final static int RESOURCEVIEW 		=  25;

    public final static int CONTENTVERSIONVIEW = 26;
    //public final static int MENUDETAILS			=  27;
    //public final static int MENUITEMALIAS		=  28;
    //public final static int MENUITEMSHORTCUT    =  29;

    private final static HashMap<Integer, Table> tables = new HashMap<Integer, Table>();

    static
    {
        VerticalDatabase db = VerticalDatabase.getInstance();
        tables.put( BINARYDATA, db.tBinaryData );
        tables.put( CONTENT, db.tContent );
        tables.put( CONTENTHANDLER, db.tContentHandler );
        tables.put( CONTENTTYPE, db.tContentType );
        tables.put( CONTENTOBJECT, db.tContentObject );
        tables.put( LOGENTRY, db.tLogEntry );
        tables.put( MENU, db.tMenu );
        tables.put( MENUITEM, db.tMenuItem );
        tables.put( PAGE, db.tPage );
        tables.put( PAGETEMPLATE, db.tPageTemplate );
        tables.put( USER, db.tUser );
        tables.put( SECTION, db.tMenuItem );
        tables.put( CATEGORY, db.tCategory );
        tables.put( CONTENTVIEW, ContentView.getInstance() );
        tables.put( SECTIONCONTENT, SectionContentView.getInstance() );
        tables.put( UNIT, db.tUnit );
        tables.put( DOMAIN, db.tDomain );
        tables.put( GROUP, db.tGroup );
        tables.put( CONTENTVERSIONVIEW, ContentVersionView.getInstance() );
    }

    // Prevent instantiation

    private Types()
    {
    }
}