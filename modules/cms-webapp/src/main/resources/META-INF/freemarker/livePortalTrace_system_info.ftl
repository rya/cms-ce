[#ftl]
[#import "livePortalTraceLibrary.ftl" as lib/]
{
"portal_request_traces_in_progress": ${portalRequestTracesInProgress},
"entity_cache_count": ${entityCacheCount},
"entity_cache_hit_count": ${entityCacheHitCount},
"entity_cache_miss_count": ${entityCacheMissCount},
"page_cache_count": ${pageCacheCount},
"page_cache_hit_count": ${pageCacheHitCount},
"page_cache_miss_count": ${pageCacheMissCount},
"java_heap_memory_usage_init": ${javaHeapMemoryUsageInit},
"java_heap_memory_usage_used": ${javaHeapMemoryUsageUsed},
"java_heap_memory_usage_committed": ${javaHeapMemoryUsageCommitted},
"java_heap_memory_usage_max": ${javaHeapMemoryUsageMax},
"java_non_heap_memory_usage_init": ${javaNonHeapMemoryUsageInit},
"java_non_heap_memory_usage_used": ${javaNonHeapMemoryUsageUsed},
"java_non_heap_memory_usage_committed": ${javaNonHeapMemoryUsageCommitted},
"java_non_heap_memory_usage_max": ${javaNonHeapMemoryUsageMax},
"java_thread_count": ${javaThreadCount},
"java_thread_peak_count": ${javaThreadPeakCount},
"hibernate_connection_count": ${hibernateConnectionCount},
"hibernate_query_cache_hit_count": ${hibernateQueryCacheHitCount},
"hibernate_collection_fetch_count": ${hibernateCollectionFetchCount},
"hibernate_collection_load_count": ${hibernateCollectionLoadCount}
}