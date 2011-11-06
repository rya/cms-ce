[#ftl]
[#import "livePortalTraceLibrary.ftl" as lib/]
<table style="table-layout: fixed; width: 100%;">
    <thead>
    <tr>
        <th style="width: 5%; text-align: center">#</th>
        <th style="width: 5%; text-align: center">Type</th>
        <th style="width: 55%">URL</th>
        <th style="width: 20%; padding-left: 10px">Started</th>
        <th style="width: 15%; text-align: right">Duration</th>
    </tr>
    </thead>
    <tbody id="current-tbody">
    [#list longestTraces as trace]
        [@lib.printPortalRequestTraceRow portalRequestTrace=trace/]
    [/#list]
    </tbody>
</table>

