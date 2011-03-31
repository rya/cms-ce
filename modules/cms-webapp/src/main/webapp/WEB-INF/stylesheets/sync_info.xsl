<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

	<xsl:include href="common/button.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>

    <xsl:template match="/">
        <html>
            <head>
                <script type="text/javascript" src="javascript/admin.js"/>
                <script type="text/javascript" src="javascript/tabpane.js"/>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
                <link type="text/css" rel="stylesheet" href="css/admin.css"/>
            </head>
            <body>
                <h1>
                    <xsl:call-template name="displaysystempath">
                        <xsl:with-param name="page" select="'10'"/>
                    </xsl:call-template>
                </h1>

                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                </table>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="placebobutton"><img src="images/1x1.gif"/></td>
                    </tr>
                </table>
                <table cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                </table>

                <div class="tab-pane" id="tab-pane-1">
                    <script type="text/javascript" language="JavaScript">
                        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true);
                    </script>

					<div class="tab-page" id="tab-page-1">
                        <span class="tab">%synchronize%</span>

                        <script type="text/javascript" language="JavaScript">
                            tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                        </script>

                        <fieldset>
                            <legend>&nbsp;%blockStatus%&nbsp;</legend>
                            <table class="formtable">
                                <tr>
                                    <td class="form_labelcolumn">
                                        %totalCount%:
                                    </td>
                                    <td>
                                        <xsl:value-of select="count(/vertical/sync/status/entry)"/>
										%entries%
                                    </td>
                                </tr>
                                <tr>
                                    <td class="form_labelcolumn">
                                        %totalTime%:
                                    </td>
                                    <td>
                                        <xsl:value-of select="/vertical/sync/status/@time"/> ms
                                    </td>
                                </tr>
                            </table>
                        </fieldset>

						<xsl:if test="count(/vertical/sync/status/entry) > 0">
	                        <fieldset>
	                            <legend>&nbsp;%blockLog%&nbsp;</legend>
	                            <table class="formtable">
	                                <tr>
	                                    <td>
											<b>%fldLogLocation%</b>
	                                    </td>
	                                    <td>
											<b>%fldLogPosition%</b>
	                                    </td>
	                                    <td>
	                                        <b>%fldLogType%</b>
	                                    </td>
	                                    <td>
	                                        <b>%fldLogStatus%</b>
	                                    </td>
	                                </tr>
									<xsl:for-each select="/vertical/sync/status/entry">
		                                <tr>
		                                    <td>
		                                        <xsl:value-of select="@location"/>
		                                    </td>
		                                    <td>
		                                        <xsl:value-of select="@position"/>
		                                    </td>
		                                    <td>
		                                        <xsl:value-of select="@type"/>
		                                    </td>
		                                    <td>
		                                        <xsl:value-of select="@status"/>
		                                    </td>
		                                </tr>
									</xsl:for-each>
	                            </table>
	                        </fieldset>
						</xsl:if>
	                </div>
                </div>

                <table cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                </table>

                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="caption" select="'%cmdOk%'"/>
                    <xsl:with-param name="href">
						<xsl:text>adminpage?page=10</xsl:text> 
						<xsl:text>&amp;op=page</xsl:text> 
					</xsl:with-param>
                </xsl:call-template>

                <script type="text/javascript" language="JavaScript">
                    setupAllTabs();
                </script>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2006. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios/><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition></MapperMetaTag>
</metaInformation>
-->