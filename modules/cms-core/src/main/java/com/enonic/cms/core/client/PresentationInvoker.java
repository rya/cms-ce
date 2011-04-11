/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.esl.util.Base64Util;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.GetBinaryParams;
import com.enonic.cms.api.client.model.GetCategoriesParams;
import com.enonic.cms.api.client.model.GetContentBinaryParams;
import com.enonic.cms.api.client.model.GetMenuBranchParams;
import com.enonic.cms.api.client.model.GetMenuDataParams;
import com.enonic.cms.api.client.model.GetMenuItemParams;
import com.enonic.cms.api.client.model.GetMenuParams;
import com.enonic.cms.api.client.model.GetSubMenuParams;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.service.DataSourceService;
import com.enonic.cms.core.service.PresentationService;

import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.security.user.User;

/**
 * This class wraps the presentation service and calls with the new client api.
 */
public final class PresentationInvoker
{

    private final PresentationService presService;

    private final DataSourceService dataSourceService;

    private final SecurityService securityService;

    public PresentationInvoker( PresentationService presService, DataSourceService dataSourceService, SecurityService securityService )
    {
        this.presService = presService;
        this.dataSourceService = dataSourceService;
        this.securityService = securityService;
    }

    private User getRunAsUser()
    {
        return securityService.getRunAsOldUser();
    }

    private DataSourceContext createDataSourceContext()
    {
        DataSourceContext dataSourceContext = new DataSourceContext();
        dataSourceContext.setUser( securityService.getRunAsUser() );
        return dataSourceContext;
    }

    public Document getCategories( GetCategoriesParams params )
        throws Exception
    {
        assertMinValue( "categoryKey", params.categoryKey, 0 );
        return this.dataSourceService.getCategories( createDataSourceContext(), params.categoryKey, params.levels,
                                                     params.includeTopCategory, true, false,
                                                     params.includeContentCount ).getAsJDOMDocument();
    }

    public Document getMenu( GetMenuParams params )
        throws Exception
    {
        assertMinValue( "menuKey", params.menuKey, 0 );

        return this.dataSourceService.getMenu( createDataSourceContext(), params.menuKey, params.tagItem, params.levels,
                                               false ).getAsJDOMDocument();
    }

    public Document getMenuBranch( GetMenuBranchParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );

        return this.dataSourceService.getMenuBranch( createDataSourceContext(), params.menuItemKey, params.includeTopLevel,
                                                     params.startLevel, params.levels ).getAsJDOMDocument();
    }

    public Document getMenuData( GetMenuDataParams params )
        throws Exception
    {
        assertMinValue( "menuKey", params.menuKey, 0 );

        return this.dataSourceService.getMenuData( createDataSourceContext(), params.menuKey ).getAsJDOMDocument();
    }

    public Document getMenuItem( GetMenuItemParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );

        return this.dataSourceService.getMenuItem( createDataSourceContext(), params.menuItemKey, params.withParents,
                                                   params.details ).getAsJDOMDocument();
    }

    public Document getSubMenu( GetSubMenuParams params )
        throws Exception
    {
        assertMinValue( "menuItemKey", params.menuItemKey, 0 );
        return this.dataSourceService.getSubMenu( createDataSourceContext(), params.menuItemKey, params.tagItem, params.levels,
                                                  false ).getAsJDOMDocument();
    }

//    public Document renderContent( RenderContentParams params )
//        throws Exception
//    {
//        assertMinValue( "menuKey", params.menuKey, 0 );
//        assertMinValue( "contentKey", params.contentKey, 0 );
//
//        HashMap<String, String> parameters = stringArrayToHashMap( params.parameters );
//        String html = this.presService.renderContentPage( getRunAsUser(), params.menuKey, params.contentKey, params.profile, parameters,
//                                                          params.basePath, params.encodeURIs );
//
//        Element markup = new Element( "markup" );
//        markup.addContent( new CDATA( html ) );
//        return new Document( markup );
//    }
//
//    public Document renderPage( RenderPageParams params )
//        throws Exception
//    {
//        assertMinValue( "menuItemKey", params.menuItemKey, 0 );
//        int menuKey = this.presService.getMenuKeyByMenuItem( params.menuItemKey );
//
//        HashMap<String, String> parameters = stringArrayToHashMap( params.parameters );
//
//        String html =
//            this.presService.renderPage( getRunAsUser(), menuKey, params.menuItemKey, params.profile, parameters, -1, params.languageCode,
//                                         params.basePath, params.encodeURIs );
//
//        Element markup = new Element( "markup" );
//        markup.addContent( new CDATA( html ) );
//        return new Document( markup );
//    }

    public Document getBinary( GetBinaryParams params )
        throws Exception
    {
        assertMinValue( "binaryKey", params.binaryKey, 0 );

        BinaryData binaryData = this.presService.getBinaryData( getRunAsUser(), params.binaryKey, -1, null, null, 0, false );

        return getBinaryDocument( binaryData );
    }

    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        assertMinValue( "contentKey", params.contentKey, 0 );

        int binaryKey = this.presService.getBinaryDataKey( params.contentKey, params.label );
        BinaryData binaryData = this.presService.getBinaryData( getRunAsUser(), binaryKey, -1, null, null, 0, false );

        return getBinaryDocument( binaryData );
    }

    private Document getBinaryDocument( BinaryData binaryData )
    {
        if ( binaryData == null )
        {
            return null;
        }

        Element binaryElem = new Element( "binary" );
        Element fileNameElem = new Element( "filename" );
        fileNameElem.setText( binaryData.fileName );
        binaryElem.addContent( fileNameElem );

        Element binaryKeyElem = new Element( "binarykey" );
        binaryKeyElem.setText( String.valueOf( binaryData.key ) );
        binaryElem.addContent( binaryKeyElem );

        Element contentKeyElem = new Element( "contentkey" );
        contentKeyElem.setText( String.valueOf( binaryData.contentKey ) );
        binaryElem.addContent( contentKeyElem );

        Element sizeElem = new Element( "binarykey" );
        sizeElem.setText( String.valueOf( binaryData.data.length ) );
        binaryElem.addContent( sizeElem );

        Element timestampElem = new Element( "binarykey" );
        timestampElem.setText( binaryData.timestamp.toString() );
        binaryElem.addContent( timestampElem );

        Element dataElem = new Element( "data" );
        dataElem.setText( Base64Util.encode( binaryData.data ) );
        binaryElem.addContent( dataElem );

        return new Document( binaryElem );
    }

    private HashMap<String, String> stringArrayToHashMap( String[] stringArray )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( stringArray != null )
        {
            for ( int i = 0; i < stringArray.length / 2; i++ )
            {
                String key = stringArray[i * 2];
                String value = stringArray[i * 2 + 1];
                map.put( key, value );
            }
        }

        return map;
    }

    private void assertMinValue( String name, int value, int minValue )
    {
        if ( value < minValue )
        {
            throw new IllegalArgumentException( "Parameter [" + name + "] must be >= " + minValue );
        }
    }

}
