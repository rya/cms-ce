/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import com.enonic.vertical.engine.dbmodel.MenuTable;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.handlers.CommonHandler;

public class MenuElementProcessor
    implements ElementProcessor
{

    CommonHandler commonHandler;

    public MenuElementProcessor( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void process( Element elem )
    {
        String intStr = elem.getAttribute( "menukey" );
        if ( intStr != null && intStr.length() > 0 )
        {
            int menuKey = Integer.parseInt( intStr );
            Column[] selectColumns = new Column[]{MenuTable.INSTANCE.men_sName};
            Column[] whereColumns = new Column[]{MenuTable.INSTANCE.men_lKey};
            StringBuffer sql = XDG.generateSelectSQL( MenuTable.INSTANCE, selectColumns, false, whereColumns );
            String name = commonHandler.getString( sql.toString(), new Object[]{new Integer( menuKey )} );
            elem.setAttribute( "menuname", name );
        }
    }
}