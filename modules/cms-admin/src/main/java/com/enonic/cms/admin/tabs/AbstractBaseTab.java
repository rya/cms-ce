/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs;

import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.admin.tabs.annotations.TopLevelTab;

abstract public class AbstractBaseTab
        extends VerticalLayout
{
    public AbstractBaseTab()
    {
        setMargin( true );
        TopLevelTab topLevelTab = getClass().getAnnotation( TopLevelTab.class );
        setCaption( topLevelTab.title() ); // TODO i18n
    }
}
