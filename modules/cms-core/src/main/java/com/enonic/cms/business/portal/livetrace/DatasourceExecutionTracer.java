package com.enonic.cms.business.portal.livetrace;


import com.enonic.cms.business.portal.datasource.methodcall.MethodCall;
import com.enonic.cms.business.portal.datasource.methodcall.MethodCallParameter;

import com.enonic.cms.domain.portal.datasource.DatasourcesType;

public class DatasourceExecutionTracer
{
    public static DatasourceExecutionTrace startTracing( DatasourcesType datasourcesType, String datasourceMethodName,
                                                         LivePortalTraceService livePortalTraceService )
    {
        if ( !livePortalTraceService.tracingEnabled() )
        {
            return null;
        }

        if ( datasourceMethodName == null )
        {
            return null;
        }

        if ( DatasourcesType.PAGETEMPLATE == datasourcesType )
        {
            return livePortalTraceService.startPageTemplateDatasourceExecutionTracing( datasourceMethodName );
        }
        else if ( DatasourcesType.PORTLET == datasourcesType )
        {
            return livePortalTraceService.startPortletDatasourceExecutionTracing( datasourceMethodName );
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( DatasourceExecutionTrace trace, LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null && livePortalTraceService != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceRunnableCondition( DatasourceExecutionTrace trace, String runnableCondition )
    {
        if ( trace != null )
        {
            trace.setRunnableCondition( runnableCondition );
        }
    }

    public static void traceIsExecuted( DatasourceExecutionTrace trace, boolean isExecuted )
    {
        if ( trace != null )
        {
            trace.setExecuted( isExecuted );
        }
    }

    public static void traceMethodCall( MethodCall methodCall, DatasourceExecutionTrace trace )
    {
        if ( trace != null && methodCall != null )
        {
            for ( MethodCallParameter param : methodCall.getParameters() )
            {
                String name = param.getName();
                if ( !"__context__".equalsIgnoreCase( name ) )
                {
                    String value = objectToString( param.getArgument() );
                    String override = param.getOverride();
                    trace.addDatasourceMethodArgument( new DatasourceMethodArgument( name, value, override ) );
                }
            }
        }
    }

    public static void traceIsCacheUsed( boolean cacheUsed, LivePortalTraceService livePortalTraceService )
    {
        if ( livePortalTraceService == null )
        {
            return;
        }

        DatasourceExecutionTrace trace = livePortalTraceService.getCurrentDatasourceExecutionTrace();
        if ( trace != null )
        {
            trace.setCacheUsed( cacheUsed );
        }
    }

    private static String objectToString( Object object )
    {
        if ( object == null )
        {
            return "";
        }

        if ( object.getClass().isArray() )
        {
            if ( object instanceof int[] )
            {
                StringBuilder sb = new StringBuilder();
                int[] array = (int[]) object;
                for ( int i = 0; i < array.length; i++ )
                {
                    sb.append( array[i] );
                    if ( i < array.length - 1 )
                    {
                        sb.append( "," );
                    }
                }
                return sb.toString();
            }
            else if ( object instanceof String[] )
            {
                StringBuilder sb = new StringBuilder();
                String[] array = (String[]) object;
                for ( int i = 0; i < array.length; i++ )
                {
                    sb.append( array[i] );
                    if ( i < array.length - 1 )
                    {
                        sb.append( "," );
                    }
                }
                return sb.toString();
            }
            else if ( object instanceof boolean[] )
            {
                StringBuilder sb = new StringBuilder();
                boolean[] array = (boolean[]) object;
                for ( int i = 0; i < array.length; i++ )
                {
                    sb.append( array[i] );
                    if ( i < array.length - 1 )
                    {
                        sb.append( "," );
                    }
                }
                return sb.toString();
            }
            else
            {
                return String.valueOf( object );
            }
        }
        else
        {
            return String.valueOf( object );
        }
    }


}
