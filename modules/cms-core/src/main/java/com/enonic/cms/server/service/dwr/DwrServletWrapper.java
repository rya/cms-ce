/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.dwr;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.server.service.admin.ajax.AdminAjaxServiceImpl;
import com.enonic.cms.server.service.admin.ajax.dto.RegionDto;
import com.enonic.cms.server.service.admin.ajax.dto.SynchronizeStatusDto;
import com.enonic.cms.server.service.admin.ajax.dto.UserDto;
import com.enonic.cms.store.dao.PreferenceDao;
import org.directwebremoting.servlet.DwrServlet;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.vhost.VirtualHostHelper;

/**
 * This class implements a servlet that wraps around dwr. It fixes a path problem that is seen when certain virtual hosts are used.
 */
public final class DwrServletWrapper
    extends DwrServlet
{
    private final StringBuilder classes;

    /**
     * Construct the wrapper.
     */
    public DwrServletWrapper()
    {
        this.classes = new StringBuilder();

        addClass(AdminAjaxServiceImpl.class);
        addClass(PreferenceDao.class);
        addClass(SynchronizeStatusDto.class);
        addClass(RegionDto.class);
        addClass(UserDto.class);
    }

    private void addClass(final Class type)
    {
        if (this.classes.length() > 0) {
            this.classes.append(",");
        }
        
        this.classes.append(type.getName());
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( VirtualHostHelper.hasBasePath( req ) )
        {
            String basePath = VirtualHostHelper.getBasePath( req );
            final String servletPath = req.getServletPath().replace( "/admin", basePath );

            HttpServletRequest newReq = new HttpServletRequestWrapper( req )
            {
                public String getServletPath()
                {
                    return servletPath;
                }
            };

            ServletRequestAccessor.setRequest( newReq );
            super.service(newReq, res);
        }
        else
        {
            ServletRequestAccessor.setRequest( req );
            super.service(req, res);
        }
    }

    @Override
    public void init( final ServletConfig config )
        throws ServletException
    {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("debug", "false");
        params.put("classes", this.classes.toString());

        final ServletConfig wrapper = new ServletConfig()
        {
            public String getServletName()
            {
                return config.getServletName();
            }

            public ServletContext getServletContext()
            {
                return config.getServletContext();
            }

            public String getInitParameter(final String name)
            {
                return params.get(name);
            }

            public Enumeration getInitParameterNames()
            {
                return Collections.enumeration(params.keySet());
            }
        };

        super.init(wrapper);
    }
}
