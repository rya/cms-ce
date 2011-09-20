[#ftl]
<html>
<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="-1"/>

    <title>JCR Data Migration Tool</title>
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
    [#if migrationInProgress == true]
    <meta http-equiv="refresh" content="2;url=migration#last" />
    [/#if]
    <script type="text/javascript">
    <!--
        function startUpgrade(){
            if (confirm("Are you sure you want to start the migration?")) {
                location.href = "?start=true";
            }
        }
    //-->
    </script>
</head>
<body>
<h1>Welcome to ${versionTitleVersion}</h1>

<div class="infoBox">
    <b>JCR Data Migration tool</b>
    <ul>
        [#if migrationInProgress == true]
            <li>Migration in progress. Please wait for the process to finish...</li>
        [#else]
            [#if migrationOk == true]
            <li>Migration completed successfully!</li>
            <li><a href="migration/jcrxml" target="_blank">View JCR repository as XML</a></li>
            <li><a href="migration/jcrtree" target="_blank">View JCR repository tree</a></li>
            [/#if]
            <li><button id="migrateBut" onclick="startUpgrade()">Start Migration</button></li>
        [/#if]
    </ul>
</div>

[#if log?size > 0]
<div class="infoBox">
    <b>Log Messages</b>
    <div class="messages">
        [#list log as entry]
            <div [#if entry.level == 'INFO']class="level-info"[/#if][#if entry.level == 'WARN']class="level-warning"[/#if][#if entry.level == 'ERROR']class="level-error"[/#if]>
            ${entry}
            [#if entry.cause??]
                <pre class="detailPart">${entry.getStacktrace()}</pre>
            [/#if]
            </div>
        [/#list]
        <a name="last"/>
    </div>
</div>
[/#if]


</body>
</html>
