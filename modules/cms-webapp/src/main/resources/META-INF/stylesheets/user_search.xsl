<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
	<!ENTITY Oslash "&#216;">
	<!ENTITY oslash "&#248;">
	<!ENTITY Aring  "&#197;">
	<!ENTITY aring  "&#229;">
	<!ENTITY AElig  "&#198;">
	<!ENTITY aelig  "&#230;">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/displayuserstorepath.xsl"/>
    <xsl:include href="common/button.xsl"/>

    <xsl:param name="domainkey"/>
    <xsl:param name="domainname"/>
    <xsl:param name="page"/>
    <xsl:param name="searchtype" select="simple"/>
    <xsl:param name="searchtext"/>
    <xsl:param name="sortby"/>
    <xsl:param name="sortby-direction" select="'ascending'"/>

    <xsl:variable name="previousop">
    	<xsl:choose>
    		<xsl:when test="$searchtext">
    			<xsl:text>searchresults</xsl:text>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:text>browse</xsl:text>
    		</xsl:otherwise>
    	</xsl:choose>
    </xsl:variable>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
        <xsl:text>&amp;domainkey=</xsl:text><xsl:value-of select="$domainkey"/>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>

                <link href="css/admin.css" rel="stylesheet" type="text/css">
                </link>
                <script type="text/javascript" src="javascript/admin.js">
                </script>
                <script type="text/javascript" src="javascript/accessrights.js">
                </script>
                <script type="text/javascript" src="javascript/validate.js">
                </script>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css">
                </link>
                <script type="text/javascript" src="javascript/tabpane.js">
                </script>
                <script type="text/javascript" language="JavaScript">

                    var simpleValidatedFields = new Array(3);
                    simpleValidatedFields[0] = new Array("%fldSearchText%", "searchtext", validateRequired);

                    //            var advancedValidatedFields = new Array(3);
                    //            advancedValidatedFields[0] = new Array("%fldSearchText%", "asearchtext", validateRequired);

                    function validateAll(formName)
                    {
                        var f = document.forms[formName];

                        if( document.all['searchtype'].value == 'simple') {
                            if ( !checkAll(formName, simpleValidatedFields) )
                                return false;
                        } else {
                            //                if ( !checkAll(formName, advancedValidatedFields) )
                            //                    return false;
                        }


                        f.submit();
                    }

                    function search() {

                        var searchType = "simple";
                        var selPageId = tabPane1.getSelectedPage();
                        if( selPageId == "tab-page-advanced" )
                            searchType = "advanced";

                        document.all['searchtype'].value = searchType;
                        validateAll('formAdmin');
                    }

                </script>
            </head>

            <body>
                <form name="formAdmin" method="get" action="adminpage">
                    <input type="hidden" name="op" value="searchresults"/>
                    <input type="hidden" name="page" value="{$page}"/>
                    <input type="hidden" name="domainkey" value="{$domainkey}"/>
                    <input type="hidden" name="searchtype" value="simple"/>
                    <input type="hidden" name="sortby" value="{$sortby}"/>
                    <input type="hidden" name="sortby-direction" value="{$sortby-direction}"/>

					<h1>
	                    <xsl:call-template name="displayuserstorepath">
	                        <xsl:with-param name="domainkey" select="$domainkey"/>
	                        <xsl:with-param name="lastelement" select="'%headUsers%'"/>
	                        <xsl:with-param name="lastelementurl" select="$pageURL"/>
	                        <xsl:with-param name="disabled" select="not($callback = '')"/>
	                    </xsl:call-template>
	                </h1>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>

                                <div class="tab-pane" id="tab-pane-1">
                                    <script type="text/javascript" language="JavaScript">
                                        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                    </script>
                                    <div class="tab-page" id="tab-page-standard">
                                        <span class="tab">%blockStandardSearch%</span>

                                        <script type="text/javascript" language="JavaScript">
                                            tabPane1.addTabPage( document.getElementById( "tab-page-standard" ) );
                                        </script>

                                        <table border="0" cellspacing="0" cellpadding="2">
                                            <tr>
                                                <td width="50%">%fldSearchText%</td>
                                                <td><input type="text" name="searchtext"/></td>
                                            </tr>
                                            <tr>
                                                <td></td>
                                                <td>
                                                    &nbsp;
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </table>

                    <script type="text/javascript" language="JavaScript">
                        setupAllTabs();
                    </script>

                    <xsl:call-template name="button">
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="caption" select="'%cmdQuickSearch%'"/>
                        <xsl:with-param name="onclick">
                            <xsl:text>javascript: search();</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                    <xsl:text>&nbsp;</xsl:text>
                    <xsl:call-template name="button">
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                        <xsl:with-param name="onclick">
                            <xsl:text>javaScript:window.location = 'adminpage?</xsl:text>
                            <xsl:text>page=</xsl:text><xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=</xsl:text><xsl:value-of select="$previousop"/>
                            <xsl:text>&amp;domainkey=</xsl:text><xsl:value-of select="$domainkey"/>
                            <xsl:text>&amp;searchtext=</xsl:text><xsl:value-of select="$searchtext"/>
                            <xsl:text>&amp;searchtype=</xsl:text><xsl:value-of select="$searchtype"/>
                            <xsl:text>&amp;sortby=</xsl:text><xsl:value-of select="$sortby"/>
							<xsl:text>&amp;sortby-direction=</xsl:text><xsl:value-of select="$sortby-direction"/>
                            <xsl:text>';</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                </form>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>