/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.List;

/**
 * Oct 6, 2010
 */
public interface LivePortalTraceService
{
    boolean tracingEnabled();

    PortalRequestTrace startPortalRequestTracing( String url );

    PageRenderingTrace startPageRenderTracing( PortalRequestTrace portalRequestTrace );

    WindowRenderingTrace startWindowRenderTracing( PortalRequestTrace portalRequestTrace );

    DatasourceExecutionTrace startPageTemplateDatasourceExecutionTracing( String datasourceMethodName );

    DatasourceExecutionTrace startPortletDatasourceExecutionTracing( String datasourceMethodName );

    ClientMethodExecutionTrace startClientMethodExecutionTracing( String methodName );

    ContentIndexQueryTrace startContentIndexQueryTracing();

    ViewTransformationTrace startViewTransformationTracing();

    InstructionPostProcessingTrace startInstructionPostProcessingTracingForWindow();

    InstructionPostProcessingTrace startInstructionPostProcessingTracingForPage();

    AttachmentRequestTrace startAttachmentRequestTracing( PortalRequestTrace portalRequestTrace );

    ImageRequestTrace startImageRequestTracing( PortalRequestTrace portalRequestTrace );

    PortalRequestTrace getCurrentPortalRequestTrace();

    DatasourceExecutionTrace getCurrentDatasourceExecutionTrace();

    ImageRequestTrace getCurrentImageRequestTrace();

    void stopTracing( PortalRequestTrace livePortalRequestTrace );

    void stopTracing( PageRenderingTrace pageRenderTrace );

    void stopTracing( WindowRenderingTrace windowRenderingTrace );

    void stopTracing( DatasourceExecutionTrace datasourceExecutionTrace );

    void stopTracing( ClientMethodExecutionTrace clientMethodExecutionTrace );

    void stopTracing( ViewTransformationTrace trace );

    void stopTracing( ContentIndexQueryTrace contentIndexQueryTrace );

    void stopTracing( AttachmentRequestTrace attachmentRequestTrace );

    void stopTracing( ImageRequestTrace imageRequestTrace );

    void stopTracing( InstructionPostProcessingTrace instructionPostProcessingTrace );

    int getNumberOfPortalRequestTracesInProgress();

    List<PortalRequestTrace> getCurrentPortalRequestTraces();

    List<PortalRequestTrace> getHistorySince( long historyNumber );

    List<PortalRequestTrace> getLongestTimePortalPageRequestTraces();

    List<PortalRequestTrace> getLongestTimePortalAttachmentRequestTraces();

    List<PortalRequestTrace> getLongestTimePortalImageRequestTraces();

    void clearLongestPageRequestsTraces();

    void clearLongestAttachmentRequestTraces();

    void clearLongestImageRequestTraces();
}
