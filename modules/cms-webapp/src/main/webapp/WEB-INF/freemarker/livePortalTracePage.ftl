[#ftl]
[#import "livePortalTraceLibrary.ftl" as lib/]
[#if livePortalTraceEnabled == 0]
<html>
<body>
<h1>
    Admin / Live Portal Trace is not enabled!
</h1>
</body>
</html>
    [#else]
    <html>
    <head>
    <title>Admin / Live Portal Trace </title>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="javascript/lib/jquery/crypt/jquery.base64.min.js"></script>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-ui-1.8.14.min.js"></script>
    <script type="text/javascript" src="javascript/tabpane.js"></script>

    <link type="text/css" rel="stylesheet" href="css/admin.css"/>
    <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
    <link rel="stylesheet" type="text/css" href="css/tools/jquery-ui-1.8.14.css"/>

    <script type="text/javascript">

    var reloadCurrentRequestsIntervalId = 0;
    var reloadLongestPageRequestsIntervalId = 0;
    var reloadLongestAttachmentRequestsIntervalId = 0;
    var reloadLongestImageRequestsIntervalId = 0;
    var loadNewPastRequestsIntervalId = 0;
    var refreshSystemInfoIntervalId = 0;

    var lastHistoryNumber = -1;

    function resolveURLAndAddParams( params )
    {
        return "servlet/tools/com.enonic.cms.core.tools.LivePortalTraceController?page=914&op=custom&" + params;
    }

    function stopAutomaticUpdate()
    {
        clearInterval( reloadCurrentRequestsIntervalId );
        clearInterval( reloadLongestPageRequestsIntervalId );
        clearInterval( reloadLongestAttachmentRequestsIntervalId );
        clearInterval( reloadLongestImageRequestsIntervalId );
        clearInterval( loadNewPastRequestsIntervalId );

        document.getElementById( "stop-auto-update" ).disabled = true;
        document.getElementById( "start-auto-update" ).disabled = false;
        document.getElementById( "fetch-recent-history" ).disabled = false;
    }

    function startAutomaticUpdate()
    {
        startAutomaticUpdateOfCurrent();
        startAutomaticUpdateOfLongestPageRequests();
        startAutomaticUpdateOfLongestAttachmentRequests();
        startAutomaticUpdateOfLongestImageRequests();
        startAutomaticUpdateOfHistory();

        document.getElementById( "stop-auto-update" ).disabled = false;
        document.getElementById( "start-auto-update" ).disabled = true;
        document.getElementById( "fetch-recent-history" ).disabled = true;
    }

    function startAutomaticUpdateOfCurrent()
    {
        reloadCurrentRequestsIntervalId = setInterval( function()
                                                       {
                                                           reloadCurrentPortalRequests()
                                                       }, 2000 );
    }

    function startAutomaticUpdateOfLongestPageRequests()
    {
        reloadLongestPageRequestsIntervalId = setInterval( function()
                                                           {
                                                               reloadLongestPortalPageRequests()
                                                           }, 10000 );
    }

    function startAutomaticUpdateOfLongestAttachmentRequests()
    {
        reloadLongestAttachmentRequestsIntervalId = setInterval( function()
                                                                 {
                                                                     reloadLongestPortalAttachmentRequests()
                                                                 }, 10000 );
    }

    function startAutomaticUpdateOfLongestImageRequests()
    {
        reloadLongestImageRequestsIntervalId = setInterval( function()
                                                            {
                                                                reloadLongestPortalImageRequests()
                                                            }, 10000 );
    }

    function startAutomaticUpdateOfHistory()
    {
        loadNewPastRequestsIntervalId = setInterval( function()
                                                     {
                                                         loadNewPastPortalRequestTraces();
                                                     }, 2000 );
    }

    function startAutomaticUpdateOfSystemInfo()
    {
        refreshSystemInfoIntervalId = setInterval( function()
                                                   {
                                                       refreshSystemInfo()
                                                   }, 500 );
    }

    function showMoreTraceInfo( id )
    {
        $( "#" + id ).show();
    }

    function reloadCurrentPortalRequests()
    {
        $( "#window-current" ).load( resolveURLAndAddParams( "window=current" ) );
    }

    function reloadLongestPortalPageRequests()
    {
        $( "#window-longest-pagerequests" ).load( resolveURLAndAddParams( "window=longestpagerequests") );
    }

    function clearLongestPageRequestTraces()
    {
        jQuery.ajax( {
                         url: resolveURLAndAddParams( "command=clear-longestpagerequests" ),
                         type: 'POST',
                         cache: false,
                         async: true,
                         dataType: 'html',
                         success: reloadLongestPortalPageRequests
                     } );
    }

    function reloadLongestPortalAttachmentRequests()
    {
        $( "#window-longest-attachmentrequests" ).load( resolveURLAndAddParams( "window=longestattachmentrequests" ) );
    }

    function clearLongestAttachmentRequestTraces()
    {
        jQuery.ajax( {
                         url: resolveURLAndAddParams( "command=clear-longestattachmentrequests" ),
                         type: 'POST',
                         cache: false,
                         async: true,
                         dataType: 'html',
                         success: reloadLongestPortalAttachmentRequests

                     } );
    }

    function reloadLongestPortalImageRequests()
    {
        $( "#window-longest-imagerequests" ).load( resolveURLAndAddParams( "window=longestimagerequests" ) );
    }

    function clearLongestImageRequestTraces()
    {
        jQuery.ajax( {
                         url: resolveURLAndAddParams( 'command=clear-longestimagerequests') ,
                         type: 'POST',
                         cache: false,
                         async: true,
                         dataType: 'html',
                         success: reloadLongestPortalImageRequests
                     } );
    }

    function setLastHistoryNumber( number )
    {
        lastHistoryNumber = number;
    }

    function loadNewPastPortalRequestTraces()
    {

        var url = resolveURLAndAddParams( "history=true&records-since-id=" + lastHistoryNumber );

        $.getJSON( url, function( jsonObj )
        {
            var lastHistoryRecordNumber = jsonObj.lastHistoryRecordNumber;
            setLastHistoryNumber( lastHistoryRecordNumber );

            var pastPortalRequestTraces = jsonObj.pastPortalRequestTraces;

            var tableBody = document.getElementById( "newPastPortalRequestTraces-table-body" );

            // potential slow
            var firstTr = tableBody.getElementsByTagName( "tr" )[0];
            var initialLoad = firstTr == null;

            for ( var key in pastPortalRequestTraces )
            {
                var pastPortalRequestTrace = pastPortalRequestTraces[key];
                var portalRequestTrace = pastPortalRequestTrace.portalRequestTrace;

                var tr = document.createElement( "tr" );
                tr.mydata = $.base64.decode( portalRequestTrace.detailHtml );
                tr.onclick = function()
                {
                    showPortalRequestTraceDetail( this.mydata );
                };

                if ( initialLoad )
                {
                    tableBody.appendChild( tr );
                }
                else
                {
                    tableBody.insertBefore( tr, firstTr );
                }

                var td1 = document.createElement( "td" );
                td1.innerHTML = portalRequestTrace.requestNumber;
                tr.appendChild( td1 );

                var td2 = document.createElement( "td" );
                td2.innerHTML = portalRequestTrace.type;
                td2.className = "type-column";
                tr.appendChild( td2 );

                var td3 = document.createElement( "td" );
                var siteNameDecoded = $.base64.decode( portalRequestTrace.siteName );
                var siteLocalUrlDecoded = $.base64.decode( portalRequestTrace.siteLocalUrl );
                td3.innerHTML = siteNameDecoded + " : " + siteLocalUrlDecoded;
                tr.appendChild( td3 );

                var td4 = document.createElement( "td" );
                td4.innerHTML = portalRequestTrace.startTime;
                td4.className = "startTime-column";
                tr.appendChild( td4 );

                var td5 = document.createElement( "td" );
                td5.innerHTML = portalRequestTrace.executionTime;
                td5.className = "duration-column";
                tr.appendChild( td5 );
            }
        } );

    }

    function refreshSystemInfo()
    {

        var url = resolveURLAndAddParams( "system-info=true" );

        $.getJSON( url, function( jsonObj )
        {
            $( '#current-requests-tab-label' ).text( jsonObj.portal_request_traces_in_progress );

            $( '#entity-cache-count' ).text( jsonObj.entity_cache_count );
            $( '#entity-cache-hit-count' ).text( jsonObj.entity_cache_hit_count );
            $( '#entity-cache-miss-count' ).text( jsonObj.entity_cache_miss_count );
            $( '#entity-cache-capacity-count' ).text( jsonObj.entity_cache_capacity_count );

            $( '#page-cache-count' ).text( jsonObj.page_cache_count );
            $( '#page-cache-hit-count' ).text( jsonObj.page_cache_hit_count );
            $( '#page-cache-miss-count' ).text( jsonObj.page_cache_miss_count );
            $( '#page-cache-capacity-count' ).text( jsonObj.page_cache_capacity_count );

            $( '#java-heap-memory-usage-init' ).text( humanReadableBytes( jsonObj.java_heap_memory_usage_init ) );
            $( '#java-heap-memory-usage-used' ).text( humanReadableBytes( jsonObj.java_heap_memory_usage_used ) );
            $( '#java-heap-memory-usage-committed' ).text( humanReadableBytes( jsonObj.java_heap_memory_usage_committed ) );
            $( '#java-heap-memory-usage-max' ).text( humanReadableBytes( jsonObj.java_heap_memory_usage_max ) );

            $( '#java-non-heap-memory-usage-init' ).text( humanReadableBytes( jsonObj.java_non_heap_memory_usage_init ) );
            $( '#java-non-heap-memory-usage-used' ).text( humanReadableBytes( jsonObj.java_non_heap_memory_usage_used ) );
            $( '#java-non-heap-memory-usage-committed' ).text( humanReadableBytes( jsonObj.java_non_heap_memory_usage_committed ) );
            $( '#java-non-heap-memory-usage-max' ).text( humanReadableBytes( jsonObj.java_non_heap_memory_usage_max ) );

            $( '#java-thread-count' ).text( jsonObj.java_thread_count );
            $( '#java-thread-peak-count' ).text( jsonObj.java_thread_peak_count );

            var data_source_open_connection_count = jsonObj.data_source_open_connection_count;
            if( data_source_open_connection_count == -1 )
            {
                $( '#data-source-open-connection-count' ).text( "N/A" );
            }
            else
            {
                $( '#data-source-open-connection-count' ).text( jsonObj.data_source_open_connection_count );
            }

            $( '#hibernate-connection-count' ).text( jsonObj.hibernate_connection_count );
            $( '#hibernate-query-cache-hit-count' ).text( jsonObj.hibernate_query_cache_hit_count );
            $( '#hibernate-collection-fetch-count' ).text( jsonObj.hibernate_collection_fetch_count );
            $( '#hibernate-collection-load-count' ).text( jsonObj.hibernate_collection_load_count );

        } );
    }

    function humanReadableBytes( size )
    {

        var suffix = ["bytes", "KB", "MB", "GB", "TB", "PB"],

                tier = 0;

        while ( size >= 1024 )
        {

            size = size / 1024;

            tier++;

        }

        return Math.round( size * 10 ) / 10 + " " + suffix[tier];
    }

    function closePortalRequestTraceDetailWindow()
    {
        $( "#portalRequestTraceDetail-window" ).hide();
    }

    function openPortalRequestTraceDetailWindow()
    {
        $( "#portalRequestTraceDetail-window" ).show();
    }

    function showPortalRequestTraceDetail( html )
    {
        $( "#portalRequestTraceDetail-details" ).html( html );

        openPortalRequestTraceDetailWindow();
    }

    function toggleWindowRenderingTrace( id )
    {
        $( "#window-rendering-trace-" + id ).toggle();
    }

    function toggleDatasourceExecutionTrace( id )
    {
        $( "#datasource-execution-trace-" + id ).toggle();
    }

    function toggleClientMethodExecutionTrace( id )
    {
        $( "#client-method-execution-trace-" + id ).toggle();
    }

    function toggleContentIndexQueryTrace( id )
    {
        $( "#content-index-query-trace-" + id ).toggle();
    }

    </script>
    <style type="text/css">

        #portalRequestTraceDetail-details a:link {
            text-decoration: none;
            font-weight: bold;
            color: black;
        }

        .noBorderBottom {
            border-bottom: none !important;
        }

        .tableIndent {
            margin-left: 20px;
        }

        .noWrap {
            white-space: nowrap;
        }

        .listBox {
            padding: 8px;
            margin: 10px;
            border: 1px solid #A0A0A0;
            border-radius: 4px;
            background-color: #eeeeee;
            overflow: auto;
        }

        .listBox td {
            font-family: Monospace;
        }

        .listBox th {
            text-align: left;
        }

        #system-info-table {
            width: 100%;
            border-collapse: collapse;
            padding: 0;
            margin: 0 0 20px 0;
        }

        #system-info-table.post tr td, #system-info-table.post tr th {
            border: 0;
            vertical-align: top;
        }

        #system-info-table tr th {
            font-weight: normal;
            background-color: #EEE;
            text-align: left;
        }

        #system-info-table tr td,
        #system-info-table tr th {
            height: 18px;
            padding: 2px 10px 2px 10px;
            vertical-align: middle;
            border: 1px #aaa solid;

        }

        #system-info-table td {
            padding-left: 10px;
        }

        .system-info-group-name-td {
            padding-right: 10px;
        }

        .system-info-value {
            text-align: right;
            width: 80px;
        }

        pre {
            font-size: 10pt;
            border-left: 1px #000000 dotted;
            padding: 10px;
        }

        .type-column {
            text-align: center !important;
            white-space: nowrap !important;
            padding-right: 10px;
            padding-left: 10px;
        }

        .duration-column {
            text-align: right !important;
            white-space: nowrap !important;
            padding-left: 10px;
        }

        .startTime-column {
            text-align: left !important;
            white-space: nowrap !important;
            padding-left: 10px;
        }

        #portalRequestTraceDetail-window {
            background-color: #d3d3d3;
            border: 2px solid #A0A0A0;
            border-radius: 4px;
            padding: 5px;
            position: fixed;
            width: 50%;
            max-height: 60%;
            overflow-y: auto;
            overflow-x: auto;
            top: 10px;
            right: 10px;
            display: none;
            z-index: 999;
        }

        #portalRequestTraceDetail-table {
            font-family: monospace;
        }

        #portalRequestTraceDetail-table td {
            padding-left: 5px;
            padding-right: 15px;
            border-bottom: 1px solid gray;
            vertical-align: top;
        }

        #portalRequestTraceDetail-table th {
            text-align: left;
        }

    </style>
    </head>
    <body>

    <table style="margin-bottom: 10px">
        <tr>
            <td style="margin-right: 20px" valign="top">
                <h1>Admin / <a href="${baseUrl}/adminpage?page=912&op=liveportaltrace">Live Portal Trace</a></h1>
                <button class="button_text" id="stop-auto-update" onclick="stopAutomaticUpdate()">
                    Stop automatic update
                </button>

                <button class="button_text" id="start-auto-update" onclick="startAutomaticUpdate()" disabled="true">
                    Start automatic update
                </button>
            </td>
            <td style="padding-left: 40px">
                <table id="system-info-table">
                    <tr>
                        <th class="system-info-group-name-td">Entity cache</th>
                        <td>count</td>
                        <td class="system-info-value" id="entity-cache-count"></td>
                        <td>hit count</td>
                        <td class="system-info-value" id="entity-cache-hit-count"></td>
                        <td>miss count</td>
                        <td class="system-info-value" id="entity-cache-miss-count"></td>
                        <td>capacity</td>
                        <td class="system-info-value" id="entity-cache-capacity-count"></td>
                    </tr>
                    <tr>
                        <th class="system-info-group-name-td">Page cache</th>
                        <td>count</td>
                        <td class="system-info-value" id="page-cache-count"></td>
                        <td>hit count</td>
                        <td class="system-info-value" id="page-cache-hit-count"></td>
                        <td>miss count</td>
                        <td class="system-info-value" id="page-cache-miss-count"></td>
                        <td>capacity</td>
                        <td class="system-info-value" id="page-cache-capacity-count"></td>
                    </tr>
                    <tr>
                        <th class="system-info-group-name-td">Hibernate stats</th>
                        <td># conn.</td>
                        <td class="system-info-value" id="hibernate-connection-count"></td>
                        <td># query cache hit</td>
                        <td class="system-info-value" id="hibernate-query-cache-hit-count"></td>
                        <td># collection fetch</td>
                        <td class="system-info-value" id="hibernate-collection-fetch-count"></td>
                        <td># collection load</td>
                        <td class="system-info-value" id="hibernate-collection-load-count"></td>
                    </tr>
                    <tr>
                        <th class="system-info-group-name-td">Java</th>
                        <td>Thread count</td>
                        <td class="system-info-value" id="java-thread-count"></td>
                        <td>Peak thread count</td>
                        <td class="system-info-value" id="java-thread-peak-count"></td>
                        <td>Open JDBC conn. count</td>
                        <td class="system-info-value" id="data-source-open-connection-count"></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <th class="system-info-group-name-td">Java Heap Mem</th>
                        <td>used</td>
                        <td class="system-info-value" id="java-heap-memory-usage-used"></td>
                        <td>commited</td>
                        <td class="system-info-value" id="java-heap-memory-usage-committed"></td>
                        <td>max</td>
                        <td class="system-info-value" id="java-heap-memory-usage-max"></td>
                        <td>init</td>
                        <td class="system-info-value" id="java-heap-memory-usage-init"></td>
                    </tr>
                    <tr>
                        <th class="system-info-group-name-td">Java Non Heap Mem</th>
                        <td>used</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-used"></td>
                        <td>commited</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-committed"></td>
                        <td>max</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-max"></td>
                        <td>init</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-init"></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>

    <div class="tab-pane" id="tab-main">

        <script type="text/javascript" language="JavaScript">
            var tabPane1 = new WebFXTabPane( document.getElementById( "tab-main" ), true );
        </script>

        <!-- History of portal requests -->
        <div class="tab-page" id="tab-page-1">
            <span class="tab">Completed requests</span>

            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
            </script>

            <button class="button_text" id="fetch-recent-history" onclick="javascript: loadNewPastPortalRequestTraces()" disabled="true">
                Fetch recent
            </button>

            <div class="listBox" style="height: 500px">
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
                    <tbody id="newPastPortalRequestTraces-table-body">
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Current portal requests -->
        <div class="tab-page" id="tab-page-2">
            <span class="tab">Current portal requests (<span id="current-requests-tab-label"></span>)</span>

            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
            </script>

            <button class="button_text" id="reloadCurrentPortalRequests" onclick="javascript: reloadCurrentPortalRequests()">Refresh
            </button>

            <div class="listBox" style="height: 200px" id="window-current">
                Please wait...
            </div>

        </div>


        <!-- Longest page requests -->
        <div class="tab-page" id="tab-page-3">
            <span class="tab">Longest page requests</span>

            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-3" ) );
            </script>

            <button class="button_text" id="reloadLongestPortalPageRequests" onclick="javascript: reloadLongestPortalPageRequests()">Refresh
            </button>
            <button id="clearLongestPageRequestTraces" onclick="javascript: clearLongestPageRequestTraces()">Clear</button>

            <div class="listBox" style="height: 200px" id="window-longest-pagerequests">
                Please wait...
            </div>
        </div>

        <!-- Longest attachment requests -->
        <div class="tab-page" id="tab-page-4">
            <span class="tab">Longest attachment requests</span>

            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-4" ) );
            </script>

            <button class="button_text" id="reloadLongestPortalAttachmentRequests"
                    onclick="javascript: reloadLongestPortalAttachmentRequests()">Refresh
            </button>
            <button id="clearLongestAttachmentRequestTraces" onclick="javascript: clearLongestAttachmentRequestTraces()">Clear</button>

            <div class="listBox" style="height: 200px" id="window-longest-attachmentrequests">
                Please wait...
            </div>
        </div>

        <!-- Longest image requests -->
        <div class="tab-page" id="tab-page-5">
            <span class="tab">Longest image requests</span>

            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-5" ) );
            </script>

            <button class="button_text" id="reloadLongestPortalImageRequests" onclick="javascript: reloadLongestPortalImageRequests()">
                Refresh
            </button>
            <button id="clearLongestImageRequestTraces" onclick="javascript: clearLongestImageRequestTraces()">Clear</button>

            <div class="listBox" style="height: 200px" id="window-longest-imagerequests">
                Please wait...
            </div>
        </div>

    </div>


    <div id="portalRequestTraceDetail-window">
        <div>
            Trace Details (<a href="javascript: void(0);" onclick="closePortalRequestTraceDetailWindow()">Close</a>)
            <div>
                <div id="portalRequestTraceDetail-details">

                </div>
            </div>
        </div>
    </div>


    <script type="text/javascript">

        setupAllTabs();

        startAutomaticUpdateOfSystemInfo();

        reloadCurrentPortalRequests();
        reloadLongestPortalPageRequests();
        reloadLongestPortalAttachmentRequests();
        reloadLongestPortalImageRequests();
        loadNewPastPortalRequestTraces();

        startAutomaticUpdate();

    </script>

    </body>
    </html>
[/#if]
