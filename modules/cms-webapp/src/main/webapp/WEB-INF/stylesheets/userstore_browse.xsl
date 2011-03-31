<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/operations_template.xsl"/>
  <xsl:include href="common/waitsplash.xsl"/>
  <xsl:include href="common/accesslevel_parameters.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/tablecolumnheader.xsl"/>
  <xsl:include href="common/tablerowpainter.xsl"/>
  <xsl:include href="common/displayuserstorepath.xsl"/>
  <xsl:include href="common/browse_table_js.xsl"/>

  <xsl:param name="reload"/>
  <xsl:param name="sortby" select="'@name'"/>
  <xsl:param name="sortby-direction" select="'ascending'"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?page=</xsl:text>
    <xsl:value-of select="$page"/>
    <xsl:text>&amp;op=browse</xsl:text>
  </xsl:variable>

  <xsl:template match="/">
    <html>
      <head>
        <script type="text/javascript" language="JavaScript" src="javascript/admin.js">//</script>

        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <xsl:call-template name="waitsplash"/>

        <xsl:if test="$reload = 'true'">
          <script type="text/javascript" language="JavaScript">
            parent.leftFrame.location.href =
            <xsl:text>"adminpage?page=2&amp;op=browse&amp;selecteddomainkey=</xsl:text>
            <xsl:value-of select="$selecteddomainkey"/><xsl:text>";</xsl:text>
          </script>
        </xsl:if>
      </head>

      <body>
        <h1>
          <xsl:call-template name="displayuserstorepath"/>
        </h1>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="browse_title_buttonrow_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
          <xsl:if test="$enterpriseadmin = 'true'">
            <tr>
              <td>
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdNew%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=form</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </td>
            </tr>
          </xsl:if>
          <tr>
            <td class="browse_buttonrow_datarows_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
          <tr>
            <td>
              <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                <tr>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldName%'"/>
                    <xsl:with-param name="pageURL" select="$pageURL"/>
                    <xsl:with-param name="current-sortby" select="$sortby"/>
                    <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                    <xsl:with-param name="sortby" select="'@name'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldType%'"/>
                    <xsl:with-param name="pageURL" select="$pageURL"/>
                    <xsl:with-param name="current-sortby" select="$sortby"/>
                    <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                    <xsl:with-param name="sortby" select="'@remote'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'90'"/>
                    <xsl:with-param name="align" select="'center'"/>
                    <xsl:with-param name="caption" select="'%fldDefaultUserStore%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>
                  
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'90'"/>
                    <xsl:with-param name="caption" select="''"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>
                </tr>

                <xsl:variable name="sortby-data-type">text</xsl:variable>

                <xsl:for-each select="/userstores/userstore">
                  <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}"
                            select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>

                  <xsl:variable name="css-td-class">
                    <xsl:text>browsetablecell</xsl:text>
                    <xsl:if test="connector/config/errors/error != ''">
                      <xsl:text> browsetablecellredarrow</xsl:text>
                    </xsl:if>
                    <xsl:if test="position() = last()">
                      <xsl:text> row-last</xsl:text>
                    </xsl:if>
                  </xsl:variable>

                  <tr>
                    <xsl:call-template name="tablerowpainter"/>
                    <td class="{$css-td-class}" title="%msgClickToEdit%">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>
                      <xsl:value-of select="@name"/>
                    </td>
                    <td class="{$css-td-class}" title="%msgClickToEdit%">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>
                      <xsl:choose>
                        <xsl:when test="@remote = 'true'">
                          <xsl:text>%txtRemote%</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:text>%txtLocal%</xsl:text>
                        </xsl:otherwise>
                      </xsl:choose>
                    </td>
                    <td class="{$css-td-class}" title="%msgClickToEdit%" style="text-align: center">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>

                      <xsl:choose>
                        <xsl:when test="@default = 'true'">
                          <img src="images/icon_check_noborder.gif" alt=""/>
                        </xsl:when>
                        <xsl:otherwise>
                          <br/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </td>
                    <td align="center" class="{$css-td-class}">

                      <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td>
                            <xsl:variable name="enableDelete" select="not(@default = 'true')"/>
                            <xsl:call-template name="operations">
                              <xsl:with-param name="page" select="$page"/>
                              <xsl:with-param name="key" select="@key"/>
                              <xsl:with-param name="includecopy" select="'false'"/>
                              <xsl:with-param name="includeparams" select="'&amp;fromsystem=true'"/>
                              <xsl:with-param name="enabledelete" select="$enableDelete"/>
                              <xsl:with-param name="JSdeleteCallback" select="'waitsplash();'"/>
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

