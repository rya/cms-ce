/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.util.Map;

import com.enonic.cms.core.structure.menuitem.MenuItemRequestParameter;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Sep 15, 2010
 * Time: 10:35:04 AM
 */
public class RequestParametersMerger
{

    public static RequestParameters mergeWithMenuItemRequestParameters( RequestParameters requestParameters,
                                                                        Map<String, MenuItemRequestParameter> menuItemRequestParameterMap )
    {
        RequestParameters mergedRequestParams = new RequestParameters();

        for ( MenuItemRequestParameter param : menuItemRequestParameterMap.values() )
        {
            if ( !param.isEmpty() )
            {
                mergedRequestParams.addParameterValue( param.getName(), param.getValue() );
            }
        }

        for ( RequestParameters.Param param : requestParameters.getParameters() )
        {
            MenuItemRequestParameter pageRequestParam = menuItemRequestParameterMap.get( param.getName() );
            if ( pageRequestParam == null || pageRequestParam.isOverridableByRequest() )
            {
                if ( !param.isEmpty() )
                {
                    mergedRequestParams.setParameterValues( param.getName(), param.getValues() );
                }
            }
        }

        return mergedRequestParams;
    }


}
