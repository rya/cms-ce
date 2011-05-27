/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import com.enonic.cms.api.Version;
import com.enonic.cms.core.jaxrs.freemarker.FreemarkerModel;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Component
public final class WelcomeResource
{
    private SiteService siteService;

    @Autowired
    public void setSiteService( SiteService siteService )
    {
        this.siteService = siteService;
    }

    private Map<String, Integer> createSiteMap()
    {
        final HashMap<String, Integer> siteMap = new HashMap<String, Integer>();
        final List<SiteEntity> sites =  this.siteService.findAll();

        for ( final SiteEntity site : sites )
        {
            siteMap.put( site.getName(), site.getKey().integerValue() );
        }

        return siteMap;
    }

    @GET
    public FreemarkerModel handleGet(final @Context UriInfo info)
    {
        final String baseUrl = info.getBaseUri().toString();

        return FreemarkerModel.create("welcomePage.ftl")
                .put("versionTitle", Version.getTitle())
                .put( "versionTitleVersion", Version.getTitleAndVersion() )
                .put( "baseUrl", baseUrl )
                .put("sites", createSiteMap());
    }
}
