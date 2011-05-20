[#ftl]
<html>
<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="-1"/>

    <title>Welcome</title>
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
</head>
<body>
<h1>Welcome to ${versionTitleVersion}</h1>

<div class="infoBox">
    <b>Management components</b>
    <ul>
        <li><a href="${baseUrl}/admin/index.html">Admin Console</a></li>
        <li><a href="${baseUrl}/dav">WebDav Location</a></li>
    </ul>
</div>
<div class="infoBox">
    <b>Sites</b>
    <ul>
        [#list sites?keys?sort as key]
            <li><a href="${baseUrl}/site/${sites[key]}">${key}</a> <!--(${sites[key]})--></li>
        [/#list]

    </ul>
</div>
<div class="infoBox">
    <b>RPC endpoints</b>
    <ul>
        <li>Java - ${baseUrl}/rpc/bin</li>
    </ul>
</div>

</body>
</html>
