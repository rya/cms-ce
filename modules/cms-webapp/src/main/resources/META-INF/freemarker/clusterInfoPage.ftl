[#ftl]
<html>
<head>
    <title>Cluster Information and Diagnostics</title>
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
    </style>
</head>
<body>
    <h1>Cluster Information and Diagnostics</h1>
    [#if enabled == 0]
        <div class="infoBox">
            <b style="color: #FF0000">&nbsp;Cluster is not enabled!&nbsp;</b>
        </div>
    [#else]
        <div class="infoBox">
            <b>&nbsp;Cluster Members&nbsp;</b>
            <ul>
            [#list members?sort as member]
                <li>${member}[#if member == nodeName] <i>(local)</i>[/#if]</li>
            [/#list]
            </ul>
        </div>
        <div class="infoBox">
            <b>&nbsp;Cluster Operations&nbsp;</b>
            <ul>
                <li><a href="cluster">Refresh</a></li>
                <li><a href="cluster?clearlog=true">Clear Trace Log</a></li>
                <li><a href="cluster?sendtest=true">Send Test Message</a></li>
            </ul>
        </div>
        <div class="infoBox">
            <b>&nbsp;Cluster Messages&nbsp;</b>
            <ul>
            [#list trace as item]
                <li>${item}</li>
            [/#list]
            </ul>
        </div>
    [/#if]
</body>
</html>
