[#ftl]

[#setting datetime_format="yyyy-MM-dd HH:mm:ss.S"/]

[#assign base64Encode = "com.enonic.cms.framework.freemarker.Base64Function"?new()/]

[#macro portalRequestTraceJSON portalRequestTrace]

    [#local detailHtml]
    [@printPortalRequestTraceDetailRows portalRequestTrace/]
    [/#local]

{
"requestNumber": "${portalRequestTrace.requestNumber}",
"type": "${portalRequestTrace.type!}",
"siteName": "${base64Encode(portalRequestTrace.siteName!)}",
"siteLocalUrl": "${base64Encode(portalRequestTrace.siteLocalUrl!)}",
"startTime": "${portalRequestTrace.duration.startTimeAsDate!?datetime}",
"executionTime": "${portalRequestTrace.duration.executionTimeAsHRFormat!}",
"detailHtml": "${base64Encode(detailHtml!)}"
}
[/#macro]

[#macro printPortalRequestTraceRow portalRequestTrace]

    [#assign detailHtml]
    [@printPortalRequestTraceDetailRows portalRequestTrace/]
    [/#assign]

<tr id="portalRequestTrace-${portalRequestTrace.id}" onclick="showPortalRequestTraceDetail( this.mydata )">
    <script type="text/javascript">
        var tr = document.getElementById( "portalRequestTrace-${portalRequestTrace.id}" );
        tr.mydata = "${detailHtml?replace("\n", "")?replace("\"","'")}";
    </script>
    <td valign="top">
    ${portalRequestTrace.requestNumber}
    </td>
    <td class="type-column">
    ${portalRequestTrace.type}
    </td>
    <td>
    ${portalRequestTrace.siteName!} : ${portalRequestTrace.siteLocalUrl!?html}
    </td>
    <td class="startTime-column">
    ${portalRequestTrace.duration.startTimeAsDate!?datetime}
    </td>
    <td class="duration-column">
    ${portalRequestTrace.duration.executionTimeAsHRFormat!}
        [#if portalRequestTrace.duration.hasEnded() == false]
            ?
        [/#if]
    </td>
</tr>
[/#macro]

[#macro printPortalRequestTraceDetailRows portalRequestTrace]
[#-- Using single quotes instead of double to make this html safe to pass as a string in json --]
<table id="portalRequestTraceDetail-table" cellspacing="5">
    <tbody>
    <tr>
        <th colspan="2">Portal request trace:</th>
    </tr>
    <tr>
        <td>Request number</td>
        <td>${portalRequestTrace.requestNumber!}</td>
    </tr>
    <tr>
        <td>Duration</td>
        <td>[@printDurationWidthStartEnd duration=portalRequestTrace.duration/]</td>
    </tr>
    <tr>
        <td>URL</td>
        <td>${portalRequestTrace.url!?html}</td>
    </tr>
    <tr>
        <td>Site</td>
        <td>${portalRequestTrace.siteName!}</td>
    </tr>
    <tr>
        <td>Site local URL</td>
        <td>${portalRequestTrace.siteLocalUrl!?html}</td>
    </tr>
    <tr>
        <td>Requester</td>
        <td>${portalRequestTrace.requester!}</td>
    </tr>
    <tr>
        <td>Type</td>
        <td>${portalRequestTrace.typeDescription} (${portalRequestTrace.type})</td>
    </tr>
    <tr>
        <td>Mode</td>
        <td>${portalRequestTrace.mode}</td>
    </tr>
    <tr>
        <td>Http Request Remote Address</td>
        <td>${portalRequestTrace.httpRequestRemoteAddress!}</td>
    </tr>
    <tr>
        <td>Http Request User Agent</td>
        <td>${portalRequestTrace.httpRequestUserAgent!}</td>
    </tr>
    <tr>
        <td>Http Request Character Encoding</td>
        <td>${portalRequestTrace.httpRequestCharacterEncoding!}</td>
    </tr>
    <tr>
        <td>Http Request Content Type</td>
        <td>${portalRequestTrace.httpRequestContentType!}</td>
    </tr>

        [#if portalRequestTrace.hasAttachmentRequsetTrace() == true]
        [@printAttachentRequestTraceDetailRows attachmentRequestTrace=portalRequestTrace.attachmentRequestTrace/]
            [#elseif portalRequestTrace.hasImageRequestTrace() == true]
            [@printImageRequestTraceDetailRows imageRequestTrace=portalRequestTrace.imageRequestTrace/]
            [#elseif portalRequestTrace.hasPageRenderingTrace() == true]
            [@printPageRenderingTraceDetailRows pageRenderingTrace=portalRequestTrace.pageRenderingTrace/]
        [/#if]
        [#if portalRequestTrace.hasWindowRenderingTrace() == true]
        <tr>
            <th colspan="2">Window rendering trace:</th>
        </tr>
        <tr>
            <td colspan="2" class="noBorderBottom">
                <table>
                [@portalWindowRendringTraceDetails windowRenderingTrace=portalRequestTrace.windowRenderingTrace index=0/]
                </table>
            </td>
        </tr>
        [/#if]
    </tbody>
</table>
[/#macro]



[#macro portalWindowRendringTraceDetails windowRenderingTrace, index]
<tr>
    <td>
        <a href="javascript: void(0);" onclick="toggleWindowRenderingTrace(${index});">
            <strong>${windowRenderingTrace.portletName}</strong>
        </a>
    </td>
    <td>
    [@printDuration duration=windowRenderingTrace.duration/]
    </td>
</tr>
<tr id="window-rendering-trace-${index}" style="display: none">
    <td colspan="2" class="noBorderBottom">
        <table class="tableIndent">
            <tr>
                <td>Used cached result</td>
                <td>${windowRenderingTrace.usedCachedResult?string}</td>
            </tr>
            <tr>
                <td>Renderer</td>
                <td>${windowRenderingTrace.renderer!}</td>
            </tr>
            [#if !windowRenderingTrace.usedCachedResult]
                <tr>
                    <th colspan="2">
                        Datasource executions:
                        [#if windowRenderingTrace.hasDatasourceExecutionTraces() == false]
                            none
                        [/#if]
                    </th>
                </tr>
                [#list windowRenderingTrace.datasourceExecutionTraces as datasourceExecutionTrace]
                    [#assign datasourceExecutionTraceId = (index*1000) + datasourceExecutionTrace_index]
                    <tr>
                        <td>
                            <a href="javascript: void(0);" onclick="toggleDatasourceExecutionTrace(${datasourceExecutionTraceId});">
                            ${datasourceExecutionTrace.methodName!}
                            </a>
                        </td>
                        <td>
                        [@printDuration duration=datasourceExecutionTrace.duration/]
                        </td>
                    </tr>
                [@datasourceExecutionTraceDetails datasourceExecutionTrace=datasourceExecutionTrace id=datasourceExecutionTraceId/]
                [/#list]
            [/#if]
            [#if windowRenderingTrace.hasInstructionPostProcessingTrace() == true]
                <tr>
                    <th colspan="2">
                        Instruction post processing:
                    </th>
                </tr>
                <tr>

                    <td colspan="2" class="noBorderBottom">
                        <table class="tableIndent">
                        [@instructionPostProcessingTraceDetails instructionPostProcessingTrace=windowRenderingTrace.instructionPostProcessingTrace/]
                        </table>
                    </td>
                </tr>
            [/#if]
        </table>
    </td>
</tr>

[/#macro]

[#macro printPageRenderingTraceDetailRows pageRenderingTrace]
<tr>
    <th colspan="2">Page rendering trace:</th>
</tr>
<tr>
    <td>Duration:</td>
    <td>[@printDuration duration=pageRenderingTrace.duration/]</td>
</tr>
<tr>
    <td>Used cached result:</td>
    <td>${pageRenderingTrace.usedCachedResult?string}</td>
</tr>
<tr>
    <td>Renderer:</td>
    <td>${pageRenderingTrace.renderer!}</td>
</tr>
    [#if !pageRenderingTrace.usedCachedResult]
    <tr>
        <th colspan="2">
            Datasource executions:
            [#if pageRenderingTrace.hasDatasourceExecutionTraces() == false]
                none
            [/#if]
        </th>
    </tr>
        [#list pageRenderingTrace.datasourceExecutionTraces as datasourceExecutionTrace]
            [#assign datasourceExecutionTraceId = (1000000) + datasourceExecutionTrace_index]
        <tr>
            <td>
                <a href="javascript: void(0);" onclick="toggleDatasourceExecutionTrace(${datasourceExecutionTraceId});">
                ${datasourceExecutionTrace.methodName!}
                </a>
            </td>
            <td>
            [@printDuration duration=datasourceExecutionTrace.duration/]
            </td>
        </tr>
        [@datasourceExecutionTraceDetails datasourceExecutionTrace=datasourceExecutionTrace id=datasourceExecutionTraceId/]
        [/#list]
    [/#if]
    [#if pageRenderingTrace.hasWindowRenderingTraces() == true]
    <tr>
        <th colspan="2">Window rendering traces:</th>
    </tr>
        [#list pageRenderingTrace.windowRenderingTraces as windowTrace]
        [@portalWindowRendringTraceDetails windowRenderingTrace=windowTrace index=windowTrace_index/]
        [/#list]
    [/#if]
    [#if pageRenderingTrace.hasInstructionPostProcessingTrace() == true]
    <tr>
        <th colspan="2">
            Instruction post processing:
        </th>
    </tr>
    <tr>
        <td colspan="2" class="noBorderBottom">
            <table class="tableIndent">
            [@instructionPostProcessingTraceDetails instructionPostProcessingTrace=pageRenderingTrace.instructionPostProcessingTrace/]
            </table>
        </td>
    </tr>
    [/#if]
[/#macro]



[#macro datasourceExecutionTraceDetails datasourceExecutionTrace id]
<tr id="datasource-execution-trace-${id}" style="display: none;">
    <td colspan="2" class="noBorderBottom">
        <table class="tableIndent">
            <tr>
                <td>Executed</td>
                <td>${datasourceExecutionTrace.executed?string("Yes", "No")}</td>
            </tr>
            <tr>
                <td>Runnable condition</td>
                <td>${datasourceExecutionTrace.runnableCondition!?html}</td>
            </tr>
            [#if datasourceExecutionTrace.executed == true]
                <tr>
                    <td>Used cached result (request scoped)</td>
                    <td>${datasourceExecutionTrace.cacheUsed?string("Yes", "No")}</td>
                </tr>
                <tr>
                    <td colspan="2">Parameters:</td>
                </tr>
                <tr>
                    <td colspan="2" class="noBorderBottom">
                        <table class="tableIndent">
                            [#list datasourceExecutionTrace.datasourceMethodArguments as argument]
                                <tr>
                                    <td>${argument.name!?html}</td>
                                    <td>${argument.value!?html}</td>
                                </tr>
                            [/#list]
                        </table>
                    </td>
                </tr>
            [/#if]
        </table>
    </td>
</tr>
[/#macro]

[#macro instructionPostProcessingTraceDetails instructionPostProcessingTrace]
<tr>
    <td>Duration</td>
    <td>[@printDuration duration=instructionPostProcessingTrace.duration/]</td>
</tr>
[/#macro]

[#macro printAttachentRequestTraceDetailRows attachmentRequestTrace]
<tr>
    <th colspan="2">Attachment request:</th>
</tr>
<tr>
    <td>Duration</td>
    <td>[@printDuration duration=attachmentRequestTrace.duration/]</td>
</tr>
<tr>
    <td>Size (bytes)</td>
    <td>${attachmentRequestTrace.sizeInBytes!}</td>
</tr>
<tr>
    <td>Content key</td>
    <td>${attachmentRequestTrace.contentKey!}</td>
</tr>
<tr>
    <td>Binary key</td>
    <td>${attachmentRequestTrace.binaryDataKey!}</td>
</tr>
[/#macro]

[#macro printImageRequestTraceDetailRows imageRequestTrace]
<tr>
    <th colspan="2">Image request:</th>
</tr>
<tr>
    <td>Duration</td>
    <td>[@printDuration duration=imageRequestTrace.duration/]</td>
</tr>
<tr>
    <td>Used cached result</td>
    <td>${imageRequestTrace.usedCachedResult!?string}</td>
</tr>
<tr>
    <td>Size (bytes)</td>
    <td>${imageRequestTrace.sizeInBytes!}</td>
</tr>
<tr>
    <td>Content key</td>
    <td>${imageRequestTrace.contentKey!}</td>
</tr>
<tr>
    <td>Label</td>
    <td>${imageRequestTrace.label!}</td>
</tr>
<tr>
    <td>Format</td>
    <td>${imageRequestTrace.imageParamFormat!}</td>
</tr>
<tr>
    <td>Quality</td>
    <td>${imageRequestTrace.imageParamQuality!}</td>
</tr>
<tr>
    <td>Filter</td>
    <td>${imageRequestTrace.imageParamFilter!}</td>
</tr>
<tr>
    <td>Background color</td>
    <td>${imageRequestTrace.imageParamBackgroundColor!}</td>
</tr>
[/#macro]

[#macro printDuration duration]
    [#if duration.hasStarted() == false]

        [#elseif duration.hasEnded() == true]
        ${duration.executionTimeAsHRFormat!}
        [#else]
        ${duration.executionTimeAsHRFormat!}
    [/#if]
[/#macro]

[#macro printDurationWidthStartEnd duration]
    [#if duration.hasStarted() == false]

        [#elseif duration.hasEnded() == true]
        ${duration.executionTimeAsHRFormat!} ( ${duration.startTimeAsDate?datetime} -> ${duration.stopTimeAsDate?datetime} )
        [#else]
        ${duration.executionTimeAsHRFormat!} ( ${duration.startTimeAsDate?datetime} -> ? )
    [/#if]
[/#macro]