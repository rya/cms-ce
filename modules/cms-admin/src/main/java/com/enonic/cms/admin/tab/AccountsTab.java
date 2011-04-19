package com.enonic.cms.admin.tab;

import com.vaadin.ui.Label;

import com.enonic.cms.admin.tab.annotations.Options;

@Options(title = "Accounts")
public class AccountsTab extends BaseTab
{
    public AccountsTab()
    {
        Label label = new Label( "<h3>Browse Accounts</h3>" );
        label.setContentMode(Label.CONTENT_XHTML);
        addComponent( label );
    }
}
