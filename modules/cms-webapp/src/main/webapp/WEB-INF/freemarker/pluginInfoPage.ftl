[#ftl]
[#import "pluginInfoLibrary.ftl" as lib/]
<html>
<head>
    <title>Plugin Information</title>
      <link type="text/css" rel="stylesheet" href="css/admin.css"/>
    <script type="text/javascript" src="javascript/lib/jquery/jquery-1.6.2.min.js"></script>
    <script type="text/javascript">

        var lastKey = 0;

        function showDetails(key)
        {
            hideDetails(lastKey);
            $("#details-" + key).show();
            lastKey = key;
        }

        function hideDetails(key)
        {
            $("#details-" + key).hide();
            lastKey = 0;
        }

    </script>

    <style type="text/css">

        .infoBox {
            padding: 8px;
            margin: 10px;
            border: 1px dotted #000000;
            background-color:#EEEEEE;
        }

        pre {
            font-size: 10pt;
            border-left: 1px #000000 dotted;
            padding: 10px;
        }

        .detailWindow {
            background-color: #d3d3d3;
            border: 2px solid gray;
            padding: 5px;
            position: fixed;
            width: 50%;
            max-height: 60%;
            overflow-y: auto;
            overflow-x: auto;
            top: 10px;
            right: 10px;
            display: none
        }
    </style>

</head>
<body>
    <h1>Admin / Plugin Information</h1>

<div class="infoBox">
    <b>Registered Plugins</b>

    <ul>
    	[#list pluginHandles as plugin]
            [@lib.pluginInfoRowWithDetails plugin=plugin/]
		[/#list]
    </ul>
</div>

<div class="infoBox">
    <b>Registered Extensions</b>

[#if functionLibraryExtensions?size > 0]
    <fieldset class="infoBox">
        <legend>Function Library Extensions</legend>
        <ul>
            [#list functionLibraryExtensions as plugin]
                <li>${plugin.html}</li>
            [/#list]
        </ul>
    </fieldset>
[/#if]

[#if autoLoginExtensions?size > 0]
    <fieldset class="infoBox">
        <legend>Autologin Extensions <small>(Sorted by priority)</small></legend>
        <ul>
            [#list autoLoginExtensions as plugin]
                <li>${plugin.html}</li>
            [/#list]
        </ul>
    </fieldset>
[/#if]

[#if httpInterceptors?size > 0]
    <fieldset class="infoBox">
        <legend>Http Interceptor Extensions <small>(Sorted by priority)</small></legend>
        <ul>
            [#list httpInterceptors as plugin]
                <li>${plugin.html}</li>
            [/#list]
        </ul>
    </fieldset>
[/#if]

[#if httpResponseFilters?size > 0]
    <fieldset class="infoBox">
        <legend>Http Response Filters Extensions <small>(Sorted by priority)</small></legend>
        <ul>
            [#list httpResponseFilters as plugin]
                <li>${plugin.html}</li>
            [/#list]
        </ul>
    </fieldset>
[/#if]

[#if taskExtensions?size > 0]
    <fieldset class="infoBox">
        <legend>Task Handler Extensions</legend>
        <ul>
            [#list taskExtensions as plugin]
                <li>${plugin.html}</li>
            [/#list]
        </ul>
    </fieldset>
[/#if]

[#if textExtractorExtensions?size > 0]
    <div class="infoBox">
        <b>Text Extractor Extensions</b>
        <ul>
            [#list textExtractorExtensions as plugin]
                <li>${plugin.html}</li>
            [/#list]
        </ul>
    </div>
[/#if]
</div>

[#list pluginHandles as plugin]
    [@lib.pluginDetail plugin=plugin/]
[/#list]

</body>
</html>
