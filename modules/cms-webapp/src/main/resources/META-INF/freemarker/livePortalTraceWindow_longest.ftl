[#ftl]
[#import "livePortalTraceLibrary.ftl" as lib/]
<table width="100%">
    <thead>
    <tr>
        <th>#</th>
        <th>Type</th>
        <th>URL</th>
        <th>Started</th>
        <th style="text-align: right">Duration</th>
    </tr>
    </thead>
    <tbody id="current-tbody">
    [#list longestTraces as trace]
        [@lib.printPortalRequestTraceRow portalRequestTrace=trace/]
    [/#list]
    </tbody>
</table>

