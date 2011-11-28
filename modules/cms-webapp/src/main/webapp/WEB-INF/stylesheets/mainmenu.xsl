<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:include href="common/menu_site.xsl"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/accesslevel_parameters.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="handlerconfigs/default.xsl"/>
  <xsl:include href="tree/displaytree.xsl"/>
  <xsl:param name="loadmainstartpage"/>
  <xsl:param name="scheduler"/>
  <xsl:param name="defaultuserstorekey"/>
  <xsl:param name="selectedmenukey"/>

  <xsl:variable name="splash-servlet-then-redirect-to-dashboard" select="concat('adminpage?page=5&amp;redirect=', admin:urlEncode('adminpage?page=960&amp;op=page'))"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/menu.css"/>

        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>

        <script type="text/javascript" language="JavaScript">
          var branchOpen = new Array();

          var allcookies = document.cookie;
          var cookiename =
          <xsl:text>'adminmenu</xsl:text>
          <xsl:value-of select="$selecteddomainkey"/>
          <xsl:text>';</xsl:text>
          var pos = allcookies.indexOf(cookiename + "=");

          if (pos != -1 )
          {
            var start = pos + cookiename.length + 1;
            var end = allcookies.indexOf(";", start);
            if (end == -1)
              end = allcookies.length;
            var values = allcookies.substring(start, end).split(',');
            for ( var i in values )
            {
              branchOpen[values[i]] = true;
            }
          }
        </script>
        <script type="text/javascript" language="JavaScript">
          <xsl:if test="$loadmainstartpage = 'true'">
            <xsl:choose>
              <xsl:when test="false()">
                parent.mainFrame.location.href = "adminpage?page=10&amp;op=page";
              </xsl:when>
              <xsl:otherwise>
                parent.mainFrame.location.href = "<xsl:value-of select="$splash-servlet-then-redirect-to-dashboard"/>";
              </xsl:otherwise>
            </xsl:choose>
          </xsl:if>

          function refreshMenu()
          {
            var location = window.location.href;
            var idx = location.indexOf("&amp;loadmainstartpage");
            if ( idx > 0 )
            {
              var end = location.indexOf("&amp;", idx + 1);
              var tmp = location.substring(0, idx);
              if (end > 0)
                tmp = tmp + location.substring(end, location.length);

              location = tmp;
            }

            location = location + "&amp;waitscreen=true";
            window.location = location;
          }

          function scrollToPageOffset() {
            var x = window.parent.topFrame.leftFrameXOffset || 0;
            var y = window.parent.topFrame.leftFrameYOffset || 0;
            window.scrollTo(x, y);
          }

          window.onunload = function()
          {
            window.parent.topFrame.setPageOffsetVal(window);
          }
        </script>

      </head>

      <body onload="javascript:openTree();scrollToPageOffset()" id="mainmenu">
        <form name="data">
        </form>
        <form method="POST" action="adminpage?page=5" name="splash">
          <input type="hidden" name="redirect">
            <xsl:attribute name="value">
              <xsl:text>adminpage?page=2&amp;op=browse</xsl:text>
            </xsl:attribute>
          </input>
        </form>

        <div class="dynamic-tab-pane-control tab-pane" id="tab-pane-1">
          <div class="tab-page" id="tab-page-domains" style="overflow:auto">
            <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
              <xsl:call-template name="tree"/>
            </table>
            <div id="mainmenu-refresh-button-container">
              <a href="javascript:;" onclick="javascript:refreshMenu();" id="mainmenu-refresh-button">
                 <img src="images/action_refresh_blue.gif" alt="%cmdRefreshMenu%" title="%cmdRefreshMenu%" width="16" height="16"/>
              </a>
            </div>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="tree">
    <xsl:apply-templates select="/sites">
      <xsl:sort select="name"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="sites">
    <xsl:variable name="href">
      <xsl:text>adminpage?page=300&amp;op=page</xsl:text>
      <xsl:text>&amp;selecteddomainkey=</xsl:text>
      <xsl:value-of select="$selecteddomainkey"/>
    </xsl:variable>
    <tr>
      <td>
        <img src="images/icon_domain.png" border="0"/>
        <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
        Enonic CMS
      </td>
    </tr>
    <tr>
      <td>
        <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
          <xsl:call-template name="selectedSitegroup"/>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="selectedSitegroup">

    <xsl:variable name="showMenuTree" select="$siteadmin = 'true' or /sites/menus/menu"/>
    <xsl:variable name="showContenttypes" select="$siteadmin = 'true' or $developer = 'true'"/>
    <xsl:variable name="showCategoryTree" select="$siteadmin = 'true' or /sites/categories/category"/>

    <!-- Dashboard -->
    <tr>
      <td width="16">
        <xsl:choose>
          <xsl:when test="$showMenuTree or $showContenttypes or $showCategoryTree">
            <img src="javascript/images/T.png" border="0"/>
          </xsl:when>
          <xsl:otherwise>
            <img src="javascript/images/L.png" border="0"/>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <a href="{$splash-servlet-then-redirect-to-dashboard}" target="mainFrame">
          <img src="images/user-active.gif" border="0"/>
          <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
          <xsl:text>%mnuDashboard%</xsl:text>
        </a>
      </td>
    </tr>


    <!-- System -->
    <xsl:if test="$enterpriseadmin = 'true'">
      <tr>
        <td width="16">
          <a>
            <xsl:attribute name="href">
              <xsl:text>javascript:openBranch('-system-java-properties');</xsl:text>
            </xsl:attribute>
            <img id="img-system-java-properties" src="javascript/images/Tplus.png" border="0"/>
          </a>
        </td>
        <td>
          <a target="mainFrame" href="adminpage?page=10&amp;op=page&amp;mode=system">
            <img src="images/icon_system.gif" border="0"/>
            <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
            <xsl:text>%mnuSystem%</xsl:text>
          </a>
        </td>
      </tr>
      <tr valign="top" id="id-system-java-properties" style="display: none;">
        <td width="16">
          <xsl:attribute name="background">
            <xsl:text>javascript/images/I.png</xsl:text>
          </xsl:attribute>
          <img src="images/shim.gif" border="0"/>
        </td>
        <td>
          <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
            <tr>
              <td width="16">
                <img src="javascript/images/T.png" border="0"/>
              </td>
              <td width="16">
                <a href="adminpage?page=10&amp;op=page&amp;mode=java_properties" target="mainFrame">
                  <img src="images/document-properties.png" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuProperties%</xsl:text>
                </a>
              </td>
            </tr>
            <tr>
              <td width="16">
                <img src="javascript/images/T.png" border="0"/>
              </td>
              <td>
                <a target="mainFrame" href="adminpage?page=10&amp;op=page&amp;mode=system_cache">
                  <img src="images/icon_system.gif" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuSystemCaches%</xsl:text>
                </a>
              </td>
            </tr>
            <tr>
              <td width="16">
                <img src="javascript/images/T.png" border="0"/>
              </td>
              <td>
                <a href="adminpage?page=1050&amp;op=browse" target="mainFrame">
                  <img src="images/icon_contenttypes.gif" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuContentHandlers%</xsl:text>
                </a>
              </td>
            </tr>
            <tr>
              <td width="16">
                <img src="javascript/images/T.png" border="0"/>
              </td>
              <td>
                <a href="adminpage?page=360&amp;op=browse" target="mainFrame">
                  <img src="images/icon_language.gif" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuLanguages%</xsl:text>
                </a>
              </td>
            </tr>
            <tr>
              <td width="16">
                <xsl:choose>
                  <xsl:when test="$scheduler = 'true'">
                    <img src="javascript/images/T.png" border="0"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <img src="javascript/images/L.png" border="0"/>
                  </xsl:otherwise>
                </xsl:choose>
              </td>
              <td>
                <a href="adminpage?page=350&amp;op=browse" target="mainFrame">
                  <img src="images/icon_logs.gif" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuEventLog%</xsl:text>
                </a>
              </td>
            </tr>
            <tr>
              <td width="16">
                <img src="javascript/images/T.png" border="0"/>
              </td>
              <td>
                <a href="adminpage?page=910&amp;op=pluginInfo" target="mainFrame">
                  <img src="images/icon_plugins.png" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuPluginInfo%</xsl:text>
                </a>
              </td>
            </tr>
              <tr>
              <td width="16">
                <img src="javascript/images/T.png" border="0"/>
              </td>
              <td>
                <a href="adminpage?page=912&amp;op=liveportaltrace" target="mainFrame">
                  <img src="images/utilities-system-monitor.png" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:text>%mnuLivePortalTrace%</xsl:text>
                </a>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </xsl:if>


    <!-- Resources -->
    <xsl:if test="/sites/resources">
      <xsl:variable name="url">
        <xsl:text>adminpage?page=800&amp;fieldname=%20&amp;move=true</xsl:text>
      </xsl:variable>
      <xsl:apply-templates select="/sites/resources" mode="displaytree">
        <xsl:with-param name="topnode" select="false()"/>
        <xsl:with-param name="url" select="$url"/>
        <xsl:with-param name="hassiblingoverride" select="true()"/>
      </xsl:apply-templates>
    </xsl:if>


    <!-- Contenttypes -->
    <xsl:if test="$showContenttypes">
      <table class="menuItem" cellspacing="0" cellpadding="0">
        <tr>
          <td>
            <xsl:choose>
              <xsl:when test="$showCategoryTree">
                <img src="javascript/images/T.png" border="0"/>
              </xsl:when>
              <xsl:otherwise>
                <img src="javascript/images/L.png" border="0"/>
              </xsl:otherwise>
            </xsl:choose>
          </td>
          <td>
            <a href="adminpage?page=400&amp;op=browse&amp;selecteddomainkey={$selecteddomainkey}" target="mainFrame">
              <img src="images/icon_contenttypes.gif" border="0"/>
              <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
              <xsl:text>%mnuLocalContentTypes%</xsl:text>
            </a>
          </td>
        </tr>
      </table>
    </xsl:if>

    <!--
      Userstores
      Note The <userstores> XML is pre-filtered and contains only userstores accessible for the logged in user.
    -->
    <xsl:if test="count(/sites/userstores/userstore) &gt; 0 or $siteadmin = 'true' or $enterpriseadmin = 'true'">
      <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
        <tr>
          <td width="16">
            <a>
              <xsl:attribute name="href">
                <xsl:text>javascript:openBranch('-system-userstores');</xsl:text>
              </xsl:attribute>
              <img id="img-system-userstores" src="javascript/images/Tplus.png" border="0"/>
            </a>
          </td>
          <td>
            <a href="adminpage?page=290&amp;op=browse" target="mainFrame">
              <img src="images/icon_userstore.gif" border="0"/>
              <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
              <xsl:text>%mnuUserstores%</xsl:text>
            </a>
          </td>
        </tr>

        <tr valign="top" id="id-system-userstores" style="display: none;">
          <td width="16">
            <xsl:attribute name="background">
              <xsl:text>javascript/images/I.png</xsl:text>
            </xsl:attribute>
            <img src="images/shim.gif" border="0"/>
          </td>
          <td>

            <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
              <xsl:if test="$siteadmin = 'true'">
                <tr valign="top" id="id-domains">
                  <td width="16">
                    <xsl:choose>
                      <xsl:when test="/sites/userstores/userstore">
                        <img src="javascript/images/T.png" border="0"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <img src="javascript/images/L.png" border="0"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                  <td>
                    <a href="adminpage?page=700&amp;op=browse&amp;mode=globalgroups" target="mainFrame">
                      <img src="images/icon_groups.gif" border="0"/>
                      <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                      <xsl:text>%mnuGlobalGroups%</xsl:text>
                    </a>
                  </td>
                </tr>
              </xsl:if>


              <xsl:for-each select="/sites/userstores/userstore">
                <xsl:sort select="@name"/>
                <tr>
                  <td width="16">
                    <a>
                      <xsl:attribute name="href">
                        <xsl:text>javascript:openBranch('-domain</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>');</xsl:text>
                      </xsl:attribute>
                      <xsl:choose>
                        <xsl:when test="position() = last()">
                          <img id="img-domain{@key}" src="javascript/images/Lplus.png" border="0"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <img id="img-domain{@key}" src="javascript/images/Tplus.png" border="0"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </a>
                  </td>
                  <td>
                    <a target="mainFrame" href="adminpage?page=290&amp;op=page&amp;key={@key}">
                      <img src="images/icon_userstore.gif" border="0"/>
                      <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                      <xsl:value-of select="@name"/>
                    </a>
                  </td>
                </tr>
                <tr valign="top" id="id-domain{@key}" style="display: none;">
                  <td width="16">
                    <xsl:if test="position() != last()">
                      <xsl:attribute name="background">
                        <xsl:text>javascript/images/I.png</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <img src="images/shim.gif" border="0"/>
                  </td>
                  <td>
                    <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                      <tr>
                        <td width="16">
                          <img src="javascript/images/T.png" border="0"/>
                        </td>
                        <td width="16">
                          <a href="adminpage?page=700&amp;op=browse&amp;userstorekey={@key}" target="mainFrame">
                            <img src="images/icon_users.gif" border="0"/>
                            <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                            <xsl:text>%mnuUsers%</xsl:text>
                          </a>
                        </td>
                      </tr>
                      <tr>
                        <td width="16">
                          <img src="javascript/images/L.png" border="0"/>
                        </td>
                        <td width="16">
                          <a href="adminpage?page=700&amp;op=browse&amp;userstorekey={@key}&amp;mode=groups" target="mainFrame">
                            <img src="images/icon_groups.gif" border="0"/>
                            <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                            <xsl:text>%mnuGroups%</xsl:text>
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
    </xsl:if>


    <!-- Sites -->
    <xsl:if test="$showMenuTree">
      <xsl:variable name="url">
        <xsl:text>adminpage?mainmenu=true</xsl:text>
      </xsl:variable>
      <xsl:apply-templates select="/sites/menus" mode="displaytree">
        <xsl:with-param name="url" select="$url"/>
        <xsl:with-param name="topnode" select="false()"/>
        <xsl:with-param name="hassiblingoverride" select="($siteadmin = 'true' or $enterpriseadmin = 'true')"/>
        <xsl:with-param name="selectedmenukey" select="$selectedmenukey"/>
      </xsl:apply-templates>
    </xsl:if>


    <!-- Categories -->
    <xsl:if test="$showCategoryTree">
      <xsl:variable name="url">
        <xsl:text>adminpage?mainmenu=true</xsl:text>
      </xsl:variable>

      <xsl:apply-templates select="/sites/categories" mode="displaytree">
        <xsl:with-param name="url" select="$url"/>
        <xsl:with-param name="topnode" select="false()"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>

  <xsl:template name="menu_href_pagebuilder">
    <xsl:param name="xpathMenu"/>
    <xsl:text>adminpage?page=850&amp;op=browse&amp;menukey=</xsl:text>
    <xsl:value-of select="$xpathMenu/@key"/>
    <xsl:text>&amp;selectedunitkey=</xsl:text>
    <xsl:value-of select="$xpathMenu/@unitkey"/>
  </xsl:template>

  <xsl:template name="menu_href_menuitem">
    <xsl:param name="xpathMenuItem"/>
    <xsl:param name="unitkey" select="$selectedunitkey"/>
    <xsl:param name="menukey" select="$menukey"/>

    <xsl:text>adminpage?page=850&amp;op=browse</xsl:text>
    <xsl:text>&amp;selectedunitkey=</xsl:text>
    <xsl:value-of select="$unitkey"/>
    <xsl:text>&amp;menukey=</xsl:text>
    <xsl:value-of select="$menukey"/>
    <xsl:text>&amp;parentmi=</xsl:text>
    <xsl:value-of select="$xpathMenuItem/@key"/>
  </xsl:template>

  <xsl:template name="menu_href_category">
    <xsl:param name="xpathCategory"/>

    <xsl:variable name="page">
      <xsl:choose>
        <xsl:when test="$xpathCategory/@contenttypekey">
          <xsl:value-of select="number($xpathCategory/@contenttypekey) + 999"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>991</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:text>adminpage?page=</xsl:text>
    <xsl:value-of select="$page"/>
    <xsl:text>&amp;op=browse&amp;cat=</xsl:text>
    <xsl:value-of select="$xpathCategory/@key"/>
    <xsl:text>&amp;selectedunitkey=</xsl:text>
    <xsl:value-of select="$xpathCategory/@unitkey"/>
  </xsl:template>

  <xsl:template name="menu_href_section">
    <xsl:param name="xpathSection"/>
    <xsl:param name="menukey"/>

    <xsl:if
        test="not($xpathSection/accessrights/userright/@approve = 'false' and $xpathSection/accessrights/userright/@publish = 'false')">
      <xsl:text>adminpage?page=950</xsl:text>
      <xsl:text>&amp;op=browse&amp;sec=</xsl:text>
      <xsl:value-of select="$xpathSection/@key"/>
      <xsl:text>&amp;menukey=</xsl:text>
      <xsl:value-of select="$selectedmenukey"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>