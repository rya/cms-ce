/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.servlet.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BrowserDetector
{

    /**
     * IE matching pattern.
     */
    private final static Pattern IE_PATTERN = Pattern.compile( "Mozilla/\\d\\.\\d+\\s\\(compatible\\;\\s*MSIE\\s(\\d+)\\.(\\d+)\\;\\.*" );

    static public class BrowserInfo
    {
        private String browserName = "unknown";

        private String majorVersion = "0";

        private String minorVersion = "0";

        private String platformType = "";

        BrowserInfo()
        {
        }

        BrowserInfo( String browserName, String majorVersion, String minorVersion )
        {
            this.browserName = browserName;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
        }

        public String getName()
        {
            return browserName;
        }

        public String getVersion()
        {
            return majorVersion + "." + minorVersion;
        }

        public String getMajorVersion()
        {
            return majorVersion;
        }

        public String getMinorVersion()
        {
            return minorVersion;
        }

        public String getPlatformType()
        {
            return platformType;
        }

        public String toString()
        {
            return browserName + " " + getVersion();
        }
    }


    public static BrowserInfo detectBrowser( String userAgentString )
    {
        BrowserInfo bi = new BrowserInfo();

        if ( matchIE( bi, userAgentString ) )
        {
            return bi;
        }

        return bi;
    }

    private static boolean matchIE( BrowserInfo bi, String userAgentString )
    {
        Matcher matcher = IE_PATTERN.matcher( userAgentString );
        if ( matcher.find() )
        {
            bi.browserName = "IE";
            bi.majorVersion = matcher.group( 1 );
            bi.minorVersion = matcher.group( 2 );

            if ( userAgentString.toLowerCase().indexOf( "windows" ) != -1 )
            {
                bi.platformType = "windows";
            }

            return true;
        }

        return false;
    }
}
