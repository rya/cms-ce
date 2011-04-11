/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.login;

import java.rmi.server.UID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.store.dao.RememberedLoginDao;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.RememberedLoginEntity;
import com.enonic.cms.domain.security.RememberedLoginKey;
import com.enonic.cms.domain.security.user.UserKey;

/**
 * Jul 10, 2009
 */
public class LoginServiceImpl
    implements LoginService
{
    private RememberedLoginDao rememberedLoginDao;

    private TimeService timeService;

    private long autologinTimeoutInMilliSeconds;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String rememberLogin( UserKey userKey, SiteKey siteKey, boolean resetGUID )
    {

        RememberedLoginEntity rememberedLogin = rememberedLoginDao.findByUserKeyAndSiteKey( userKey, siteKey );
        String guid;

        if ( rememberedLogin == null )
        {
            guid = createCookieSafeUID();
            RememberedLoginKey key = new RememberedLoginKey();
            key.setSiteKey( siteKey );
            key.setUserKey( userKey );
            rememberedLogin = new RememberedLoginEntity();
            rememberedLogin.setKey( key );
            rememberedLogin.setCreatedAt( timeService.getNowAsDateTime().toDate() );
            rememberedLogin.setGuid( guid );
            rememberedLoginDao.store( rememberedLogin );
        }
        else
        {
            if ( resetGUID )
            {
                guid = createCookieSafeUID();
                rememberedLogin.setGuid( guid );
            }
            else
            {
                guid = rememberedLogin.getGuid();
            }

            rememberedLogin.setCreatedAt( timeService.getNowAsDateTime().toDate() );
        }

        return guid;
    }

    public UserKey getRememberedLogin( String guid, SiteKey siteKey )
    {
        RememberedLoginEntity rememberedLogin = rememberedLoginDao.findByGuidAndSite( guid, siteKey );
        if ( rememberedLogin == null )
        {
            return null;
        }

        long now = timeService.getNowAsMilliseconds();
        long loginRememberedAt = rememberedLogin.getCreatedAt().getTime();
        long timeRemembered = now - loginRememberedAt;

        if ( timeRemembered < autologinTimeoutInMilliSeconds )
        {
            return rememberedLogin.getKey().getUserKey();
        }

        return null;
    }

    private String createCookieSafeUID()
    {
        String uid = new UID().toString();

        // IE/Tomcat 6.0.18 hack - IE won't return cookies that got quoted (") values. Tomcat 6.0.18 will quote all values containing ()<>@,;:\\\"[]?={} \t
        // http://cephas.net/blog/2008/11/18/tomcat-6018-version-1-cookies-acegi-remember-me-and-ie/
        final String charsToReplace = "()<>@,;:\\\"[]?={} \t";
        for ( int i = 0; i < charsToReplace.length(); i++ )
        {
            uid = uid.replace( charsToReplace.charAt( i ), '_' );
        }
        return uid;
    }

    @Autowired
    public void setRememberedLoginDao( RememberedLoginDao value )
    {
        this.rememberedLoginDao = value;
    }

    @Autowired
    public void setTimeService( TimeService value )
    {
        this.timeService = value;
    }

    public void setAutologinTimeoutInDays( Integer value )
    {
        this.autologinTimeoutInMilliSeconds = (long) 1000 * 60 * 60 * 24 * value;
    }


}
