package com.enonic.cms.business.portal.livetrace;


import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class DatasourceExecutionTrace
{
    private String methodName;

    private String runnableCondition;

    private boolean isExecuted;

    private Duration duration = new Duration();

    private boolean isCacheUsed = false;

    private List<DatasourceMethodArgument> datasourceMethodArgumentList = new ArrayList<DatasourceMethodArgument>();

    public DatasourceExecutionTrace( String methodName )
    {
        this.methodName = methodName;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public boolean isExecuted()
    {
        return isExecuted;
    }

    void setExecuted( boolean executed )
    {
        isExecuted = executed;
    }

    void setStartTime( DateTime startTime )
    {
        duration.setStartTime( startTime );
    }

    void setStopTime( DateTime stopTime )
    {
        duration.setStopTime( stopTime );
    }

    public Duration getDuration()
    {
        return duration;
    }

    public String getRunnableCondition()
    {
        return runnableCondition;
    }

    void setRunnableCondition( String runnableCondition )
    {
        this.runnableCondition = runnableCondition;
    }

    public boolean isCacheUsed()
    {
        return isCacheUsed;
    }

    void setCacheUsed( boolean cacheUsed )
    {
        isCacheUsed = cacheUsed;
    }

    public List<DatasourceMethodArgument> getDatasourceMethodArgumentList()
    {
        return datasourceMethodArgumentList;
    }

    public void setDatasourceMethodArgumentList( List<DatasourceMethodArgument> datasourceMethodArgumentList )
    {
        this.datasourceMethodArgumentList = datasourceMethodArgumentList;
    }

    public void addDatasourceMethodArgument( DatasourceMethodArgument datasourceMethodArgument )
    {
        datasourceMethodArgumentList.add( datasourceMethodArgument );
    }

    public List<DatasourceMethodArgument> getDatasourceMethodArguments()
    {
        return datasourceMethodArgumentList;
    }
}
