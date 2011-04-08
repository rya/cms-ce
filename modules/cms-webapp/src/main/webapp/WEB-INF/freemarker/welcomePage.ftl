[#ftl]
<html>
<head>
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

[#if modelUpgradeNeeded == true]
<div class="infoBoxError">
    <b>Upgrade Needed!</b>
    <br/>
    Database upgrade from model <b>${upgradeFrom}</b> to model <b>${upgradeTo}</b> is needed. Admin or site will not
    work correctly if not upgraded. Go to <a href="${baseUrl}/tools/upgrade">tools/upgrade</a> to upgrade.
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
        <li><a href="${baseUrl}/admin/">Admin Console</a></li>
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

<div class="${toolsRestricted?string('infoBoxError', 'infoBox')}">
    <b>Tools</b>
[#if toolsRestricted == true]
    <i>(${toolsRestrictedError})</i>
[/#if]
    <ul>
        <li><a href="${baseUrl}/tools/properties">Configuration Properties</a></li>
        <li><a href="${baseUrl}/tools/upgrade">Upgrade Tool</a></li>
        <li><a href="${baseUrl}/tools/connectioninfo">Connection Information</a></li>
        <li><a href="${baseUrl}/tools/liveportaltrace">Live Portal Trace</a></li>
    </ul>
</div>
</body>
</html>
