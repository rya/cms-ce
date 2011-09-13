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

        h2 {
            font-size: 12pt;
        }

        body {
            font-size: 12pt;
        }

        .detailPart table {
            font-size: 8pt;
        }

        .infoBox {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #FFFFFF;
        }

        .infoBoxError {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #FFA0A0;
        }

        .infoBoxWarning {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color: #FFFFCC;
        }
    </style>
    [#if migrationInProgress == true]
    <meta http-equiv="refresh" content="5;url=migration" />
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

</body>
</html>
