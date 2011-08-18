[#ftl]
[#import "livePortalTraceLibrary.ftl" as lib/]
[#if livePortalTraceEnabled == 0]
<html>
<body>
<h1>
    Live Portal Trace is not enabled!
</h1>
</body>
</html>
    [#else]
    <html>
    <head>
    <title>Live Portal Trace</title>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript" src="javascript/lib/jquery/crypt/jquery.base64.min.js"></script>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-ui-1.8.14.min.js"></script>
    <link rel="stylesheet" type="text/css" href="css/tools/jquery-ui-1.8.14.css"/>
    <script type="text/javascript">

        var reloadCurrentRequestsIntervalId = 0;
        var reloadLongestPageRequestsIntervalId = 0;
        var reloadLongestAttachmentRequestsIntervalId = 0;
        var reloadLongestImageRequestsIntervalId = 0;
        var loadNewPastRequestsIntervalId = 0;

        var servletBaseUrl = "servlet/tools/com.enonic.cms.server.service.tools.LivePortalTraceController?page=914&op=custom";


        var lastHistoryNumber = -1;

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

        function showMoreTraceInfo( id )
        {
            $( "#" + id ).show();
        }

        function reloadCurrentPortalRequests()
        {
            $( "#window-current" ).load( servletBaseUrl + "&window=current" );
        }

        function reloadLongestPortalPageRequests()
        {
            $( "#window-longest-pagerequests" ).load( servletBaseUrl + "&window=longestpagerequests" );
        }

        function reloadLongestPortalAttachmentRequests()
        {
            $( "#window-longest-attachmentrequests" ).load( servletBaseUrl + "&window=longestattachmentrequests" );
        }

        function reloadLongestPortalImageRequests()
        {
            $( "#window-longest-imagerequests" ).load( servletBaseUrl + "&window=longestimagerequests" );
        }

        function setLastHistoryNumber( number )
        {
            lastHistoryNumber = number;
        }

        function loadNewPastPortalRequestTraces()
        {

            var url = servletBaseUrl + '&history=true&records-since-id=' + lastHistoryNumber;

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
                    tr.appendChild( td2 );

                    var td3 = document.createElement( "td" );
                    var siteNameDecoded = $.base64.decode( portalRequestTrace.siteName );
                    var siteLocalUrlDecoded = $.base64.decode( portalRequestTrace.siteLocalUrl );
                    td3.innerHTML = siteNameDecoded + " : " + siteLocalUrlDecoded;
                    tr.appendChild( td3 );

                    var td4 = document.createElement( "td" );
                    td4.innerHTML = portalRequestTrace.startTime;
                    tr.appendChild( td4 );

                    var td5 = document.createElement( "td" );
                    td5.innerHTML = portalRequestTrace.executionTime;
                    td5.className = "duration-column";
                    tr.appendChild( td5 );
                }
            } );

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

    </script>
    <style type="text/css">
        h1 {
            font-size: 22pt;
        }

        h2 {
            font-size: 18px;
            font-weight: normal;
        }

        body {
            font-size: 12pt;
            font-family: Verdana,Arial,sans-serif;
        }

        .listBox {
            padding: 8px;
            margin: 10px;
            border: 1px solid #A0A0A0;
            border-radius: 4px;
            background-color: #FFFFFF;
            overflow: auto;
            font-family: monospace;
        }

        .listBox th {
            text-align: left;
        }

        pre {
            font-size: 10pt;
            border-left: 1px #000000 dotted;
            padding: 10px;
        }

        .duration-column {
            text-align: right;
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
            display: none
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
    <h1>Live Portal Trace</h1>

    <button id="stop-auto-update" onclick="stopAutomaticUpdate()">
        Stop automatic update
    </button>

    <button id="start-auto-update" onclick="startAutomaticUpdate()" disabled="true">
        Start automatic update
    </button>

    <br/>
    <br/>

    <div id="tabs">

        <ul>
            <li><a href="#tabs-1">Current requests</a></li>
            <li><a href="#tabs-2">Longest page requests</a></li>
            <li><a href="#tabs-3">Longest attachment requests</a></li>
            <li><a href="#tabs-4">Longest image requests</a></li>
        </ul>

        <!-- Current portal requests -->
        <div id="tabs-1">
            <button id="reloadCurrentPortalRequests" onclick="javascript: reloadCurrentPortalRequests()">Refresh</button>

    <div class="listBox" style="height: 200px" id="window-current">
        Please wait...
    </div>
        </div>


        <!-- Longest portal page requests -->
        <div id="tabs-2">
            <button id="reloadLongestPortalPageRequests" onclick="javascript: reloadLongestPortalPageRequests()">Refresh</button>

    <div class="listBox" style="height: 200px" id="window-longest-pagerequests">
        Please wait...
    </div>
        </div>

        <!-- Longest portal attachment requests -->
        <div id="tabs-3">
            <button id="reloadLongestPortalAttachmentRequests" onclick="javascript: reloadLongestPortalAttachmentRequests()">Refresh</button>

    <div class="listBox" style="height: 200px" id="window-longest-attachmentrequests">
        Please wait...
    </div>
        </div>

        <!-- Longest portal image requests -->
        <div id="tabs-4">
            <button id="reloadLongestPortalImageRequests" onclick="javascript: reloadLongestPortalImageRequests()">Refresh</button>

            <div class="listBox" style="height: 200px" id="window-longest-imagerequests">
                Please wait...
            </div>
        </div>

    </div>

    <!-- History -->
    <h2>History of portal requests
        <button id="fetch-recent-history" onclick="javascript: loadNewPastPortalRequestTraces()" disabled="true">Fetch recent</button>
    </h2>

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

        $(function() {
            $( "#tabs" ).tabs();
        });

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
