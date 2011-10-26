/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.spring.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.portal.mvc.view.SiteCustomForwardView;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.esl.net.URL;

public class RedirectAndForwardHelper
{

    private boolean replaceSpacesWithPlus = false;

    private AppendParamsToPathHelper appendParamsToRedirectPathHelper = new AppendParamsToPathHelper();


    public void setReplaceSpacesWithPlus( boolean value )
    {
        this.replaceSpacesWithPlus = value;
    }

    public ModelAndView getRedirectModelAndView( HttpServletRequest request, String path )
    {
        return getRedirectModelAndView( request, path, null );
    }

    public ModelAndView getRedirectModelAndView( HttpServletRequest request, String path, Map<String, String[]> params )
    {
        if ( replaceSpacesWithPlus )
        {
            path = replaceSpacesWithPlus( path );
        }

        StringBuffer view = new StringBuffer();
        view.append( "redirect:" );
        view.append( path );

        String url = appendParamsToRedirectPathHelper.appendParamsToPath( view, request, params );

        return new ModelAndView( url );
    }

    public ModelAndView getForwardModelAndView( HttpServletRequest request, String path )
    {
        return getForwardModelAndView( request, path, null );
    }

    public ModelAndView getForwardModelAndView( HttpServletRequest request, String path, Map<String, String[]> params )
    {
        if ( replaceSpacesWithPlus )
        {
            path = replaceSpacesWithPlus( path );
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put( "path", path );
        model.put( "requestParams", params );
        return new ModelAndView( new SiteCustomForwardView(), model );
    }

    private String replaceSpacesWithPlus( String path )
    {
        return path.replaceAll( " ", "+" );
    }

    /**
     * Helper class for flatten out a map of params and append it to a path.
     */
    private class AppendParamsToPathHelper
    {

        public String appendParamsToPath( StringBuffer path, HttpServletRequest request, Map<String, String[]> params )
        {

            if ( params == null || params.size() == 0 )
            {
                return path.toString();
            }

            URL url = new URL( path );

            for ( Map.Entry<String, String[]> entry : params.entrySet() )
            {
                String key = entry.getKey();
                Object valueObject = entry.getValue();

                boolean addParam = checkAddParam( request, key, valueObject );
                if ( addParam )
                {
                    addParam( url, key, valueObject );
                }
            }

            return url.toString();
        }

        private void addParam( URL url, String key, Object valueObject )
        {

            if ( valueObject instanceof String[] )
            {
                String[] svalues = (String[]) valueObject;
                for ( String v : svalues )
                {
                    url.addParameter( key, v );
                }
            }
            else
            {
                url.addParameter( key, valueObject.toString() );
            }
        }

        protected boolean checkAddParam( HttpServletRequest request, String key, Object value )
        {
            return true;
        }
    }
}
