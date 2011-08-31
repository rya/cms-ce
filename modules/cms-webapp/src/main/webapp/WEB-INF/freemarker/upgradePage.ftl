[#ftl]
<html>
<head>
    <title>Database Upgrade Tool</title>
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

        .passwordbox strong {
            display:block;
            font-size: 10pt;
        }

        .disabledbox strong {
            font-size: 10pt;
            display:block;
            background:#ddd;
            color:green;
        }

        .authenticationFailed {
            font-size: 10px;
            color: red;
            margin-top:5px;
        }

        .buttonbar {

        }

        .container {
            float: right;
            padding:10px;
            border:1px solid #eee;
            background:#ddd;
            width:320px;
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
    [#if upgradeInProgress == true]
    <meta http-equiv="refresh" content="5"/>
    [/#if]
    <script type="text/javascript">
    <!--
        function jumpToLast() {
            location.href = "#last";
        }

        function startUpgrade(all){
            if (confirm("Are you sure you want to start the upgrade?")) {

                var password = document.getElementById('adminPasswordField').value;
                var passwordParam = "";

                if (password!=null) {
                    passwordParam = "&adminPassword=" + password;
                }

                if (all) {
                    location.href = "?upgradeAll=true" + passwordParam;
                } else {
                    location.href = "?upgradeStep=true" + passwordParam;
                }
            }
        }
    //-->
    </script>    
</head>
<body onload="jumpToLast()">
    <h1>Database Upgrade Tool</h1>
    <div class="infoBox">

    [#if needsOldUpgrade == true]
        Cannot upgrade your system. Please upgrade to latest 4.5.x version first
        (${requiredVersion}).
    [#elseif upgradeInProgress == true]
        Upgrade in progress. Please wait for the upgrade to finish.
    [#elseif upgradeNeeded == true]
        <div class="container">
             [#if authenticated]
              <div class="disabledbox">
                <strong>Authenticated</strong><br/>
                &nbsp;<br>
              </div>
             [#else]
              <div class="passwordbox">
                <strong>Enterprise Administrator password:</strong>
                <input type="password" id="adminPasswordField" name="adminPasswordField"/>
              </div>
             [/#if]
            <div class="buttonbar">
                <input type="button" name="upgradeStep" value="Upgrade Step" onclick="startUpgrade(false)"/>
                <input type="button" name="upgradeAll" value="Upgrade All" onclick="startUpgrade(true)"/>
            </div>
              [#if authenticationFailed == true]
                     <div class="authenticationFailed">Authentication needed, please fill in Enterprise Administrator password</div>
              [/#if]
        </div>

        Upgrade from model <b>${upgradeFrom}</b> to model <b>${upgradeTo}</b>. Do the following steps:
        <ul>
            <li>Backup the database before proceeding.</li>
            <li>Run either one step at a time, or do the entire upgrade into one step.</li>
            <li>If successful, go back to the <a href="${baseUrl}">info page</a></li>
        </ul>
    [#else]
        Upgrade is done. System is up-to-date. Go back to <a href="${baseUrl}">info page</a>.
    [/#if]
    </div>
    [#if upgradeLog?size > 0]
    <div class="infoBox">
        <b>${upgradeInProgress?string("Log Messages", "Last Log Messages")}</b>
        <div class="messages">
            [#list upgradeLog as entry]
                ${entry}
            [/#list]
            <a name="last"/>
        </div>
    </div>
    [/#if]
</body>
</html>
