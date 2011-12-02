[#ftl strip_whitespace=true]
[#import "livePortalTraceLibrary.ftl" as lib/]
<table class="trace-table" cellspacing="0">
    <thead>
    <tr>
        <th style="width: 5%; text-align: center">#</th>
        <th style="width: 5%; text-align: center">Type</th>
        <th style="width: 45%">URL</th>
        <th style="width: 20%; padding-left: 10px">Started</th>
        <th style="width: 10%; text-align: right">Duration</th>
        <th style="width: 15%; text-align: left; padding-left: 10px">Cache usage</th>
    </tr>
    </thead>
    <tbody id="current-tbody">
    [#list longestTraces as trace]
        [@lib.printPortalRequestTraceRow portalRequestTrace=trace/]
    [/#list]
    </tbody>
</table>

