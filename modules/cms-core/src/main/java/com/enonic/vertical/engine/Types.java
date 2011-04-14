/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

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

    // Prevent instantiation

    private Types()
    {
    }
}