[#ftl strip_whitespace=true]
[#import "livePortalTraceLibrary.ftl" as lib/]
{
"lastCompletedNumber": "${lastCompletedNumber}",
"completedPortalRequestTraces": [
[#list completedPortalRequestTraces as trace]
{
"portalRequestTrace": [@lib.portalRequestTraceJSON portalRequestTrace=trace/]
}[#if trace_has_next],[/#if]
[/#list]
]
}