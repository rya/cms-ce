<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/textfielddate.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displaypath.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/logentryutils.xsl"/>
    <xsl:include href="common/dropdown_date.xsl"/>

    <xsl:param name="from" select="''"/>
    <xsl:param name="filter" select="''"/>
    <xsl:param name="currentdate" select="''"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
        <xsl:if test="$from != ''">
          <xsl:text>&amp;from=</xsl:text>
          <xsl:value-of select="$from"/>
        </xsl:if>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/log_filter.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/calendar_picker.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>

                <xsl:variable name="site">
                  <xsl:choose>
                    <xsl:when test="contains($filter, 'si')">
                      <xsl:text>"</xsl:text>
                      <xsl:value-of select="concat('si', substring-before(substring-after($filter, 'si'), ';'))"/>
                      <xsl:text>"</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>null</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:variable name="type">
                  <xsl:choose>
                    <xsl:when test="contains($filter, 'ty')">
                      <xsl:text>"</xsl:text>
                      <xsl:value-of select="concat('ta', substring-before(substring-after($filter, 'ta'), ';'))"/>
                      <xsl:text>;</xsl:text>
                      <xsl:value-of select="concat('ty', substring-before(substring-after($filter, 'ty'), ';'))"/>
                      <xsl:text>"</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>null</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:variable name="from">
                  <xsl:choose>
                    <xsl:when test="contains($filter, 'fr')">
                      <xsl:text>"</xsl:text>
                      <xsl:value-of select="substring-before(substring-after($filter, 'fr'), ';')"/>
                      <xsl:text>"</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>null</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:variable name="to">
                  <xsl:choose>
                    <xsl:when test="contains($filter, 'to')">
                      <xsl:text>"</xsl:text>
                      <xsl:value-of select="substring-before(substring-after($filter, 'to'), ';')"/>
                      <xsl:text>"</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>null</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <script type="text/javascript" language="JavaScript">
                  site = <xsl:value-of select="$site"/>;
                  type = <xsl:value-of select="$type"/>;
                  from = <xsl:value-of select="$from"/>;
                  to = <xsl:value-of select="$to"/>;
                </script>

                <!--script type="text/javascript" language="JavaScript">
                    var validatedFields = new Array(1);
                    validatedFields[0] = new Array("%fldName%", "name", validateRequired);


                    function validateAll(formName)
                    {
                      var f = document.forms[formName];

                      if ( !checkAll(formName, validatedFields) )
		                  return;

                      f.submit();
                    }

                </script-->
            </head>

            <body>
                <h1>
                    <xsl:call-template name="displaysystempath">
                        <xsl:with-param name="page" select="$page"/>
                        <xsl:with-param name="nolinks" select="false()"/>
                    </xsl:call-template>
                </h1>

                <form name="formAdmin" method="get">

                    <input type="hidden" id="_pageurl" value="{$pageURL}"/>

                    <table width="100%" border="0" cellspacing="0" cellpadding="2">
                        <!-- separator -->
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>

                        <!-- form -->
                        <tr>
                            <td>
                                <div class="tab-pane" id="tab-pane-1">

                                    <script type="text/javascript" language="JavaScript">
                                        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                    </script>

                                    <div class="tab-page" id="tab-page-1">
                                        <span class="tab">%blockFilter%</span>

                                        <script type="text/javascript" language="JavaScript">
                                            tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                        </script>

                                        <fieldset>
                                            <legend>&nbsp;%blockGeneral%&nbsp;</legend>
                                            <table border="0" cellspacing="2" cellpadding="0" width="100%">
                                              <tr>
                                                <xsl:call-template name="dropdown_site"/>
                                              </tr>
                                              <tr>
                                                <xsl:call-template name="dropdown_type"/>
                                              </tr>
                                            </table>
                                        </fieldset>

                                        <fieldset>
                                            <legend>&nbsp;%blockTimestamp%&nbsp;</legend>
                                            <table border="0" cellspacing="2" cellpadding="0" width="100%">
                                              <tr>
                                                <xsl:variable name="date">
                                                  <xsl:if test="contains($filter, 'fr')">
                                                    <xsl:value-of select="substring-before(substring-after($filter, 'fr'), ';')"/>
                                                  </xsl:if>
                                                </xsl:variable>

                                                <xsl:call-template name="dropdown_date">
                                                  <xsl:with-param name="name" select="'_from'"/>
                                                  <xsl:with-param name="label" select="'%fldFrom% (%fldMonth%/%fldYear%):'"/>
                                                  <xsl:with-param name="selectnode">
                                                    <xsl:choose>
                                                      <xsl:when test="$date != ''">
                                                        <xsl:value-of select="concat(substring($date, 7, 4),'-',substring($date, 4, 2),'-',substring($date, 1, 2))"/>
                                                      </xsl:when>
                                                    </xsl:choose>
                                                  </xsl:with-param>
                                                  <xsl:with-param name="currentdate" select="$currentdate"/>
                                                  <xsl:with-param name="colspan" select="'1'"/>
                                                  <xsl:with-param name="emptyrow" select="'%optChoose%'"/>
                                                  <xsl:with-param name="onchange">
                                                    <xsl:text>dateChanged("_from", this)</xsl:text>
                                                  </xsl:with-param>
                                                </xsl:call-template>
                                              </tr>
                                              <tr>
                                                <xsl:variable name="date">
                                                  <xsl:if test="contains($filter, 'to')">
                                                    <xsl:value-of select="substring-before(substring-after($filter, 'to'), ';')"/>
                                                  </xsl:if>
                                                </xsl:variable>

                                                <xsl:call-template name="dropdown_date">
                                                  <xsl:with-param name="name" select="'_to'"/>
                                                  <xsl:with-param name="label" select="'%fldTo% (%fldMonth%/%fldYear%):'"/>
                                                  <xsl:with-param name="selectnode">
                                                    <xsl:choose>
                                                      <xsl:when test="$date != ''">
                                                        <xsl:value-of select="substring($date, 7, 4)"/>
                                                        <xsl:text>-</xsl:text>
                                                        <xsl:choose>
                                                          <xsl:when test="number(substring($date, 4, 2)) = 1">
                                                            <xsl:text>12</xsl:text>
                                                          </xsl:when>
                                                          <xsl:otherwise>
                                                            <xsl:if test="number(substring($date, 4, 2)) &lt; 11">
                                                              <xsl:text>0</xsl:text>
                                                            </xsl:if>
                                                            <xsl:value-of select="number(substring($date, 4, 2)) - 1"/>
                                                          </xsl:otherwise>
                                                        </xsl:choose>
                                                        <xsl:value-of select="substring($date, 4, 2)"/>
                                                        <xsl:text>-</xsl:text>
                                                        <xsl:value-of select="substring($date, 1, 2)"/>
                                                      </xsl:when>
                                                    </xsl:choose>
                                                  </xsl:with-param>
                                                  <xsl:with-param name="currentdate" select="$currentdate"/>
                                                  <xsl:with-param name="colspan" select="'1'"/>
                                                  <xsl:with-param name="emptyrow" select="'%optChoose%'"/>
                                                  <xsl:with-param name="onchange">
                                                    <xsl:text>dateChanged("_to", this)</xsl:text>
                                                  </xsl:with-param>
                                                </xsl:call-template>
                                              </tr>
                                            </table>
                                        </fieldset>
                                    </div>

                                </div>

                                <script type="text/javascript" language="JavaScript">
                                    setupAllTabs();
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td >
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'link'"/>
                                    <xsl:with-param name="caption" select="'%cmdApplyFilter%'"/>
                                    <xsl:with-param name="name" select="'applyfilter'"/>
                                    <xsl:with-param name="href" select="'javascript:applyFilter();'"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </form>

            </body>
        </html>
    </xsl:template>

    <xsl:template name="dropdown_site">
        <td valign="baseline" nowrap="nowrap" class="form_labelcolumn">%fldSite%:</td>
        <td nowrap="nowrap">
            <select id="_site">
              <xsl:attribute name="onchange">siteChanged(this)</xsl:attribute>

              <option value="none">%optChoose%</option>
              <option value="sia">
                <xsl:if test="contains($filter, 'sia')">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optAdminConsole%</xsl:text>
              </option>

              <xsl:for-each select="/data/menus/menu">
                <option>
                  <xsl:if test="contains($filter, concat('si', @key))">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>

                  <xsl:attribute name="value">
                    <xsl:text>si</xsl:text>
                    <xsl:value-of select="@key"/>
                  </xsl:attribute>
                  <xsl:value-of select="name"/>
                </option>
              </xsl:for-each>
            </select>
        </td>
    </xsl:template>

    <xsl:template name="dropdown_type">

        <td valign="baseline" nowrap="nowrap" class="form_labelcolumn">%fldType%:</td>
        <td nowrap="nowrap">
            <select id="_type">
              <xsl:attribute name="onchange">typeChanged(this)</xsl:attribute>

              <option value="none">%optChoose%</option>

              <xsl:if test="contains($filter, 'sia') or not(contains($filter, 'si'))">
                  <option value="ty0">
                    <xsl:if test="contains($filter, 'ty0')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="typekey" select="0"/>
                    </xsl:call-template>
                  </option>
                  <option value="ty2">
                    <xsl:if test="contains($filter, 'ty2')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="typekey" select="2"/>
                    </xsl:call-template>
                  </option>
                  <option value="ty3">
                    <xsl:if test="contains($filter, 'ty3')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="typekey" select="3"/>
                    </xsl:call-template>
                  </option>
                  <option value="ty1">
                    <xsl:if test="contains($filter, 'ty1')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="typekey" select="1"/>
                    </xsl:call-template>
                  </option>
                  <option value="ta0;ty4">
                    <xsl:if test="contains($filter, 'ta0;ty4')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="0"/>
                      <xsl:with-param name="typekey" select="4"/>
                    </xsl:call-template>
                  </option>
                  <option value="ta0;ty5">
                    <xsl:if test="contains($filter, 'ta0;ty5')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="0"/>
                      <xsl:with-param name="typekey" select="5"/>
                    </xsl:call-template>
                  </option>
                  <option value="ta0;ty6">
                    <xsl:if test="contains($filter, 'ta0;ty6')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="0"/>
                      <xsl:with-param name="typekey" select="6"/>
                    </xsl:call-template>
                  </option>
              </xsl:if>

              <xsl:if test="contains($filter, concat('si', @key)) or not(contains($filter, 'si'))">
                  <option value="ta0;ty7">
                    <xsl:if test="contains($filter, 'ta0;ty7')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="0"/>
                      <xsl:with-param name="typekey" select="7"/>
                    </xsl:call-template>
                  </option>
              </xsl:if>

              <xsl:if test="contains($filter, 'sia') or not(contains($filter, 'si'))">
                  <option value="ta1;ty4">
                    <xsl:if test="contains($filter, 'ta1;ty4')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="1"/>
                      <xsl:with-param name="typekey" select="4"/>
                    </xsl:call-template>
                  </option>
                  <option value="ta1;ty5">
                    <xsl:if test="contains($filter, 'ta1;ty5')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="1"/>
                      <xsl:with-param name="typekey" select="5"/>
                    </xsl:call-template>
                  </option>
                  <option value="ta1;ty6">
                    <xsl:if test="contains($filter, 'ta1;ty6')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="1"/>
                      <xsl:with-param name="typekey" select="6"/>
                    </xsl:call-template>
                  </option>
              </xsl:if>

              <xsl:if test="contains($filter, concat('si', @key)) or not(contains($filter, 'si'))">
                  <option value="ta1;ty7">
                    <xsl:if test="contains($filter, 'ta1;ty7')">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="actionstring">
                      <xsl:with-param name="tablekey" select="1"/>
                      <xsl:with-param name="typekey" select="7"/>
                    </xsl:call-template>
                  </option>
              </xsl:if>
            </select>
        </td>
    </xsl:template>

</xsl:stylesheet>
