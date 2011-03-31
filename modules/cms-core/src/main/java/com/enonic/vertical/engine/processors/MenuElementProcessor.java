/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.dbmodel.VerticalDatabase;
import com.enonic.vertical.engine.handlers.CommonHandler;

public class MenuElementProcessor
    implements ElementProcessor
{

    CommonHandler commonHandler;

    VerticalDatabase db;

    public MenuElementProcessor( CommonHandler commonHandler, VerticalDatabase db )
    {
        this.commonHandler = commonHandler;
        this.db = db;
    }

    /**
     * @see com.enonic.vertical.engine.base.ElementProcessor#process(org.w3c.dom.Element)
     */
    public void process( Element elem )
    {
        String intStr = elem.getAttribute( "menukey" );
        if ( intStr != null && intStr.length() > 0 )
        {
            int menuKey = Integer.parseInt( intStr );
            Column[] selectColumns = new Column[]{db.tMenu.men_sName};
            Column[] whereColumns = new Column[]{db.tMenu.men_lKey};
            StringBuffer sql = XDG.generateSelectSQL( db.tMenu, selectColumns, false, whereColumns );
            String name = commonHandler.getString( sql.toString(), new Object[]{new Integer( menuKey )} );
            elem.setAttribute( "menuname", name );
        }
    }
}