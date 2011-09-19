/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.processors;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.dbmodel.VerticalDatabase;
import com.enonic.vertical.engine.handlers.CommonHandler;

public class UserElementProcessor
    implements ElementProcessor
{

    private CommonHandler commonHandler;

    private VerticalDatabase db;

    private HashMap userMap = new HashMap();

    public UserElementProcessor( CommonHandler commonHandler, VerticalDatabase db )
    {
        this.commonHandler = commonHandler;
        this.db = db;
    }

    public void process( Element elem )
    {
        String userKey = elem.getAttribute( "userkey" );
        String[] strings;
        if ( userMap.containsKey( userKey ) )
        {
            strings = (String[]) userMap.get( userKey );
        }
        else
        {
            Column[] selectColumns = new Column[]{db.tUser.usr_sUID, db.tUser.usr_sFullName};
            Column[] whereColumns = new Column[]{db.tUser.usr_hKey};
            StringBuffer sql = XDG.generateSelectSQL( db.tUser, selectColumns, false, whereColumns );
            strings = commonHandler.getStrings( sql.toString(), new Object[]{userKey} );
            userMap.put( userKey, strings );
        }

        elem.setAttribute( "uid", strings[0] );
        elem.setAttribute( "username", strings[1] );
    }
}