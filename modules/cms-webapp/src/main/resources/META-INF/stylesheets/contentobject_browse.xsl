<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" />

  <xsl:include href="common/generic_parameters.xsl" />
  <xsl:include href="common/operations_template.xsl" />
  <xsl:include href="common/javascriptPreload.xsl" />
  <xsl:include href="common/genericheader.xsl" />
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/tablecolumnheader.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/tablerowpainter.xsl"/>
  <xsl:include href="common/browse_table_js.xsl"/>
  <xsl:include href="common/escapequotes.xsl"/>

  <xsl:param name="sortby" select="'name'"/>
  <xsl:param name="sortby-direction" select="'ascending'"/>
  <xsl:param name="subop"/>
  <xsl:param name="fieldname"/>
  <xsl:param name="fieldrow"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
    <xsl:text>&amp;op=browse</xsl:text>
    <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
    <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
    <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
    <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
  </xsl:variable>

  <xsl:variable name="includeparams">
    <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
    <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
    <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
  </xsl:variable>

  <xsl:template match="/">
    <xsl:call-template name="contentobjectbrowse" />
  </xsl:template>

  <xsl:template name="contentobjectbrowse">
    <html>
      <head>
        <xsl:call-template name="javascriptPreload" />
        <link href="css/admin.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="javascript/admin.js">//</script>

        <xsl:if test="$subop = 'browsepopup'">
          <script type="text/javascript" src="javascript/window.js"/>
          <script type="text/javascript">
            cms.window.attatchKeyEvent('close');
          </script>
        </xsl:if>
              
      </head>

      <xsl:if test="$fieldname !='' or $fieldrow !=''">
        <script type="text/javascript" language="JavaScript">
          function browsepopup_callback(key, title) {
            if (window.top.opener.contentObjectList[key] == 'added') {
              alert("%alertContentPortletAlreadyAdded%");
              return;
            } else {
              window.top.opener.contentObjectList[key] = 'added';
              window.top.opener.callback_selectObject('<xsl:value-of select="$fieldname"/>', <xsl:value-of select="$fieldrow"/>, key, title);
              window.close();
            }
          }
        </script>
      </xsl:if>

      <body>
        <h1>
          <xsl:call-template name="genericheader">
            <xsl:with-param name="links" select="not($subop)"/>
          </xsl:call-template>
          <a>
            <xsl:attribute name="href">
              <xsl:text>adminpage?page=900&amp;op=browse</xsl:text>
              <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
              <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
              <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
              <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
            </xsl:attribute>
            <xsl:text>%headPortlets%</xsl:text>
          </a>
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
                  <xsl:text>&amp;op=form</xsl:text>
                  <xsl:text>&amp;menukey=</xsl:text>
                  <xsl:value-of select="$menukey"/>
                  <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
                  <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
                  <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
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

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldName%'" />
                    <xsl:with-param name="pageURL" select="$pageURL" />
                    <xsl:with-param name="current-sortby" select="$sortby" />
                    <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                    <xsl:with-param name="sortby" select="'name'" />
                  </xsl:call-template>


                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'100'" />
                    <xsl:with-param name="align" select="'center'" />
                    <xsl:with-param name="caption" select="'%fldCaching%'" />
                    <xsl:with-param name="sortable" select="'false'" />
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'100'" />
                    <xsl:with-param name="align" select="'center'" />
                    <xsl:with-param name="caption" select="'%fldModified%'" />
                    <xsl:with-param name="pageURL" select="$pageURL" />
                    <xsl:with-param name="current-sortby" select="$sortby" />
                    <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                    <xsl:with-param name="sortby" select="'timestamp'" />
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'90'" />
                    <xsl:with-param name="caption" select="''" />
                    <xsl:with-param name="sortable" select="'false'" />
                  </xsl:call-template>

                </tr>

                <xsl:variable name="sortby-data-type">text</xsl:variable>

                <xsl:for-each select="/contentobjects/contentobject">
                  <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}" select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>

                  <xsl:variable name="className">
                    <xsl:choose>
                      <xsl:when test="objectstylesheet/@exists = 'false' or borderstylesheet/@exists = 'false'">
                        <xsl:text>browsetablecellred</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>browsetablecell</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="position() = last()">
                      <xsl:text> row-last</xsl:text>
                    </xsl:if>
                  </xsl:variable>

                  <tr>
                    <xsl:call-template name="tablerowpainter"/>
                    <td class="{$className}" title="%msgClickToEdit%">
                      <xsl:call-template name="addEvent"/>
                      <xsl:value-of select="name"/>
                    </td>
                    <td class="{$className}" title="%msgClickToEdit%" style="text-align: center">
                      <xsl:call-template name="addEvent"/>
                      <xsl:choose>
                        <xsl:when test="contentobjectdata/@cachedisabled = 'true'">
                          %cacheIsOff%
                        </xsl:when>
                        <xsl:when
                            test="contentobjectdata/datasources/@session or contentobjectdata/datasources/@sessioncontext or contentobjectdata/datasources/@ticket or contentobjectdata/datasources/@requestcontext or contentobjectdata/datasources/@cookiecontext or contentobjectdata/datasources/@ticketcontext or contentobjectdata/datasources/@httpcontext">
                          %cacheIsDisabled%
                        </xsl:when>
                        <xsl:when test="contentobjectdata/@cachedisabled != 'true'">
                          <xsl:choose>
                            <xsl:when test="contentobjectdata/@cachetype = 'specified'">
                              <xsl:value-of select="contentobjectdata/@mincachetime"/>
                              %seconds%
                            </xsl:when>
                            <xsl:when test="contentobjectdata/@cachetype = 'forever'">
                              %optCacheForever%
                            </xsl:when>
                            <xsl:when test="contentobjectdata/@cachetype = 'default'">
                              %optDefaultCacheTime%
                            </xsl:when>
                            <xsl:otherwise>
                              %optDefaultCacheTime%
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:when>
                      </xsl:choose>
                    </td>

                    <td align="center" class="{$className}">
                      <xsl:call-template name="addEvent"/>

                      <xsl:call-template name="formatdate">
                        <xsl:with-param name="date" select="timestamp"/>
                      </xsl:call-template>
                      <xsl:text>&nbsp;</xsl:text>
                      <xsl:call-template name="formattime">
                        <xsl:with-param name="date" select="timestamp"/>
                      </xsl:call-template>
                    </td>
                    <td align="center" width="80" class="{$className}">
                      <xsl:call-template name="operations">
                        <xsl:with-param name="page" select="$page"/>
                        <xsl:with-param name="key" select="@key"/>
                        <xsl:with-param name="includecopy" select="'true'"/>
                        <xsl:with-param name="includeparams" select="$includeparams"/>
                      </xsl:call-template>
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

  <xsl:template name="addEvent">
    <xsl:choose>
      <xsl:when test="$subop = 'browsepopup'">
        <xsl:attribute name="title">
          <xsl:text>%msgClickToSelect%</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="onclick">
          <xsl:text>browsepopup_callback(</xsl:text>
          <xsl:value-of select="@key"/>
          <xsl:text>, '</xsl:text>
          <xsl:call-template name="escapequotes">
            <xsl:with-param name="string" select="name"/>
          </xsl:call-template>
          <xsl:text>');</xsl:text>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="title">
          <xsl:text>%msgClickToEdit%</xsl:text>
        </xsl:attribute>
        <xsl:call-template name="addJSEvent">
          <xsl:with-param name="key" select="@key"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>