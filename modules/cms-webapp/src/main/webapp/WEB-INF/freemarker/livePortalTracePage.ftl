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
    <script type="text/javascript" src="javascript/lib/jquery/jquery.sparkline.min-1.6.js"></script>
    <script type="text/javascript" src="javascript/tabpane.js"></script>
    <script type="text/javascript" src="liveportaltrace/live-portal-trace.js"></script>
    <link type="text/css" rel="stylesheet" href="css/admin.css"/>
    <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
    <link rel="stylesheet" type="text/css" href="css/tools/jquery-ui-1.8.14.css"/>
    <link rel="stylesheet" type="text/css" href="liveportaltrace/live-portal-trace.css"/>
    </head>
    <body>

    <table style="margin-bottom: 10px">
        <tr>
            <td style="margin-right: 10px">
                <h1>Admin / <a href="${baseUrl}/adminpage?page=912&op=liveportaltrace">Live Portal Trace</a></h1>
                <button class="button_text" id="stop-auto-update" onclick="stopAutomaticUpdate()">
                    Stop automatic update
                </button>

                <button class="button_text" id="start-auto-update" onclick="startAutomaticUpdate()" disabled="true">
                    Start automatic update
                </button>
            </td>
            <td style="padding-left: 10px">
                <table id="system-info-table">
                    <tr style="border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td">
                            <a onclick="$('#entity-cache-details-row').toggle();" href="javascript: void(0);">
                                Entity cache
                            </a>
                        </th>
                        <td colspan="8">usage:<span id="graph-entity-cache-capacity"></span> hits/misses: <span id="graph-entity-cache-hits-vs-misses"></span></td>
                    </tr>
                    <tr id="entity-cache-details-row" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td"></th>
                        <td class="system-info-label">count:</td>
                        <td class="system-info-value" id="entity-cache-count"></td>
                        <td class="system-info-label">hit count:</td>
                        <td class="system-info-value" id="entity-cache-hit-count"></td>
                        <td class="system-info-label">miss count:</td>
                        <td class="system-info-value" id="entity-cache-miss-count"></td>
                        <td class="system-info-label">capacity:</td>
                        <td class="system-info-value" id="entity-cache-capacity-count"></td>
                    </tr>
                    <tr style="border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td">
                            <a onclick="$('#page-cache-details-row').toggle();" href="javascript: void(0);">
                                Page cache
                            </a>
                        </th>
                        <td colspan="8">usage:<span id="graph-page-cache-capacity"></span> hits/misses: <span id="graph-page-cache-hits-vs-misses"></span></td>
                    </tr>
                    <tr id="page-cache-details-row" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td"></th>
                        <td class="system-info-label">count:</td>
                        <td class="system-info-value" id="page-cache-count"></td>
                        <td class="system-info-label">hit count:</td>
                        <td class="system-info-value" id="page-cache-hit-count"></td>
                        <td class="system-info-label">miss count:</td>
                        <td class="system-info-value" id="page-cache-miss-count"></td>
                        <td class="system-info-label">capacity:</td>
                        <td class="system-info-value" id="page-cache-capacity-count"></td>
                    </tr>
                    <tr style="border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td">
                            <a onclick="$('.java-memory-details-rows').toggle();" href="javascript: void(0);">
                                Java Memory
                            </a>
                        </th>
                        <td colspan="8">usage: <span id="graph-memory"></span></td>
                    </tr>
                    <tr class="java-memory-details-rows" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td">Heap</th>
                        <td class="system-info-label">used:</td>
                        <td class="system-info-value" id="java-heap-memory-usage-used"></td>
                        <td class="system-info-label">commited:</td>
                        <td class="system-info-value" id="java-heap-memory-usage-committed"></td>
                        <td class="system-info-label">max:</td>
                        <td class="system-info-value" id="java-heap-memory-usage-max"></td>
                        <td class="system-info-label">init:</td>
                        <td class="system-info-value" id="java-heap-memory-usage-init"></td>
                    </tr>
                    <tr class="java-memory-details-rows" style="display: none; background-color: #EEEEEE; border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td">
                            Non Heap
                        </th>
                        <td class="system-info-label">used:</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-used"></td>
                        <td class="system-info-label">commited:</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-committed"></td>
                        <td class="system-info-label">max:</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-max"></td>
                        <td class="system-info-label">init:</td>
                        <td class="system-info-value" id="java-non-heap-memory-usage-init"></td>
                    </tr>
                    <tr style="border-bottom: 1px solid #DDDDDD">
                        <th class="system-info-group-name-td">Misc.</th>
                        <td class="system-info-label">thread count:</td>
                        <td class="system-info-value" id="java-thread-count"></td>
                        <td class="system-info-label">peak thread count:</td>
                        <td class="system-info-value" id="java-thread-peak-count"></td>
                        <td class="system-info-label">open JDBC conn.:</td>
                        <td class="system-info-value" id="data-source-open-connection-count"></td>
                        <td></td>
                        <td></td>
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
            <div>
                <span id="graph-completed-requests-pr-second"></span>
                last: <span id="last-number-of-completed-request-pr-second">0</span>,
                peak: <span id="peak-number-of-completed-request-pr-second">0</span>
            </div>
            <div class="listBox" style="height: 500px">
                <table id="newPastPortalRequestTraces-table" class="trace-table" cellspacing="0">
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
            <div class="listBox" style="height: 500px" id="window-current">
                Please wait...
            </div>
        </div>


        <!-- Longest portal page requests -->
        <div class="tab-page" id="tab-page-3">
            <span class="tab">Longest page requests</span>
            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-3" ) );
            </script>
            <button class="button_text" id="reloadLongestPortalPageRequests" onclick="javascript: reloadLongestPortalPageRequests()">Refresh
            </button>
            <button id="clearLongestPageRequestTraces" onclick="javascript: clearLongestPageRequestTraces()">Clear</button>

            <div class="listBox" style="height: 500px" id="window-longest-pagerequests">
                Please wait...
            </div>
        </div>

        <!-- Longest portal attachment requests -->
        <div class="tab-page" id="tab-page-4">
            <span class="tab">Longest attachment requests</span>
            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-4" ) );
            </script>

            <button class="button_text" id="reloadLongestPortalAttachmentRequests"
                    onclick="javascript: reloadLongestPortalAttachmentRequests()">Refresh
            </button>
            <button id="clearLongestAttachmentRequestTraces" onclick="javascript: clearLongestAttachmentRequestTraces()">Clear</button>

            <div class="listBox" style="height: 500px" id="window-longest-attachmentrequests">
                Please wait...
            </div>
        </div>

        <!-- Longest portal image requests -->
        <div class="tab-page" id="tab-page-5">
            <span class="tab">Longest image requests</span>
            <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-5" ) );
            </script>
            <button class="button_text" id="reloadLongestPortalImageRequests" onclick="javascript: reloadLongestPortalImageRequests()">
                Refresh
            </button>
            <button id="clearLongestImageRequestTraces" onclick="javascript: clearLongestImageRequestTraces()">Clear</button>

            <div class="listBox" style="height: 500px" id="window-longest-imagerequests">
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

    <p>
    <blockquote style="font-style: italic;">
        Legend: <span class="cache-color-not-cacheable">O</span> not cacheable, <span class="cache-color-cache-hit">&radic;</span>
        cache hit, <span class="cache-color-cache-miss">X</span> cache miss, <span class="cache-color-cache-hit-blocked">&radic;</span> cache hit with concurrency block
    </blockquote>
    </p>

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
