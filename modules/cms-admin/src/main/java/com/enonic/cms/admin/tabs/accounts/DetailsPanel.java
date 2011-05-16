package com.enonic.cms.admin.tabs.accounts;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.HorizontalLayout;

import com.enonic.cms.admin.spring.VaadinComponent;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/14/11
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
@VaadinComponent
public class DetailsPanel extends HorizontalLayout
{

    @Autowired
    private UserPanel userPanel;

    @Autowired
    private MultipleSelectionPanel multipleSelectionPanel;

    @PostConstruct
    public void init(){
        setSizeFull();
        addComponent( userPanel );
    }

    public void switchToSingleMode(){
        removeAllComponents();
        addComponent( userPanel );
    }

    public void switchToMultipleMode(){
        removeAllComponents();
        addComponent( multipleSelectionPanel );
    }
}
