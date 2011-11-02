[#ftl]
<html>
<head>
    <title>Configuration Properties</title>
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
            overflow: auto;
        }

        .infoBox tt {
            font-size: 10pt;
        }

        .keyField {
            color: #000080
        }

        .valueField {
            color: #008000
        }

    </style>
</head>
<body>
    <h1>Configuration Info</h1>
    <div class="infoBox">
        <b>Directories</b>
        <ul>
            <li><span class="keyField">Home</span> = <span class="valueField">${homeDir}</span>
            <li><span class="keyField">Config</span> = <span class="valueField">${configDir}</span>
        </ul>
    </div>
    <div class="infoBox">
        <b>Config files</b>
        <ul>
            [#list configFiles?sort as configFile]
                <li>${configFile} (<a href="?download=${configFile}">download</a>)</li>
            [/#list]
        </ul>
    </div>
    <div class="infoBox">
        <b>DataSource Info</b>
        <ul>
            [#list dataSourceInfo?keys?sort as key]
                <li><span style="color: #000080">${key}</span> <b>=</b>
                <span style="color: #008000">${dataSourceInfo[key]}</span></li>
            [/#list]
        </ul>
    </div>    
    <div class="infoBox">
        <b>Configuration Properties</b>
        <ul>
            [#list properties?keys?sort as key]
                <li><span style="color: #000080">${key}</span> <b>=</b>
                <span style="color: #008000">${properties[key]}</span></li>
            [/#list]
        </ul>
    </div>
    <div class="infoBox">
        <b>System Properties</b>
        <ul>
            [#list systemProperties?keys?sort as key]
                <li><span style="color: #000080">${key}</span> <b>=</b>
                <span style="color: #008000">${systemProperties[key]}</span></li>
            [/#list]
        </ul>
    </div>
</body>
</html>
