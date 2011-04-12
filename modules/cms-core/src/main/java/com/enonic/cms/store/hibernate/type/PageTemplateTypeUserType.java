/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.enonic.cms.core.structure.page.template.PageTemplateType;
import org.hibernate.HibernateException;


public class PageTemplateTypeUserType
    extends AbstractBaseUserType
{
    public PageTemplateTypeUserType()
    {
        super( PageTemplateType.class, Types.INTEGER );
    }

    public boolean isMutable()
    {
        return false;
    }

    public Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        int intValue = rs.getInt( names[0] );
        return rs.wasNull() ? null : PageTemplateType.get(intValue);
    }

    public void nullSafeSet( PreparedStatement st, Object value, int index )
        throws HibernateException, SQLException
    {
        if ( value == null )
        {
            st.setNull( index, getSqlType() );
        }
        else
        {
            PageTemplateType pageTemplateType = (PageTemplateType) value;
            st.setInt( index, pageTemplateType.getKey() );
        }
    }
}