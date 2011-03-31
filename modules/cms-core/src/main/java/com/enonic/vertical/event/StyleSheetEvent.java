/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.domain.security.user.User;

public class StyleSheetEvent
    extends VerticalEvent
{
    private int styleSheetKey;


    public StyleSheetEvent( User user, Object source, int styleSheetKey )
    {
        super( user, source );
        this.styleSheetKey = styleSheetKey;
    }

    public int getStyleSheetKey()
    {
        return styleSheetKey;
    }
}
