/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.io.IOException;
import java.text.ParseException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.resolver.ForcedResolverValueLifetimeSettings;
import com.enonic.cms.core.structure.SiteEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.resolver.deviceclass.DeviceClassResolverService;
import com.enonic.cms.core.resolver.locale.LocaleResolverService;
import com.enonic.cms.core.service.UserServicesService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.resolver.ResolverContext;

/**
 * Created by rmy - Date: Apr 3, 2009
 */
@Controller
@RequestMapping(value = "/site/**/_services/portal")
public class PortalHandlerController
        extends AbstractUserServicesHandlerController
{

    private final static String FORM_ITEM_DEVICE_CLASS = "deviceclass";

    private final static String FORM_ITEM_LOCALE = "locale";

    private static final String FORCE_VALUE_SETTING_KEY = "lifetime";

    @Resource
    private DeviceClassResolverService deviceClassResolverService;

    @Resource
    private LocaleResolverService localeResolverService;

    private enum PortalOperation
    {
        forcedeviceclass,
        resetdeviceclass,
        forcelocale,
        resetlocale
    }

    public PortalHandlerController()
    {
        super();
    }

    @RequestMapping(value = "/forcedeviceclass", method = RequestMethod.GET)
    public void forcedeviceclass( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/resetdeviceclass", method = RequestMethod.GET)
    public void resetdeviceclass( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/forcelocale", method = RequestMethod.GET)
    public void forcelocale( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @RequestMapping(value = "/resetlocale", method = RequestMethod.GET)
    public void resetlocale( HttpServletRequest request, HttpServletResponse response )
            throws Exception
    {
        handleRequest( request, response );
    }

    @Override
    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {

        PortalOperation portalOperation = null;
        try
        {
            portalOperation = PortalOperation.valueOf( StringUtils.lowerCase( operation ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new VerticalUserServicesException( "Operation: " + operation + " not supported in service " + " portal" );
        }

        ResolverContext resolverContext = new ResolverContext( request, getSite( siteKey ) );

        switch ( portalOperation )
        {
            case forcedeviceclass:
                handleForceDeviceClass( resolverContext, response, formItems );
                break;
            case resetdeviceclass:
                handleResetDeviceClass( resolverContext, response );
                break;
            case forcelocale:
                handleForceLocale( resolverContext, response, formItems );
                break;
            case resetlocale:
                handleResetLocale( resolverContext, response );
                break;
        }

        redirectToPage( request, response, formItems );
    }

    private void handleForceDeviceClass( ResolverContext context, HttpServletResponse response, ExtendedMap formItems )
    {

        ForcedResolverValueLifetimeSettings forcedDeviceClassSetting =
            getForcedDeviceClassSetting( formItems, ForcedResolverValueLifetimeSettings.permanent );

        String deviceClass = formItems.getString( FORM_ITEM_DEVICE_CLASS );

        deviceClassResolverService.setForcedDeviceClass( context, response, forcedDeviceClassSetting, deviceClass );
    }

    private void handleResetDeviceClass( ResolverContext context, HttpServletResponse response )
    {
        deviceClassResolverService.resetDeviceClass( context, response );
    }

    private void handleForceLocale( ResolverContext context, HttpServletResponse response, ExtendedMap formItems )
    {

        ForcedResolverValueLifetimeSettings forceLocaleLifeTimeSetting =
            getForcedDeviceClassSetting( formItems, ForcedResolverValueLifetimeSettings.permanent );

        String localeString = formItems.getString( FORM_ITEM_LOCALE );

        localeResolverService.setForcedLocale( context, response, forceLocaleLifeTimeSetting, localeString );
    }

    private void handleResetLocale( ResolverContext context, HttpServletResponse response )
    {
        localeResolverService.resetLocale( context, response );
    }

    private ForcedResolverValueLifetimeSettings getForcedDeviceClassSetting( ExtendedMap formItems,
                                                                             ForcedResolverValueLifetimeSettings defaultSetting )
    {

        String forcedDeviceClassParameter = formItems.getString( FORCE_VALUE_SETTING_KEY, defaultSetting.name() );

        ForcedResolverValueLifetimeSettings forcedDeviceClassSetting;
        try
        {
            forcedDeviceClassSetting = ForcedResolverValueLifetimeSettings.valueOf( StringUtils.lowerCase( forcedDeviceClassParameter ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new VerticalUserServicesException( "Force deviceclass setting is invalid: " + forcedDeviceClassParameter );
        }
        return forcedDeviceClassSetting;
    }

    private SiteEntity getSite( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return site;
    }
}
