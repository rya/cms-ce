/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass;

import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.resolver.ForceResolverValueService;
import com.enonic.cms.core.resource.ResourceService;

import com.enonic.cms.core.resolver.CacheResolverValueService;
import com.enonic.cms.core.resolver.ScriptResolverService;

import com.enonic.cms.domain.resolver.ForcedResolverValueLifetimeSettings;
import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resolver.ScriptResolverResult;
import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;

/**
 * Created by rmy - Date: Mar 31, 2009
 */
public class DeviceClassResolverServiceImpl
    implements DeviceClassResolverService
{

    private ResourceService resourceService;

    public static final String BYPASS_CACHED_DEVICE_CLASS = "bypassCachedDeviceClass";

    public final static String DEVICE_CLASS_FORCED_BASE_NAME = "ForceDeviceClass";

    public static final String DEVICE_CLASS_CACHE_BASE_NAME = "DeviceClass";

    public static final String DEFAULT_DEVICE_CLASS = "Unknown";

    public final static int DEVICE_CLASS_RESOLVER_SCRIPT_NOT_SET_TIMESTAMP = 0;

    private ScriptResolverService deviceClassScriptResolver;

    private ForceResolverValueService forceResolverValueService;

    private CacheResolverValueService cacheResolverValueService;

    public String getDeviceClass( ResolverContext context )
    {
        if ( !deviceClassificationEnabled( context ) )
        {
            return null;
        }

        String forcedDeviceClass = resolveForcedDeviceClass( context );

        if ( StringUtils.isNotEmpty( forcedDeviceClass ) )
        {
            return forcedDeviceClass;
        }

        ResourceFile deviceClassResolverScript = getDeviceClassResolverResourceFile( context.getSite() );

        String cachedDeviceClass = resolveCachedDeviceClass( context, deviceClassResolverScript );

        if ( StringUtils.isNotEmpty( cachedDeviceClass ) )
        {
            return cachedDeviceClass;
        }

        String resolvedDeviceClass = resolveDeviceClassFromScript( context, deviceClassResolverScript );

        if ( StringUtils.isEmpty( resolvedDeviceClass ) )
        {
            return DEFAULT_DEVICE_CLASS;
        }

        cacheResolverValueService.setCachedResolverValue( context, resolvedDeviceClass,
                                                          createDeviceClassCacheKey( context, deviceClassResolverScript ) );

        return resolvedDeviceClass;
    }

    private boolean deviceClassificationEnabled( ResolverContext context )
    {
        return context.getSite().isDeviceClassificationEnabled();
    }

    private ResourceFile getDeviceClassResolverResourceFile( SiteEntity site )
    {
        ResourceKey deviceClassResolverResourceKey = site.getDeviceClassResolver();

        if ( deviceClassResolverResourceKey == null )
        {
            return null;
        }

        return getResolverScript( deviceClassResolverResourceKey );
    }

    private String resolveForcedDeviceClass( ResolverContext context )
    {
        String forcedDeviceClassKey = createForcedDeviceClassKey( context.getSite() );

        return forceResolverValueService.getForcedResolverValue( context, forcedDeviceClassKey );
    }

    private String resolveCachedDeviceClass( ResolverContext context, ResourceFile deviceClassResourceFile )
    {
        String deviceClassCacheKey = createDeviceClassCacheKey( context, deviceClassResourceFile );

        return cacheResolverValueService.getCachedResolverValue( context, deviceClassCacheKey );
    }

    private String resolveDeviceClassFromScript( ResolverContext context, ResourceFile deviceClassResolverScript )
    {
        if ( deviceClassResolverScript == null )
        {
            return null;
        }

        ScriptResolverResult scriptResolverResult = deviceClassScriptResolver.resolveValue( context, deviceClassResolverScript );

        return (String) scriptResolverResult.getResolverReturnValues().get( DeviceClassXsltScriptResolver.DEVICE_CLASS_RETURN_VALUE_KEY );
    }

    public void setForcedDeviceClass( ResolverContext context, HttpServletResponse response, ForcedResolverValueLifetimeSettings setting,
                                      String deviceClass )
    {
        resetDeviceClass( context, response );

        forceResolverValueService.setForcedValue( context, response, createForcedValueKey( context.getSite() ), setting, deviceClass );
    }

    private String createForcedDeviceClassKey( SiteEntity site )
    {
        return DEVICE_CLASS_FORCED_BASE_NAME + site.getKey();
    }

    private String createDeviceClassCacheKey( ResolverContext context, ResourceFile resourceFile )
    {
        return createSiteSessionBaseName( context.getSite() ) + getResourceFileTimeStamp( resourceFile );
    }

    private long getResourceFileTimeStamp( ResourceFile deviceClassResolverScript )
    {
        Calendar lastModified = null;

        if ( deviceClassResolverScript != null )
        {
            lastModified = deviceClassResolverScript.getLastModified();
        }

        return lastModified == null ? DEVICE_CLASS_RESOLVER_SCRIPT_NOT_SET_TIMESTAMP : lastModified.getTimeInMillis();
    }

    protected ResourceFile getResolverScript( ResourceKey resourceKey )
    {
        return resourceService.getResourceFile( resourceKey );
    }

    public void resetDeviceClass( ResolverContext context, HttpServletResponse response )
    {
        forceResolverValueService.clearForcedValue( context, response, createForcedValueKey( context.getSite() ) );
        cacheResolverValueService.clearCachedResolverValue( context, createSiteSessionBaseName( context.getSite() ) );
    }

    private String createSiteSessionBaseName( SiteEntity site )
    {
        return DEVICE_CLASS_CACHE_BASE_NAME + site.getKey().toInt();
    }

    public void setDeviceClassScriptResolver( ScriptResolverService deviceClassScriptResolver )
    {
        this.deviceClassScriptResolver = deviceClassScriptResolver;
    }

    protected String createForcedValueKey( SiteEntity site )
    {
        return DEVICE_CLASS_FORCED_BASE_NAME + site.getKey();
    }

    public void setForceResolverValueService( ForceResolverValueService forceResolverValueService )
    {
        this.forceResolverValueService = forceResolverValueService;
    }

    public void setCacheResolverValueService( CacheResolverValueService cacheResolverValueService )
    {
        this.cacheResolverValueService = cacheResolverValueService;
    }

    @Autowired
    public void setResourceService( ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

}


