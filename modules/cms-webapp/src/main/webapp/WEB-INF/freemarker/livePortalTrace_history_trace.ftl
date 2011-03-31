[#ftl]
[#import "livePortalTraceLibrary.ftl" as lib/]
{
"lastHistoryRecordNumber": "${lastHistoryRecordNumber}",
"pastPortalRequestTraces": [
[#list pastPortalRequestTraces as pastTrace]
{
"historyRecordNumber": "${pastTrace.historyRecordNumber}",
"portalRequestTrace": [@lib.portalRequestTraceJSON portalRequestTrace=pastTrace.portalRequestTrace/]
},
[/#list]
]
}