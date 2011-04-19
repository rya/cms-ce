/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

import com.enonic.cms.admin.tab.AccountsTab;
import com.enonic.cms.admin.tab.BaseTab;
import com.enonic.cms.admin.tab.ContentTab;
import com.enonic.cms.admin.tab.ReportsTab;
import com.enonic.cms.admin.tab.SystemTab;

final class AdminWindow
    extends Window
{
    public AdminWindow()
    {
        setCaption( "Enonic CMS" );

        TabSheet tabSheet = new TabSheet();
        tabSheet.setHeight("550px");
        tabSheet.setWidth("100%");

        // TODO : permissions ?
        Class<BaseTab>[] tabs = new Class[]
        {
                AccountsTab.class,
                ContentTab.class,
                ReportsTab.class,
                SystemTab.class
        };

        for ( Class<BaseTab> tabClass : tabs  )
        {
            try
            {
                BaseTab tab = tabClass.newInstance();
                tabSheet.addTab( tab );
            }
            catch ( Exception e) {
                throw new RuntimeException( e );
            }
        }

        addComponent(tabSheet);
    }
}
