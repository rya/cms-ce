[#ftl]
<html>
<head>
    <title>Connection Information</title>
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

        pre {
            font-size: 10pt;
            border-left: 1px #000000 dotted;
            padding: 10px;
        }
    </style>
</head>
<body>
    <h1>Connection Information</h1>
    [#if enabled == 0]
        <div class="infoBox">
            <b style="color: #FF0000">&nbsp;Connection information collection is not enabled!&nbsp;</b>
        </div>
    [#else]
        <div class="infoBox">
            <b>&nbsp;${connlist?size} connections opened&nbsp;</b>
            <ul>
            [#list connlist as conn]
                <pre>${conn}</pre>
            [/#list]
            </ul>
        </div>
    [/#if]
</body>
</html>
