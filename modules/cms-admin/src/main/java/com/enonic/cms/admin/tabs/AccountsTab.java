package com.enonic.cms.admin.tabs;

import com.enonic.cms.admin.tabs.accounts.AccordionPanel;
import com.enonic.cms.admin.tabs.accounts.TablePanel;
import com.enonic.cms.admin.tabs.accounts.UserPanel;
import com.enonic.cms.admin.tabs.annotations.TopLevelTab;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Scope("prototype")
@TopLevelTab(title = "Accounts", order = 1024)
public class AccountsTab extends AbstractBaseTab
{
    @Autowired
    private AccordionPanel accordionPanel;

    @Autowired
    private TablePanel tablePanel;

    @Autowired
    private UserPanel userPanel;

    @PostConstruct
    private void init()
    {
        AbsoluteLayout top = new AbsoluteLayout();
        top.setWidth( "99%" );
        top.setHeight( "40px" );

        Label label = new Label( "<h2>Browse Accounts</h2>" );
        label.setContentMode( Label.CONTENT_XHTML );
        top.addComponent( label );

        ComboBox comboBox = new ComboBox();
        top.addComponent( comboBox, "top:5px; right:0px" );

        addComponent( top );

        HorizontalLayout line = new HorizontalLayout();
        line.setSpacing( true );
        line.setWidth( "100%" );
        line.setHeight( "520px" );

        line.addComponent( accordionPanel );
        line.addComponent( tablePanel );
        line.addComponent( userPanel );

        line.setExpandRatio( userPanel, 1.0f );

        addComponent( line );
    }

}
