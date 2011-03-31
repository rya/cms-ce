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
                        <td class="placebobutton"><img src="images/1x1.gif"/></td>
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
                            <legend>&nbsp;%blockLocalNode%&nbsp;</legend>
                            <table class="formtable">
                                <tr>
                                    <td class="form_labelcolumn">
                                        %nodeKey%:
                                    </td>
                                    <td>
                                        <xsl:value-of select="/vertical/sync/local/@key"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="form_labelcolumn">
                                        %nodeName%:
                                    </td>
                                    <td>
                                        <xsl:value-of select="/vertical/sync/local/@title"/>
                                    </td>
                                </tr>
                            </table>
                        </fieldset>

                        <fieldset>
                            <legend>&nbsp;%blockRemoteNodes%&nbsp;</legend>
                            <table class="formtable">
                                <tr>
									<td>
									</td>	
                                    <td>
										<b>%name%</b>
                                    </td>
                                    <td>
										<b>%lastImport%</b>
                                    </td>
                                    <td>
										<b>%available%</b>
                                    </td>
                                </tr>
								<form name="cmdImportForm" method="get" action="adminpage">
									<input type="hidden" name="page" value="10"/>
                        			<input type="hidden" name="op" value="simport"/>

									<xsl:for-each select="/vertical/sync/remote">
		                                <tr>
											<td>
												<input type="radio" name="node">
													<xsl:attribute name="value">
														<xsl:value-of select="@name"/>
													</xsl:attribute>
													<xsl:if test="@available = 'false'">
														<xsl:attribute name="disabled">
															<xsl:text>true</xsl:text>
														</xsl:attribute>
													</xsl:if>
												</input>
											</td>	
		                                    <td>
		                                        <xsl:value-of select="@title"/>
		                                    </td>
		                                    <td>
		                                        <xsl:value-of select="@lastimport"/>
		                                    </td>
											<td>
												<xsl:choose>
													<xsl:when test="@available = 'true'">
														%yes%
													</xsl:when>
													<xsl:otherwise>
														%no%
													</xsl:otherwise>
												</xsl:choose>
											</td>
		                                </tr>
									</xsl:for-each>
								</form>
	                        </table>
                        </fieldset>
	                </div>
                </div>

                <table cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                </table>

                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:history.back();</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

				<xsl:text>&nbsp;</xsl:text>

                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="caption" select="'%cmdRefresh%'"/>
                    <xsl:with-param name="href">
						<xsl:text>adminpage?page=10</xsl:text> 
						<xsl:text>&amp;op=sync</xsl:text> 
					</xsl:with-param>
                </xsl:call-template>

				<xsl:text>&nbsp;</xsl:text>

                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="caption" select="'%cmdImport%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:document.cmdImportForm.submit();</xsl:text>
                    </xsl:with-param>
					<xsl:with-param name="disabled">
						<xsl:choose>
							<xsl:when test="count(/vertical/sync/remote[@available = 'true']) > 0">
								<xsl:text>false</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>true</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
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