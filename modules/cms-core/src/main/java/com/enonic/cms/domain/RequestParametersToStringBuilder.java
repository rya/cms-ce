/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import org.springframework.web.util.HtmlUtils;


public class RequestParametersToStringBuilder
{

    private boolean htmlEscapeParameterAmps = false;

    private boolean startWithQuestionMark = false;

    public String toString( RequestParameters params )
    {
        if ( !params.hasParameters() )
        {
            return "";
        }

        StringBuffer s = new StringBuffer();

        int paramIndex = 0;
        for ( RequestParameters.Param param : params.getParameters() )
        {
            String[] values = param.getValues();
            for ( int valueIndex = 0; valueIndex < values.length; valueIndex++ )
            {
                String svalue = values[valueIndex];
                if ( paramIndex == 0 && startWithQuestionMark )
                {
                    s.append( "?" );
                }
                else if ( paramIndex > 0 )
                {
                    s.append( htmlEscapeParameterAmps ? HtmlUtils.htmlEscape( "&" ) : "&" );
                }
                s.append( param.getName() ).append( "=" ).append( svalue );
                paramIndex++;
            }
        }
        return s.toString();
    }

    public void setHtmlEscapeParameterAmps( boolean htmlEscapeParameterAmps )
    {
        this.htmlEscapeParameterAmps = htmlEscapeParameterAmps;
    }

    public void setStartWithQuestionMark( boolean startWithQuestionMark )
    {
        this.startWithQuestionMark = startWithQuestionMark;
    }
}
