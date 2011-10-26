<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/operations_template.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/browse_table_js.xsl"/>

    <xsl:param name="page"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
    </xsl:variable>

    <xsl:template name="runtypestring">
      <xsl:param name="workentry"/>
      
      <xsl:choose>
        <!-- run once -->
        <xsl:when test="not($workentry) or $workentry/trigger/@type = 'simple' and $workentry/trigger/repeat/@count = 0">
          <xsl:text>%optRunOnce%</xsl:text>
        </xsl:when>

        <!-- run infinite -->
        <xsl:when test="$workentry/trigger/@type = 'simple' and $workentry/trigger/repeat/@count = -1">
          <xsl:text>%optRunInfinite%</xsl:text>
        </xsl:when>
        
        <!-- run repeatedly -->
        <xsl:when test="$workentry/trigger/@type = 'simple'">
          <xsl:text>%optRunRepeatedly%</xsl:text>
        </xsl:when>
        
        <!-- run hourly -->
        <xsl:when test="$workentry/trigger/@type = 'cron' and $workentry/trigger/hourly">
          <xsl:text>%optRunHourly%</xsl:text>
        </xsl:when>
        
        <!-- run daily -->
        <xsl:when test="$workentry/trigger/@type = 'cron' and $workentry/trigger/daily">
          <xsl:text>%optRunDaily%</xsl:text>
        </xsl:when>
        
        <!-- run custom -->
        <xsl:otherwise>
          <xsl:text>%optRunCustom%</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <xsl:template match="/">
        <html>
            <head>
            	<script type="text/javascript" src="javascript/admin.js">//</script>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
            </head>

            <body>
                <h1>
                    <xsl:call-template name="displaysystempath">
                        <xsl:with-param name="page" select="$page"/>
                    </xsl:call-template>
                </h1>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    <tr>
                        <td>
                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'"/>
                                <xsl:with-param name="caption" select="'%cmdNew%'"/>
                                <xsl:with-param name="href">
                                    <xsl:text>adminpage?page=</xsl:text>
                                    <xsl:value-of select="$page"/>
                                    <xsl:text>&amp;op=wizard</xsl:text>
                                    <xsl:text>&amp;name=createupdate</xsl:text>
                                    <xsl:text>&amp;create=1</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>
                        </td>
                    </tr>
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    <tr>
                        <td>
                            <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                                <tr>
                                    <xsl:variable name="datewidth">110</xsl:variable>
                                    <xsl:variable name="typewidth">80</xsl:variable>
                                    <xsl:variable name="intwidth">55</xsl:variable>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="caption" select="'%fldName%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="$typewidth"/>
                                        <xsl:with-param name="caption" select="'%fldRun%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="$datewidth"/>
                                        <xsl:with-param name="caption" select="'%fldNext%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'50'"/>
                                        <xsl:with-param name="caption" select="''"/>
                                        <xsl:with-param name="sortable" select="'false'"/>
                                    </xsl:call-template>
                                </tr>

                                <xsl:for-each select="/data/workentries/workentry">
                                    <tr>
                                        <xsl:call-template name="tablerowpainter"/>

                                      <xsl:variable name="css-class">
                                        <xsl:text>browsetablecell</xsl:text>
                                        <xsl:if test="position() = last()">
                                          <xsl:text> row-last</xsl:text>
                                        </xsl:if>
                                      </xsl:variable>


                                        <td class="{$css-class}" title="%msgClickToEdit%">
											<xsl:call-template name="addJSEvent">
												<xsl:with-param name="key" select="position()"/>
											</xsl:call-template>
										
                                            <xsl:value-of select="name"/>
                                        </td>
                                        <td class="{$css-class}" title="%msgClickToEdit%">
											<xsl:call-template name="addJSEvent">
												<xsl:with-param name="key" select="position()"/>
											</xsl:call-template>
                                            <xsl:call-template name="runtypestring">
                                              <xsl:with-param name="workentry" select="."/>
                                            </xsl:call-template>
                                        </td>
                                        <td class="{$css-class}" title="%msgClickToEdit%">
											<xsl:call-template name="addJSEvent">
												<xsl:with-param name="key" select="position()"/>
											</xsl:call-template>
											<xsl:call-template name="formatdate">
												<xsl:with-param name="date" select="trigger/time/@next"/>
											</xsl:call-template>
											<xsl:text>&nbsp;</xsl:text>
											<xsl:call-template name="formattime">
												<xsl:with-param name="date" select="trigger/time/@next"/>
												<xsl:with-param name="includeseconds" select="true()"/>
											</xsl:call-template>
											<!--xsl:value-of select="trigger/time/@next"/-->
                                        </td>
                                        <td align="center" class="{$css-class}">
                                            <table border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                  <td align="center" class="operationscell">
                                                    <xsl:call-template name="button">
                                                      <xsl:with-param name="style" select="'flat'"/>
                                                      <xsl:with-param name="type" select="'link'"/>
                                                      <xsl:with-param name="id">
                                                        <xsl:text>operation_edit_</xsl:text><xsl:value-of select="position()"/>
                                                      </xsl:with-param>
                                                      <xsl:with-param name="image" select="'images/icon_edit.gif'"/>
                                                      <xsl:with-param name="tooltip" select="'%altContentEdit%'"/>
                                                      <xsl:with-param name="href">
                                                        <xsl:text>adminpage?page=</xsl:text>
                                                        <xsl:value-of select="$page"/>
                                                        <xsl:text>&amp;op=wizard&amp;name=createupdate&amp;key=</xsl:text>
                                                        <xsl:value-of select="@key"/>
                                                      </xsl:with-param>
                                                    </xsl:call-template>
                                                  </td>
                                                  <td align="center" class="operationscell">
                                                    <xsl:call-template name="button">
                                                      <xsl:with-param name="style" select="'flat'"/>
                                                      <xsl:with-param name="type" select="'link'"/>
                                                      <xsl:with-param name="image" select="'images/icon_delete.gif'"/>
                                                      <xsl:with-param name="tooltip" select="'%altContentDelete%'"/>
                                                      <xsl:with-param name="href">
                                                        <xsl:text>adminpage?page=</xsl:text>
                                                        <xsl:value-of select="$page"/>
                                                        <xsl:text>&amp;op=remove&amp;key=</xsl:text>
                                                        <xsl:value-of select="@key"/>
                                                      </xsl:with-param>
                                                    </xsl:call-template>
                                                  </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </xsl:for-each>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>

