/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;

public class UnitHandler
    extends BaseHandler
{

    // Unit SQL

    private static final Map<String, String> orderByMap;

    static
    {
        orderByMap = new HashMap<String, String>();

        orderByMap.put( "key", "uni_lKey" );
        orderByMap.put( "name", "uni_sName" );
        orderByMap.put( "description", "uni_sDescription" );
        orderByMap.put( "timestamp", "uni_dteTimeStamp" );
    }

}
