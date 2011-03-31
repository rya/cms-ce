<html>
<head>
    <title>${details.title}</title>
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

        .detailBox {

        }

        .detailPart {
            font-size: 8pt;
            margin: 10px;
            padding: 10px;
            border-left: 1px dashed #000000;
            overflow: hidden;
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

        .errorMessage {
            font-weight: bold;
            color: #E00000;
        }

        .footerLine {
            color: #808080;
            text-align: right;
            font-style: italic;
            font-size: 10pt;
        }

        .detailBox {
            background-color: #F0F0F0;
            padding: 10px;
            margin: 10px;
            font-size: 10pt;
        }

        #minitabs {
            margin: 0;
            padding: 0 0 20px 10px;
            border-bottom: 1px solid #606060;
        }

        #minitabs li {
            margin: 0;
            padding: 0;
            display: inline;
            list-style-type: none;
        }

        #minitabs a:link, #minitabs a:visited {
            float: left;
            font-size: 12px;
            line-height: 14px;
            font-weight: normal;
            margin: 0 10px 4px 10px;
            padding-bottom: 2px;
            text-decoration: none;
            color: #606060;
        }

        #minitabs a.active:link, #minitabs a.active:visited, #minitabs a:hover {
            border-bottom: 4px solid #606060;
            padding-bottom: 2px;
            background: #fff;
            color: #606060;
        }

        #minitabs a.active:visited {
            font-weight: bold;
        }
    </style>
    <script type="text/javascript">
        function showTab1()
        {
            showTab( 1 );
            hideTab( 2 );
            hideTab( 3 );
        }

        function showTab2()
        {
            showTab( 2 );
            hideTab( 1 );
            hideTab( 3 );
        }

        function showTab3()
        {
            showTab( 3 );
            hideTab( 1 );
            hideTab( 2 );
        }

        function showTab( num )
        {
            setLinkActive( num, true );
            setBoxVisible( num, true );
        }

        function hideTab( num )
        {
            setLinkActive( num, false );
            setBoxVisible( num, false );
        }

        function setLinkActive( num, active )
        {
            var linkId = "tab" + num + "link";
            var linkElem = document.getElementById( linkId );

            if ( active )
            {
                linkElem.className = "active";
            }
            else
            {
                linkElem.className = "";
            }
        }

        function setBoxVisible( num, visible )
        {
            var boxId = "tab" + num + "box";
            var boxElem = document.getElementById( boxId );
            if ( visible )
            {
                boxElem.style.display = "block";
            }
            else
            {
                boxElem.style.display = "none";
            }
        }
    </script>
</head>
<body>
<h1>${details.title}</h1>

<div class="infoBox errorMessage">${details.message}</div>
<div class="infoBox">
    <ul id="minitabs">
        <li><a id="tab1link" href="javascript:showTab1()" class="active">General</a></li>
        <li><a id="tab2link" href="javascript:showTab2()">Full Stack Trace</a></li>
        <li><a id="tab3link" href="javascript:showTab3()">Request info</a></li>
    </ul>
    <div id="tab1box" class="detailBox" style="display:block;">
        Due to technical problems this page cannot be served. Please try again later
        - or contact the system administrator.
    </div>
    <div id="tab2box" class="detailBox" style="display:none;">
    <#list 1..details.numExceptions as i>
        <b>${details.getExceptionMessage(i - 1)}</b><br/>
        <pre class="detailPart">${details.getExceptionStackTrace(i - 1)}</pre>
    </#list>
    </div>
<#macro printKeyValue key value>
    <tr>
        <td><code>${key}</code></td>
        <td>&nbsp;=&nbsp;</td>
        <td><code>${value}</code></td>
    </tr>
</#macro>
    <div id="tab3box" class="detailBox" style="display:none;">
        <b>General Information</b>

        <div class="detailPart">
            <table border="0" cellpadding="0" cellspacing="0">
            <@printKeyValue key="siteKey" value="${details.request.siteKey}"/>
                        <@printKeyValue key="localPath" value="${details.request.localPath}"/>
            </table>
        </div>
        <b>Request Parameters</b>

        <div class="detailPart">
            <table border="0" cellpadding="0" cellspacing="0">
            <#list details.request.parameterNames as x>
                            <@printKeyValue key="${x}" value="${details.request.getParameterValuesAsCommaSeparatedString(x)}"/>
                        </#list>
            </table>
        </div>
    </div>
</div>
<div class="infoBox footerLine">${details.generatedOnString}</div>
</body>
</html>
