/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.portal.datasource.context.DatasourcesContextXmlCreator;

import com.enonic.cms.portal.datasource.methodcall.MethodCall;
import com.enonic.cms.portal.datasource.methodcall.MethodCallFactory;
import com.enonic.cms.portal.datasource.processor.DataSourceProcessor;
import com.enonic.cms.portal.rendering.tracing.RenderTrace;

import com.enonic.cms.domain.portal.ShoppingCart;
import com.enonic.cms.domain.portal.datasource.DataSourceResult;
import com.enonic.cms.domain.portal.datasource.Datasource;
import com.enonic.cms.domain.portal.datasource.Datasources;
import com.enonic.cms.domain.portal.datasource.expressionfunctions.ExpressionContext;
import com.enonic.cms.domain.portal.rendering.tracing.DataTraceInfo;

public class DatasourceExecutor
{

    private DatasourceExecutorContext context;

    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;


    public DatasourceExecutor( DatasourceExecutorContext datasourceExecutorContext )
    {
        this.context = datasourceExecutorContext;
    }

    public DataSourceResult getDataSourceResult( Datasources datasources )
    {
        DataSourceResult dataSourceResult = new DataSourceResult();

        Document resultDoc = new Document( new Element( resolveResultRootElementName( datasources ) ) );
        Element verticaldataEl = resultDoc.getRootElement();

        Element contextEl = datasourcesContextXmlCreator.createContextElement( datasources, context );
        verticaldataEl.addContent( contextEl );

        if ( datasources.isShoppingCartSetToFull() || datasources.isShoppingCartSetToSummary() )
        {
            Document shoppingCartDoc = buildShoppingCartXml( context.getShoppingCart(), datasources.isShoppingCartSetToFull() );

            // shoppingcart
            Element contentobject = getOrCreateElement( verticaldataEl, "contentobject" );
            Element shoppingcart = shoppingCartDoc.getRootElement();
            contentobject.addContent( shoppingcart.detach() );
        }

        // execute data sources
        for ( Datasource datasource : datasources.getDatasourceElements() )
        {
            if ( isRunnableByCondition( datasource ) )
            {
                Document datasourceResultDocument = executeMethodCall( datasource );
                verticaldataEl.addContent( datasourceResultDocument.getRootElement().detach() );
            }
        }

        dataSourceResult.setData( XMLDocumentFactory.create( resultDoc ) );
        setTraceDataSourceResult( resultDoc );

        return dataSourceResult;
    }

    /**
     * Checks the condition attribute of the xml element, and evaluates it to decide if the datasource should be run. Returns true if
     * condition does not exists, is empty, contains 'true' or evaluates to true.
     */
    protected boolean isRunnableByCondition( final Datasource datasource )
    {
        // Note: Made protected to enable testing. Should normally be tested through public methods

        if ( datasource.getCondition() == null || datasource.getCondition().equals( "" ) )
        {
            return true;
        }

        try
        {
            ExpressionContext expressionFunctionsContext = new ExpressionContext();
            expressionFunctionsContext.setSite( context.getSite() );
            expressionFunctionsContext.setMenuItem( context.getMenuItem() );
            expressionFunctionsContext.setContentFromRequest( context.getContentFromRequest() );
            expressionFunctionsContext.setUser( context.getUser() );
            expressionFunctionsContext.setPortalInstanceKey( context.getPortalInstanceKey() );
            expressionFunctionsContext.setLocale( context.getLocale() );
            expressionFunctionsContext.setDeviceClass( context.getDeviceClass() );
            expressionFunctionsContext.setPortletWindowRenderedInline( context.isPortletWindowRenderedInline() );

            ExpressionFunctionsExecutor expressionExecutor = new ExpressionFunctionsExecutor();
            expressionExecutor.setExpressionContext( expressionFunctionsContext );
            expressionExecutor.setHttpRequest( context.getHttpRequest() );
            expressionExecutor.setRequestParameters( context.getRequestParameters() );
            expressionExecutor.setVerticalSession( context.getVerticalSession() );

            String evaluatedExpression = expressionExecutor.evaluate( datasource.getCondition() );
            return evaluatedExpression.equals( "true" );
        }
        catch ( Exception e )
        {
            throw new DatasourceException( "Failed to evaluate expression", e );
        }

    }


    private String resolveResultRootElementName( Datasources datasources )
    {
        final String name = datasources.getResultRootName();

        if ( name != null && name.length() > 0 )
        {
            return name;
        }
        return context.getDefaultResultRootElementName();
    }

    private Document buildShoppingCartXml( ShoppingCart cart, boolean full )
    {
        if ( cart != null )
        {
            return XMLDocumentFactory.create( cart.toDoc( full ) ).getAsJDOMDocument();
        }
        else
        {
            return new Document( new Element( "shoppingcart" ) );
        }
    }


    private Document executeMethodCall( Datasource datasource )
    {
        MethodCall methodCall = MethodCallFactory.create( context, datasource, context.getDatasourceServiceInvocationCache() );
        return executeMethodCall( datasource, methodCall );
    }

    private Document executeMethodCall( final Datasource datasource, final MethodCall methodCall )
    {
        XMLDocument xmlDocument = methodCall.invoke();

        xmlDocument = postProcessDocument( xmlDocument, methodCall );

        Document jdomDocument = (Document) xmlDocument.getAsJDOMDocument().clone();

        if ( datasource.getResultElementName() != null )
        {
            Element originalRootEl = jdomDocument.getRootElement();
            Element wrappingResultElement = new Element( datasource.getResultElementName() );
            wrappingResultElement.addContent( originalRootEl.detach() );
            jdomDocument = new Document( wrappingResultElement );
        }

        return jdomDocument;
    }


    private XMLDocument postProcessDocument( XMLDocument source, MethodCall methodCall )
    {
        if ( !context.hasProcessors() )
        {
            return source;
        }

        org.w3c.dom.Document doc = source.getAsDOMDocument();
        for ( DataSourceProcessor processor : context.getProcessors() )
        {
            processor.postProcess( doc, methodCall );
        }

        return XMLDocumentFactory.create( doc );
    }

    private void setTraceDataSourceResult( Document result )
    {
        DataTraceInfo info = RenderTrace.getCurrentDataTraceInfo();
        if ( info != null )
        {
            info.setDataSourceResult( XMLDocumentFactory.create( result ) );
        }
    }

    private Element getOrCreateElement( Element parent, String name )
    {
        Element elem = parent.getChild( name );
        if ( elem != null )
        {
            return elem;
        }

        elem = new Element( name );
        parent.addContent( elem );
        return elem;
    }


    public void setContext( DatasourceExecutorContext value )
    {
        this.context = value;
    }

    public void setDatasourcesContextXmlCreator( DatasourcesContextXmlCreator datasourcesContextXmlCreator )
    {
        this.datasourcesContextXmlCreator = datasourcesContextXmlCreator;
    }

}
