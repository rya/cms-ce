/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.structure.portlet.PortletEntity;

public class PageParameter
{

    private Integer parameterkey;

    private List<PortletEntity> portlets = new ArrayList<PortletEntity>();

    public PageParameter( Integer parameterkey )
    {
        this.parameterkey = parameterkey;
    }

    public void addPortlet( PortletEntity portlet )
    {
        portlets.add( portlet );
    }

    public Integer getParameterkey()
    {
        return parameterkey;
    }

    public List<PortletEntity> getPortlets()
    {
        return portlets;
    }
}
