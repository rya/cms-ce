/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 15, 2009
 * Time: 4:13:51 PM
 */
public class PathAndParamsToStringBuilder
{
    private boolean htmlEscapeParameterAmps = false;

    private boolean includeFragment = true;

    private boolean urlEncodePath = true;

    private boolean includeParamsInPath = true;

    private String encoding = "UTF-8";

    public String toString( PathAndParams pathAndParams )
    {

        Path localPath = pathAndParams.getPath();
        RequestParameters requestParams = pathAndParams.getParams();

        StringBuffer result = new StringBuffer();

        result.append( getLocalPath( localPath ) );

        result.append( getParams( requestParams ) );

        result.append( getFragment( localPath ) );

        return result.toString();

    }

    private String getLocalPath( Path localPath )
    {
        if ( urlEncodePath )
        {
            return localPath.getAsUrlEncoded( false, encoding );
        }
        else
        {
            return localPath.getPathWithoutFragmentAsString();
        }
    }

    private String getParams( RequestParameters requestParams )
    {
        if ( includeParamsInPath && requestParams.hasParameters() )
        {
            RequestParametersToStringBuilder requestParametersToStringBuilder = new RequestParametersToStringBuilder();
            requestParametersToStringBuilder.setStartWithQuestionMark( true );
            requestParametersToStringBuilder.setHtmlEscapeParameterAmps( htmlEscapeParameterAmps );
            return requestParametersToStringBuilder.toString( requestParams );
        }

        return "";
    }

    private String getFragment( Path localPath )
    {
        if ( includeFragment && localPath.hasFragment() )
        {
            try
            {
                String encodedFragment = URLEncoder.encode( localPath.getFragment(), encoding );
                return "#" + encodedFragment;
            }
            catch ( UnsupportedEncodingException e )
            {
                throw new RuntimeException( e.getMessage(), e );
            }
        }

        return "";
    }


    public void setHtmlEscapeParameterAmps( boolean htmlEscapeParameterAmps )
    {
        this.htmlEscapeParameterAmps = htmlEscapeParameterAmps;
    }

    public void setIncludeFragment( boolean includeFragment )
    {
        this.includeFragment = includeFragment;
    }

    public void setUrlEncodePath( boolean urlEncodePath )
    {
        this.urlEncodePath = urlEncodePath;
    }

    public void setIncludeParamsInPath( boolean includeParamsInPath )
    {
        this.includeParamsInPath = includeParamsInPath;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

}




