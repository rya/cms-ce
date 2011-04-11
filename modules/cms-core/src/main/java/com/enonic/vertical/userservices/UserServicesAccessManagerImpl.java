/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.structure.SiteService;

import com.enonic.cms.domain.SiteKey;

public class UserServicesAccessManagerImpl
    implements UserServicesAccessManager
{

    private static enum AccessPermission
    {
        ALLOW,
        DENY,
    }

    private static final String HTTP_SERVICES_ALLOW_PROPERTY = "cms.site.httpServices.allow";

    private static final String HTTP_SERVICES_DENY_PROPERTY = "cms.site.httpServices.deny";

    private static final String ACCESS_RULE_ALL = "*";

    private static final AccessPermission DEFAULT_ACCESS_RULE = AccessPermission.DENY;

    private SitePropertiesService sitePropertiesService;

    private SiteService siteService;

    private ConcurrentMap<SiteKey, ConcurrentMap<String, AccessPermission>> sitesAccessRules;

    public UserServicesAccessManagerImpl()
    {
        sitesAccessRules = new ConcurrentHashMap<SiteKey, ConcurrentMap<String, AccessPermission>>();
    }

    public boolean isOperationAllowed( SiteKey site, String service, String operation )
    {
        siteService.checkSiteExist( site );

        ConcurrentMap<String, AccessPermission> siteRules = getRulesForSite( site );
        AccessPermission access = applyAccessRules( service, operation, siteRules );

        return access == AccessPermission.ALLOW;
    }

    private AccessPermission applyAccessRules( String service, String operation, ConcurrentMap<String, AccessPermission> siteRules )
    {
        // search for specific rule: "service.operation"
        AccessPermission accessServiceOperation = siteRules.get( service + "." + operation );
        if ( accessServiceOperation != null )
        {
            return accessServiceOperation;
        }

        // search for generic rule: "service.*"
        AccessPermission accessService = siteRules.get( service + ".*" );
        if ( accessService != null )
        {
            siteRules.putIfAbsent( service + "." + operation, accessService );
            return accessService;
        }

        // no rule found -> return default and cache value
        AccessPermission defaultAccess = siteRules.get( ACCESS_RULE_ALL );
        siteRules.putIfAbsent( service + "." + operation, defaultAccess );
        return defaultAccess;
    }

    private ConcurrentMap<String, AccessPermission> getRulesForSite( SiteKey site )
    {
        ConcurrentMap<String, AccessPermission> rules = sitesAccessRules.get( site );
        if ( rules == null )
        {
            initSiteRules( site );
            rules = sitesAccessRules.get( site );
        }
        return rules;
    }

    private void initSiteRules( SiteKey site )
    {
        ConcurrentMap<String, AccessPermission> siteRules = new ConcurrentHashMap<String, AccessPermission>();

        String allowRules = sitePropertiesService.getProperty( HTTP_SERVICES_ALLOW_PROPERTY, site );
        String denyRules = sitePropertiesService.getProperty( HTTP_SERVICES_DENY_PROPERTY, site );
        parseAndAddRules( allowRules, AccessPermission.ALLOW, siteRules, site );
        parseAndAddRules( denyRules, AccessPermission.DENY, siteRules, site );

        siteRules.putIfAbsent( ACCESS_RULE_ALL, DEFAULT_ACCESS_RULE );

        sitesAccessRules.putIfAbsent( site, siteRules );
    }

    private void parseAndAddRules( String accessRules, AccessPermission accessPermission, ConcurrentMap<String, AccessPermission> siteRules,
                                   SiteKey site )
    {
        accessRules = StringUtils.trimToEmpty( accessRules );
        String[] ruleItems = accessRules.split( "," );
        for ( String ruleItem : ruleItems )
        {
            ruleItem = ruleItem.trim();
            if ( ruleItem.isEmpty() )
            {
                continue;
            }
            if ( siteRules.containsKey( ruleItem ) )
            {
                throw new IllegalArgumentException( "Duplicated value for http service access rule '" + ruleItem + "' on site " + site );
            }
            siteRules.put( ruleItem, accessPermission );
        }
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }

    public void setSiteService( SiteService siteService )
    {
        this.siteService = siteService;
    }

}

