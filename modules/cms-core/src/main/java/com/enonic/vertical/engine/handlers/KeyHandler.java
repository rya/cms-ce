/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class KeyHandler
    extends HibernateDaoSupport
{
    private final static String KEY_UPDATE =
            "UPDATE tKey SET key_lLastKey = key_lLastKey + 1 WHERE key_sTableName = :table";

    private final static String KEY_SELECT =
            "SELECT key_lLastKey FROM tKey WHERE key_sTableName = :table";

    private final static String KEY_INSERT =
            "INSERT INTO tKey (key_sTableName, key_lLastKey) VALUES (:table, :key)";

    private boolean updateNextKey(final String tableName)
    {
        final Query query = getSession().createSQLQuery(KEY_UPDATE);
        query.setString("table", tableName.toLowerCase());
        return query.executeUpdate() > 0;
    }

    private int selectCurrentKey(final String tableName)
    {
        final Query query = getSession().createSQLQuery(KEY_SELECT);
        query.setString("table", tableName.toLowerCase());
        return ((Number)query.uniqueResult()).intValue();
    }

    private int insertCurrentKey(final String tableName, final int key)
    {
        final Query query = getSession().createSQLQuery(KEY_INSERT);
        query.setString("table", tableName.toLowerCase());
        query.setInteger("key", key);
        query.executeUpdate();

        return key;
    }

    public int generateNextKeySafe( final String tableName )
    {
        if (updateNextKey(tableName)) {
            return selectCurrentKey(tableName);
        } else {
            return insertCurrentKey(tableName, 0);
        }
    }
}
