<html>
    <head>
        <title>${details.title?html}</title>
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
    </head>
    <body>
        <h1>${details.title?html}</h1>
            <div id="tab1box" class="detailBox" style="display:block;">
                Due to technical problems this page cannot be served. Please try again later
                - or contact the system administrator.
            </div>
        <div class="infoBox footerLine">${details.generatedOnString}</div>
    </body>
</html>
