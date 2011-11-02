<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/escapequotes.xsl"/>
  <xsl:include href="handlerconfigs/default.xsl"/>
  <xsl:include href="common/button.xsl"/>

  <xsl:param name="callback" select="''"/>

  <xsl:param name="filter" select="''"/>

  <xsl:template match="/">

    <title>%fldSelectPage%:</title>

    <script type="text/javascript" src="javascript/window.js"/>

    <script type="text/javascript">
      cms.window.attatchKeyEvent('close');
    </script>

    <script type="text/javascript" language="JavaScript">

      function selectMenuItem( menuItemKey, menuItemPath )
      {
        window.opener.<xsl:value-of select="$callback"/>( menuItemKey, menuItemPath );
        window.close();
      }

    </script>

    <script src="javascript/menu.js"/>
    <link rel="stylesheet" type="text/css" href="css/menu.css"/>
    <link rel="stylesheet" type="text/css" href="css/admin.css"/>


    <body>

      <xsl:for-each select="/menuitems-across-sites/site">
        <xsl:variable name="site-name" select="name"/>

        <fieldset>
          <legend>
            <img src="images/icon_sites.gif" alt="{$site-name}" title="{$site-name}" style="vertical-align: middle;"/>
            <xsl:text> </xsl:text>
            <span style="color: #000">
              <xsl:value-of select="$site-name"/>
            </span>
          </legend>

          <table border="0" cellpadding="0" cellspacing="0" style="width:100%">
            <tr>
              <td>
                <table border="0" cellspacing="0" cellpadding="3">
                  <tr>
                    <th align="left" style="width: 290px">%fldPage%</th>
                  </tr>
                  <xsl:for-each select="menuitems/menuitem">
                    <xsl:variable name="menuitem-key" select="@key"/>
                    <tr>

                      <td>
                        <table border="0" cellpadding="0" cellspacing="0">
                          <tr>
                            <td>
                              <xsl:variable name="altText">
                                <xsl:choose>
                                  <xsl:when test="page/page-template/type = 'content'">
                                    <xsl:text>%optPageTemplateTypeContent%</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="page/page-template/type = 'sectionpage'">
                                    <xsl:text>%optPageTemplateTypeSectionPage%</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="page/page-template/type = 'newsletter'">
                                    <xsl:text>%optPageTemplateTypeNewsletter%</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="type = 'section'">
                                    <xsl:text>%optSection%</xsl:text>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:text>%optUnknown%</xsl:text>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </xsl:variable>

                              <img border="0" alt="{$altText}" title="{$altText}">
                                <xsl:attribute name="src">
                                  <xsl:text>images/icon_menuitem</xsl:text>
                                  <xsl:choose>
                                    <xsl:when test="page/page-template/type = 'content'">
                                      <xsl:text>_content</xsl:text>
                                    </xsl:when>
                                    <xsl:when test="page/page-template/type = 'sectionpage'">
                                      <xsl:text>_sectionpage</xsl:text>
                                    </xsl:when>
                                    <xsl:when test="page/page-template/type = 'newsletter'">
                                      <xsl:text>_standard</xsl:text>
                                    </xsl:when>
                                    <xsl:when test="type = 'section'">
                                      <xsl:text>_section</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:text>_unknown</xsl:text>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                  <xsl:if test="show-in-menu = 'true'">_show</xsl:if>
                                  <xsl:if test="@anonaccess = 'false'">_lock</xsl:if>
                                  <xsl:text>.gif</xsl:text>
                                </xsl:attribute>
                              </img>
                            </td>
                            <td>&nbsp;</td>
                            <td>
                              <a href="javascript:;" onclick="javascript: selectMenuItem( {@key}, '{ concat( $site-name, ': ', path ) }' )">
                                <xsl:value-of select="path"/>
                              </a>
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
        </fieldset>

      </xsl:for-each>

    </body>
  </xsl:template>


</xsl:stylesheet>
