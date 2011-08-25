[#ftl]
<html>
<head>
    <title>Welcome</title>
    <link type="text/css" rel="stylesheet" href="admin/css/admin.css"/>
    <style type="text/css">

        body, td, th, input, select, textarea {
            font-family: Arial, Tahoma, Verdana, sans-serif;
            font-size: 13px;
        }

        body
        {
            background-color: #fff;
        }

        h1 {
            font-size: 22px;
            color: white;
            font-size: 22px;
            margin-top: 0;
            padding-left: 10px;
            padding-top: 27px;
        }

        body {
            padding-left: 10px;
        * padding-left : 0; /* IE hack! See frameset for spacing */
            padding-right: 15px;
            padding-bottom: 15px;
            padding-top: 8px;
        }

        .top {
        border: 0;
        margin: 9px;
        padding: 0;
        color: #fff;
        background-color: #3a3a3a;
        background-image: url( admin/images/logo-screen.gif);
        background-repeat: no-repeat;
        background-position: 98% 50%;
        height: 70px;
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
<div class="top">
<h1>Welcome to ${versionTitleVersion}</h1>
</div>

[#if modelUpgradeNeeded == true]
<div class="infoBoxError">
    <b>Upgrade Needed!</b>
    <br/>
    Database upgrade from model <b>${upgradeFrom}</b> to model <b>${upgradeTo}</b> is needed. Admin or site will not
    work correctly if not upgraded. Go to <a href="${baseUrl}/upgrade">upgrade</a> to upgrade.
</div>
[/#if]
[#if softwareUpgradeNeeded == true]
<div class="infoBoxError">
    <b>Software Upgrade Needed!</b>
    <br/>
    Database model is newer than software allows. Please upgrade the software. Admin or site will not
    work correctly if not upgraded.
</div>
[/#if]
[#list additionalMessages as message]
<div class="infoBoxWarning">
    <b>${message.key}</b>
    <br/>
${message.value}
</div>
[/#list]

<div class="infoBox">
    <b>Management components</b>
    <ul>
        <li><a href="${baseUrl}/admin">Admin Console</a></li>
        <li><a href="${baseUrl}/dav">WebDav Location</a></li>
    </ul>
</div>
<div class="infoBox">
    <b>Sites</b>
[#if upgradeNeeded == false]
    <ul>
        [#list sites?keys?sort as key]
            <li><a href="${baseUrl}/site/${sites[key]}">${key}</a> <!--(${sites[key]})--></li>
        [/#list]

    </ul>
[/#if]
[#if upgradeNeeded == true]
    <p>
        N/A
    </p>
[/#if]
</div>
<div class="infoBox">
    <b>RPC endpoints</b>
    <ul>
        <li>Java - ${baseUrl}/rpc/bin</li>
    </ul>
</div>
<div class="infoBox">
    <b>Documentation</b>
    <ul>
        <li><a href="http://www.enonic.com/docs">Enonic CMS Documentation</li>
    </ul>
</div>

</body>
</html>
