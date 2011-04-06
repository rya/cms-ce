<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/accesslevel_parameters.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/displayuserstorepath.xsl"/>
  <xsl:include href="common/waitsplash.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>

  <xsl:param name="key"/>
  <xsl:param name="reload"/>
  <xsl:param name="userstorekey"/>
  <xsl:param name="userstorename"/>
  <xsl:param name="synchronizeUsers"/>
  <xsl:param name="synchronizeGroups"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?op=page</xsl:text>
    <xsl:text>&amp;page=</xsl:text><xsl:value-of select="$page"/>
    <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$key"/>
  </xsl:variable>

  <xsl:template match="/">
    <html>
      <head>
        <script type="text/javascript" src="javascript/admin.js">//</script>

        <!-- Split Button -->
        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/MenuButton.js">//</script>

        <script type="text/javascript" src="javascript/lib/jquery/jquery-1.3.2.min.js">//</script>
        <script type="text/javascript" src="javascript/userstore.js">//</script>

        <script type="text/javascript" src="javascript/tabpane.js">//</script>

        <xsl:call-template name="waitsplash"/>

        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>
        <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
      </head>

      <script type="text/javascript" language="JavaScript">
        <xsl:if test="$reload = 'true' or $reload = 'menu'">
          window.top.frames['leftFrame'].refreshMenu();
        </xsl:if>
      </script>
      <body>

        <xsl:if test="$synchronizeUsers = 'true' or $synchronizeGroups = 'true'">
          <script type="text/javascript">
            $(document).ready( function()
            {
              checkIfUserstoreIsSynchronizing('<xsl:value-of select="$userstorekey"/>');
            });
          </script>
        </xsl:if>

        <h1>
          <xsl:call-template name="displayuserstorepath">
            <xsl:with-param name="userstorekey" select="$userstorekey"/>
            <xsl:with-param name="userstorename" select="$userstorename"/>
          </xsl:call-template>
        </h1>

        <xsl:variable name="error">
          <part>
            <xsl:value-of select="/userstores/userstore/connector/config/errors/error"/>            
          </part>
        </xsl:variable>

        <xsl:if test="/userstores/userstore/connector/config/errors/error != ''">
          <xsl:call-template name="displayerror">
            <xsl:with-param name="error" select="exslt-common:node-set($error)"/>
          </xsl:call-template>
        </xsl:if>

        <table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="browse_title_buttonrow_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
        </table>

        <xsl:if test="$userstoreadmin = 'true'">
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%cmdEdit%'"/>
            <xsl:with-param name="onclick">
              <xsl:text>javascript:document.cmdEditForm.submit();</xsl:text>
            </xsl:with-param>
          </xsl:call-template>

          <!-- BEGIN: Menu Button -->

          <xsl:if test="$synchronizeUsers = 'true' or $synchronizeGroups = 'true'">
            <ul id="synchronize-menu-button" title="%synchronize%..." class="cms-menu-button">
              <xsl:if test="$synchronizeUsers = 'true' and $synchronizeGroups = 'true'">
                <li>
                  <a href="javascript: synchronize('{$userstorekey}', 'usersAndGroups');">%cmdSynchronizeUsersAndGroups%</a>
                </li>
              </xsl:if>
              <xsl:if test="$synchronizeUsers = 'true'">
                <li style="background-image:url(images/icon_users.gif)">
                  <a href="javascript: synchronize('{$userstorekey}', 'users');">%cmdSynchronizeUsers%</a>
                </li>
              </xsl:if>
              <xsl:if test="$synchronizeGroups = 'true'">
                <li style="background-image:url(images/icon_groups.gif)">
                  <a href="javascript: synchronize('{$userstorekey}', 'groups');">%cmdSynchronizeGroups%</a>
                </li>
              </xsl:if>
            </ul>
            <script type="text/javascript" charset="utf-8">
              var synchronizeMenuButton = new cms.ui.MenuButton('synchronize-menu-button');
              synchronizeMenuButton.insert();
            </script>
          </xsl:if>
          <!-- END: Menu Button -->

          <table cellspacing="0" cellpadding="0" border="0">
            <tr>
              <td class="browse_buttonrow_datarows_seperator">
                <img src="images/1x1.gif"/>
              </td>
            </tr>
          </table>

          <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
              var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
            </script>

            <div class="tab-page" id="tab-page-1">
              <span class="tab">%blockUserstoreInfo%</span>

              <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
              </script>

              <fieldset>
                <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                <table class="formtable">
                  <tr>
                    <td class="form_labelcolumn" valign="top">
                      %fldName%:
                    </td>
                    <td>
                      <xsl:value-of select="/userstores/userstore/@name"/>
                    </td>
                  </tr>
                  <tr>
                    <td class="form_labelcolumn" valign="top">
                      %fldDefaultUserStore%:
                    </td>
                    <td>
                      <xsl:choose>
                        <xsl:when test="/userstores/userstore/@default = 'true'">
                          <img src="images/icon_check_noborder.gif" alt=""/>
                        </xsl:when>
                        <xsl:otherwise>
                          <br/>
                        </xsl:otherwise>
                      </xsl:choose>

                    </td>
                  </tr>
                  <xsl:variable name="remote" select="/userstores/userstore/@remote = 'true'"/>
                  <tr>
                    <td class="form_labelcolumn" valign="top">
                      %fldType%:
                    </td>
                    <td>
                      <xsl:choose>
                        <xsl:when test="$remote">
                          %optRemoteUserStore%
                        </xsl:when>
                        <xsl:otherwise>
                          %optLocalUserStore%
                        </xsl:otherwise>
                      </xsl:choose>
                    </td>
                  </tr>
                  <xsl:if test="$remote">
                    <tr>
                      <td class="form_labelcolumn" valign="top">
                        %lblRemoteUserStoreConnector%:
                      </td>
                      <td>
                        <xsl:value-of select="/userstores/userstore/connector/@name"/>
                      </td>
                    </tr>
                  </xsl:if>
                </table>
              </fieldset>

              <xsl:if test="$synchronizeUsers = 'true' or $synchronizeGroups = 'true'">
                <fieldset id="synchronizationFieldset" style="display: none">
                  <legend>&nbsp;%blockSynchronization%&nbsp;</legend>
                  <table class="formtable">
                    <tr>
                      <td class="form_labelcolumn" valign="top">
                        %synchStarted%:
                      </td>
                      <td>
                        <span id="synchronize-started-date"><xsl:comment> // </xsl:comment></span>
                      </td>
                    </tr>
                    <tr>
                      <td class="form_labelcolumn" valign="top">
                        %synchFinished%:
                      </td>
                      <td>
                        <span id="synchronize-finished-date"><xsl:comment> // </xsl:comment></span>
                      </td>
                    </tr>
                  </table>

                  <script type="text/javascript">
                    updateStartAndFinishDate('<xsl:value-of select="$userstorekey"/>');
                  </script>

                </fieldset>

              </xsl:if>
            </div>
          </div>

          <script type="text/javascript" language="JavaScript">
            setupAllTabs();
          </script>

          <!-- Helper form: cmdEditForm -->
          <form name="cmdEditForm" method="get" action="adminpage">
            <input type="hidden" name="page" value="290"/>
            <input type="hidden" name="op" value="form"/>
            <input type="hidden" name="key" value="{$key}"/>
            <input type="hidden" name="redirect_to" value="{concat($pageURL, '&amp;reload=menu')}"/>
          </form>

          <!-- Helper form: cmdNewSiteForm -->
          <form name="cmdNewSiteForm" method="get" action="adminpage">
            <input type="hidden" name="page" value="300"/>
            <input type="hidden" name="op" value="form"/>
            <input type="hidden" name="selecteddomainkey" value="{$key}"/>
            <input type="hidden" name="redirect_to" value="{concat($pageURL, '&amp;reload=menu')}"/>
          </form>

          <!-- Helper form: cmdSynchronizeForm -->
          <form name="cmdSynchronizeAllForm" method="get" action="adminpage">
            <input type="hidden" name="page" value="290"/>
            <input type="hidden" name="op" value="synchronize_all"/>
            <input type="hidden" name="domainkey" value="{$key}"/>
            <input type="hidden" name="redirect_to" value="{$pageURL}"/>
          </form>
          <form name="cmdSynchronizeGroupsForm" method="get" action="adminpage">
            <input type="hidden" name="page" value="290"/>
            <input type="hidden" name="op" value="synchronize_groups"/>
            <input type="hidden" name="domainkey" value="{$key}"/>
            <input type="hidden" name="redirect_to" value="{$pageURL}"/>
          </form>
          <form name="cmdSynchronizeUsersForm" method="get" action="adminpage">
            <input type="hidden" name="page" value="290"/>
            <input type="hidden" name="op" value="synchronize_users"/>
            <input type="hidden" name="domainkey" value="{$key}"/>
            <input type="hidden" name="redirect_to" value="{$pageURL}"/>
          </form>
        </xsl:if>
        
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
