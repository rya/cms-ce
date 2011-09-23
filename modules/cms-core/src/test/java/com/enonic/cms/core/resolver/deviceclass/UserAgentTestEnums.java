/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.deviceclass;

/**
 * Created by IntelliJ IDEA. User: rmh Date: Mar 19, 2009 Time: 10:06:59 AM To change this template use File | Settings | File Templates.
 */
public enum UserAgentTestEnums
{
    ANDROID( "Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3 " ),
    NOKIA_N95(
        "Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaN95/10.0.018; Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413 (KHTML, like Gecko) Safari/413" ),
    NOKIA_N80( "Mozilla/5.0 (SymbianOS/9.1; U; en-us) AppleWebKit/413 (KHTML, like Gecko) Safari/413" ),
    IPHONE( "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3" ),
    OPERA_10( "Opera/10.00 (Windows NT 6.0; U; ja) Presto/2.2.0" ),
    OPERA_9( "Opera/9.60 (X11; Linux i686; U; en) Presto/2.1.1" ),
    OPERA_MINI( "Opera/9.50 (J2ME/MIDP; Opera Mini/4.0.9800/209; U; en) " ),
    OPERA_MINI2( "Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9.0.3) Gecko/2008092417 Opera Mini/4.0.10031/298; U; en)" ),
    FIREFOX3( "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.6) Gecko/2009011913 Firefox/3.0.6" ),
    FIREFOX2( "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080612 Fedora/2.0.0.14-20080612.fc8.acer Firefox/2.0.0.14" ),
    FIREFOX_MOBILE( "Mozilla/5.0 (X11; U; Linux armv61; en-US; rv:1.9.1b2pre) Gecko/20081015 Fennec/1.0a1" ),
    IE7( "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)" ),
    SAFARI( "Mozilla/5.0 (Macintosh; U; Intel Mac OS X; en-gb) AppleWebKit/523.10.6 (KHTML, like Gecko) Version/3.0.4 Safari/523.10.6" ),
    PALM( "Mozilla/4.0 (compatible; MSIE 6.0; Windows 98; PalmSource/Palm-D050; Blazer/4.3) 16;320x320)" ),
    BLACKBERRY( "BlackBerry8330/4.3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1 VendorID/105" ),
    WINDOWS_MOBILE( "Mozilla/4.0 (compatible; MSIE 4.01; Windows CE; PPC; 240x320)" );

    public String userAgent;

    UserAgentTestEnums( String userAgent )
    {
        this.userAgent = userAgent;
    }

}
