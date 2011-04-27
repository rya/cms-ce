/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin.tabs.accounts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.core.security.IAccordionPresentation;
import com.enonic.cms.core.security.group.GroupStorageService;
import com.enonic.cms.core.security.user.AccordionSearchCriteria;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.service.UserServicesService;


@Component
@Scope("vaadin")
public class FilterTreePanel
        extends VerticalLayout
{
    @Autowired
    private TablePanel tablePanel;

    @Autowired
    private transient UserServicesService userServicesService;

    @Autowired
    private transient GroupStorageService groupStorageService;

    @Autowired
    private transient UserStoreService userStoreService;

/*
    private Property userBoxModel;

    private Property groupBoxModel;

    private Property searchTextModel;
*/

    private CheckBox userBox;

    private CheckBox groupBox;

    private TextField search;

    private Map<String, CheckBox> userStoresMap = new HashMap<String, CheckBox>();

    @PostConstruct
    private void init()
    {
        setStyleName( "accounts-spacer" );
        setSizeFull();
        setMargin( true );

        VerticalLayout navigator = new VerticalLayout();
        navigator.setSizeFull();

        search = new TextField();
        search.setWidth( "100%" );
        search.setInputPrompt( "search" );
        search.setStyleName( "accounts-search-text" );

        search.addListener( new FieldEvents.TextChangeListener()
        {
            public void textChange( FieldEvents.TextChangeEvent event )
            {
                onChangeSearchCriteria( event.getText() );
            }
        } );

        navigator.addComponent( search );
        navigator.setExpandRatio( search, 0 );

        createBoldLabel( navigator, "Type" );
        createUserCheckBox( navigator );
        createGroupCheckBox( navigator );

        // Userstores
        createBoldLabel( navigator, "Userstores" );
        userStoresMap.clear();

        List<UserStoreEntity> userStores = userStoreService.findAll();
        for ( UserStoreEntity userStore : userStores )
        {
            String name = userStore.getName();
            // TODO [RNE]: Are GROUP relationships wanted ??
            List<UserEntity> relationships = userStoreService.getUsers( userStore.getKey() );

            CheckBox checkBox = createCheckBox( navigator, String.format( "%s (%s)", name, relationships.size() ) );
            navigator.addComponent( checkBox );

            userStoresMap.put( name, checkBox );
        }

        addComponent( navigator );
        setComponentAlignment( navigator, Alignment.TOP_LEFT );

        // init state
        userBox.setValue( true );
    }

    private void createBoldLabel( VerticalLayout navigator, String name )
    {
        Label label = new Label( name );
        label.setStyleName( "accounts-check-title" );
        navigator.addComponent( label );
        navigator.setExpandRatio( label, 0 );
        navigator.setComponentAlignment( label, Alignment.TOP_LEFT );
    }

    private void createUserCheckBox( VerticalLayout navigator )
    {
        Long count = userServicesService.count();
        String title = String.format( "Users (%s)", count );
        userBox = createCheckBox( navigator, title );
    }

    private void createGroupCheckBox( VerticalLayout navigator )
    {
        Long count = groupStorageService.count();
        String title = String.format( "Groups (%s)", count );
        groupBox = createCheckBox( navigator, title );
    }

    private CheckBox createCheckBox( VerticalLayout navigator, String title )
    {
        CheckBox checkBox = new CheckBox( title );
        checkBox.setStyleName( "accounts-check-filter" );

        navigator.addComponent( checkBox );
        navigator.setExpandRatio( checkBox, 0 );
        navigator.setComponentAlignment( checkBox, Alignment.TOP_LEFT );

        checkBox.addListener( new Property.ValueChangeListener()
        {
            @Override
            public void valueChange( Property.ValueChangeEvent event )
            {
                String searchText = (String) search.getValue();
                onChangeSearchCriteria( searchText );
            }
        } );

        return checkBox;
    }

    private AccordionSearchCriteria populateSearchCriteria( String nameExpression )
    {
        AccordionSearchCriteria criteria = new AccordionSearchCriteria();
        criteria.setNameExpression( nameExpression );

        for ( Map.Entry<String, CheckBox> entry : userStoresMap.entrySet() )
        {
            String userStoreName = entry.getKey();
            CheckBox checkBox = entry.getValue();

            if ( (Boolean) checkBox.getValue() )
            {
                UserStoreEntity userStore = userStoreService.findByName( userStoreName );
                criteria.appendUserStoreKey( userStore.getKey() );
            }
        }

        return criteria;
    }

    private void onChangeSearchCriteria( String searchText )
    {
        AccordionSearchCriteria criteria = populateSearchCriteria( searchText );
        List<IAccordionPresentation> issues = new ArrayList<IAccordionPresentation>();

        if ( (Boolean) userBox.getValue() )
        {
            issues.addAll( userServicesService.findByCriteria( criteria ) );
        }

        if ( (Boolean) groupBox.getValue() )
        {
            issues.addAll( groupStorageService.findByCriteria( criteria ) );
        }

        tablePanel.showIssues( issues );
    }
}
