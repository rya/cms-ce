[#ftl]
<html>
<head>
    <title>Reindex Content Tool</title>
    <style type="text/css">
        h1 {
            font-size: 22pt;
        }

        body {
            font-size: 12pt;
        }

        .infoBox {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #FFFFFF;
        }

        .buttonbar {
            float: right;
        }

        .messages {
            overflow : auto;
            height: 600px;
            width: 100%;
            margin-top: 10px;
        }

        .logentry {
            margin-left: 10px;
        }

        .level-info {
            color: #808080;
        }

        .level-error {
            color: #FF0000;
        }

        .level-warning {
            color: #808000;
        }

        .stacktrace {
            font-size: 10pt;
            border-left: medium solid #808080;
            margin-left: 20px;
            color: #808080;
        }

        .traceelem {
            margin-left: 4px;
        }
    </style>
    [#if reindexInProgress == true]
    <meta http-equiv="refresh" content="5"/>
    [/#if]
    
    <script type="text/javascript">
    <!--
        function jumpToLast() {
            location.href = "#last";
        }

        function startReindex(){
            if (confirm("Are you sure you want to start the reindexing of all content?")) {
                location.href = "?reindex=true";
            }
        }
    //-->
    </script>    
</head>
<body onload="jumpToLast()">
    <h1>Reindex Content Tool</h1>
    <div class="infoBox">
    [#if reindexInProgress == true]
        Reindexing in progress. Please wait for it to finish.    
    [#else]
        <span class="buttonbar">
            <input type="button" name="startReindex" value="Start" onclick="startReindex()"/>
        </span>
        <p>
            Reindexing of all content might take a long time, possibly affecting your live sites. Reindexing per content type is available in the administration interface.
        </p>
        <p>
            Go back to <a href="${baseUrl}">info page</a>.
        </p>
    [/#if]
    </div>
    [#if reindexLog?size > 0]
    <div class="infoBox">
        <b>${reindexInProgress?string("Log Messages", "Last Log Messages")}</b>
        <div class="messages">
            [#list reindexLog as entry]
                ${entry}<br/>
            [/#list]
            <a name="last"/>
        </div>
    </div>
    [/#if]
</body>
</html>
