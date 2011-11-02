<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>

  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/readonlyvalue.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/checkbox_boolean.xsl"/>
  <xsl:include href="common/displayuserstorepath.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/serialize.xsl"/>

  <xsl:include href="common/tablecolumnheader.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="key"/>
  <xsl:param name="grouptype"/>
  <xsl:param name="userstorekey"/>
  <xsl:param name="userstorename"/>
  <xsl:param name="callback"/>
  <xsl:param name="mode"/>

  <xsl:param name="canUpdateGroup" select="'true'"/>

  <xsl:variable name="selectedname">
    <xsl:value-of select="/groups/group[@key = $key]/displayName"/>
  </xsl:variable>

  <xsl:variable name="membership-allow-authenticated" select="$mode = 'globalgroups'"/>
  <xsl:variable name="is-remote" select="/group/userstores/userstore[@key = /group/@userStoreKey]/@remote = 'true'"/>

  <xsl:template match="/">
    <html>
      <head>
        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>

        <xsl:if test="$callback">
          <script type="text/javascript" src="javascript/window.js">//</script>
          <script type="text/javascript">
            cms.window.attatchKeyEvent('close');
          </script>
        </xsl:if>

        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/groups.js">//</script>
        <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
        <script type="text/javascript" language="JavaScript">
          var validatedFields = new Array(1);
          validatedFields[0] = new Array("%fldName%", "name", validateRequired);

          function validateAll(formName)
          {
            var f = document.forms[formName];

            if ( !checkAll(formName, validatedFields) )
              return;

            f.submit();
          }
        </script>
      </head>

      <body onload="setFocus()">

        <xsl:variable name="browsepageURL">
          <xsl:text>adminpage?</xsl:text>
          <xsl:text>page=</xsl:text>
          <xsl:value-of select="$page"/>
          <xsl:text>&amp;op=browse</xsl:text>
        </xsl:variable>
        
        <h1>
          <xsl:call-template name="displayuserstorepath">
            <xsl:with-param name="userstorekey" select="$userstorekey"/>
            <xsl:with-param name="userstorename" select="$userstorename"/>
            <xsl:with-param name="mode" select="$mode"/>
            <xsl:with-param name="disabled" select="not($callback = '')"/>
            <xsl:with-param name="isGroups" select="true()"/>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>
          <span id="titlename">
            <xsl:if test="$create != '1'">
              <xsl:value-of select="concat('/ ', group/name)"/>
            </xsl:if>
          </span>
        </h1>

        <form name="formAdmin" method="post">
          <xsl:attribute name="action">
            <xsl:text>adminpage?page=</xsl:text>
            <xsl:value-of select="$page"/>
            <xsl:choose>
              <xsl:when test="$create = '1'">&amp;op=create</xsl:when>
              <xsl:otherwise>&amp;op=update</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>

          <xsl:if test="$userstorekey">
            <input type="hidden" name="userstorekey" value="{$userstorekey}"/>
          </xsl:if>

          <input type="hidden" name="referer" value="{$referer}"/>

          <xsl:if test="$key">
            <input type="hidden" name="key" value="{group/@key}"/>
          </xsl:if>

          <table width="100%" border="0" cellspacing="0" cellpadding="2">
            <!-- separator -->
            <tr>
              <td class="form_title_form_seperator">
                <img src="images/1x1.gif"/>
              </td>
            </tr>

            <!-- form -->
            <tr>
              <td>
                <div class="tab-pane" id="tab-pane-1">
                  <script type="text/javascript" language="JavaScript">
                    var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                  </script>

                  <div class="tab-page" id="tab-page-1">
                    <span class="tab">%blockGeneral%</span>

                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                    </script>

                    <xsl:variable name="namefield-is-editable" select="not( $is-remote ) and ( $create = '1' or group/@type = 1 or group/@type = 4 or group/@type = 6 )"/>

                    <fieldset>
                      <legend>&nbsp;%blockGeneral%&nbsp;</legend>
                      <table border="0" cellspacing="2" cellpadding="0" width="100%">
                        <xsl:choose>
                          <xsl:when test="$namefield-is-editable">
                            <tr>
                              <xsl:call-template name="textfield">
                                <xsl:with-param name="label" select="'%fldName%:'"/>
                                <xsl:with-param name="name" select="'name'"/>
                                <xsl:with-param name="selectnode" select="group/displayName"/>
                                <xsl:with-param name="size" select="'50'"/>
                                <xsl:with-param name="disabled" select="group/@type = 2 or group/@type = 0 or $canUpdateGroup = 'false'"/>
                                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);
                                </xsl:with-param>
                                <xsl:with-param name="required" select="'true'"/>
                              </xsl:call-template>
                            </tr>
                          </xsl:when>
                          <xsl:otherwise>
                            <tr>
                              <xsl:call-template name="readonlyvalue">
                                <xsl:with-param name="label" select="'%fldName%:'"/>
                                <xsl:with-param name="name" select="'name'"/>
                                <xsl:with-param name="selectnode" select="group/displayName"/>
                              </xsl:call-template>
                            </tr>
                            <!--input type="hidden" name="name" value="{$selectedname}"/-->
                          </xsl:otherwise>
                        </xsl:choose>

                        <tr>

                          <xsl:variable name="restricted">
                            <xsl:choose>
                              <xsl:when test="not(group/@restricted)">
                                <xsl:text>true</xsl:text>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:value-of select="group/@restricted"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:variable>

                          <xsl:variable name="restricted-disabled">
                            <xsl:choose>
                              <xsl:when test="group/@builtIn = 'true' and $restricted = 'false'">
                                <xsl:text>false</xsl:text>
                              </xsl:when>
                              <xsl:when test="group/@builtIn = 'true'">
                                <xsl:text>true</xsl:text>
                              </xsl:when>                              
                              <xsl:when test="$canUpdateGroup = 'false'">
                                <xsl:text>true</xsl:text>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:text>false</xsl:text>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:variable>

                          <xsl:call-template name="checkbox_boolean">
                            <xsl:with-param name="label" select="'%fldRestricted%:'"/>
                            <xsl:with-param name="name" select="'restricted'"/>
                            <xsl:with-param name="selectnode" select="$restricted"/>
                            <xsl:with-param name="disabled" select="$restricted-disabled = 'true'"/>
                          </xsl:call-template>
                        </tr>
                        <tr>
                          <xsl:variable name="description-disabled">
                            <xsl:choose>
                              <xsl:when test="group/@builtIn = 'true'">
                                <xsl:text>true</xsl:text>
                              </xsl:when>
                              <xsl:when test="$canUpdateGroup = 'false'">
                                <xsl:text>true</xsl:text>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:text>false</xsl:text>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:variable>

                          <xsl:call-template name="textarea">
                            <xsl:with-param name="label" select="'%fldDescription%:'"/>
                            <xsl:with-param name="name" select="'description'"/>
                            <xsl:with-param name="selectnode" select="group/description"/>
                            <xsl:with-param name="disabled" select="$description-disabled = 'true'"/>
                            <xsl:with-param name="cols" select="'64'"/>
                            <xsl:with-param name="rows" select="'4'"/>
                          </xsl:call-template>
                        </tr>
                      </table>
                    </fieldset>
                  </div>

                  <div class="tab-page" id="tab-page-2">
                    <span class="tab">%blockMembers%</span>

                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
                    </script>

                    <fieldset>
                      <legend>&nbsp;%blockGroupsAndUsers%&nbsp;</legend>

                      <table border="0" cellspacing="2" cellpadding="0" width="50%">
                        <tr>
                          <td>
                            <xsl:if test="count(group/members/*) &gt; 0">
                              <xsl:if test="$canUpdateGroup = 'true' and ( group/@type = 2 or group/@type = 0 )">
                                <xsl:call-template name="button">
                                  <xsl:with-param name="name" select="'butAddAccesRightRow1'"/>
                                  <xsl:with-param name="type" select="'button'"/>
                                  <xsl:with-param name="caption" select="'%cmdAdd%'"/>
                                  <xsl:with-param name="onclick">
                                    <xsl:text>javascript:showUserAndGroupsPopup(</xsl:text>
                                    <xsl:choose>
                                      <xsl:when test="$userstorekey">
                                        <xsl:value-of select="$userstorekey"/>
                                      </xsl:when>
                                      <xsl:otherwise>
                                        <xsl:text>null</xsl:text>
                                      </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:text>, 'users', true, '</xsl:text>
                                    <xsl:value-of select="/group/@key"/>
                                    <xsl:text>', false, true,</xsl:text>
                                    <xsl:value-of select="$membership-allow-authenticated"/>
                                    <xsl:text>);</xsl:text>
                                  </xsl:with-param>
                                </xsl:call-template>
                              </xsl:if>
                            </xsl:if>
                          </td>
                        </tr>
                        <tr>
                          <td>
                            <table border="0" cellspacing="0" cellpadding="2" style="width: 60%">
                              <tbody id="memberstable">
                                <xsl:for-each select="group/members/*">
                                  <xsl:variable name="typetext">
                                    <xsl:choose>
                                      <xsl:when test="name() = 'user'">
                                        <xsl:text>%msgClickToRemoveUser%</xsl:text>
                                      </xsl:when>
                                      <xsl:otherwise>
                                        <xsl:text>%msgClickToRemoveGroup%</xsl:text>
                                      </xsl:otherwise>
                                    </xsl:choose>
                                  </xsl:variable>
                                  <tr>
                                    <td nowrap="nowrap">
                                      <xsl:choose>
                                        <xsl:when test="name() = 'user'">
                                          <img src="images/icon_usergroups.gif"
                                               style="vertical-align: middle"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <img src="images/icon_groups.gif"
                                               style="vertical-align: middle"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                      <xsl:text> </xsl:text>
                                      <xsl:value-of select="displayName"/>

                                      <xsl:variable name="is-global-group" select="not(@userStoreKey)"/>

                                      <xsl:text> </xsl:text>
                                      <xsl:text>(</xsl:text>
                                      <xsl:choose>
                                        <xsl:when test="$is-global-group">
                                          <xsl:text>%txtGlobalGroup%</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="qualifiedName"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                      <xsl:text>)</xsl:text>

                                      <input type="hidden" name="member" value="{@key}"/>
                                    </td>

                                    <td width="20">
                                      <xsl:call-template name="button">
                                        <xsl:with-param name="name">
                                          <xsl:text>foo[key=</xsl:text>
                                          <xsl:value-of select="@groupKey"/>
                                          <xsl:text>]</xsl:text>
                                        </xsl:with-param>
                                        <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                                        <xsl:with-param name="tooltip" select="$typetext"/>
                                        <xsl:with-param name="onclick">
                                          <xsl:text>javascript:handle_groupRemove_onclick( this );</xsl:text>
                                        </xsl:with-param>
                                      </xsl:call-template>
                                    </td>
                                    <script type="text/javascript" language="JavaScript">
                                      addChoosen('<xsl:value-of select="@groupKey"/>');
                                    </script>
                                  </tr>
                                </xsl:for-each>
                              </tbody>
                            </table>
                          </td>
                        </tr>
                        <tr>
                          <td>
                            <xsl:if test="group/@type = 2 or group/@type = 0 or $canUpdateGroup = 'true'">
                              <xsl:call-template name="button">
                                <xsl:with-param name="name" select="'butAddAccesRightRow2'"/>
                                <xsl:with-param name="type" select="'button'"/>
                                <xsl:with-param name="caption" select="'%cmdAdd%'"/>
                                <xsl:with-param name="onclick">
                                  <xsl:text>javascript:showUserAndGroupsPopup(</xsl:text>
                                  <xsl:choose>
                                    <xsl:when test="$userstorekey">
                                      <xsl:value-of select="$userstorekey"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:text>null</xsl:text>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                  <xsl:text>, 'users', true, '</xsl:text>
                                  <xsl:value-of select="/group/@key"/>
                                  <xsl:text>', false, true,</xsl:text>
                                  <xsl:value-of select="$membership-allow-authenticated"/>
                                  <xsl:text>);</xsl:text>
                                </xsl:with-param>
                              </xsl:call-template>
                            </xsl:if>
                          </td>
                        </tr>
                      </table>
                    </fieldset>
                  </div>
                  <div class="tab-page" id="tab-page-3">
                    <span class="tab">%blockMemberOf%</span>

                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-3" ) );
                    </script>

                    <fieldset>
                      <legend>&nbsp;%blockGroups%&nbsp;</legend>
                      <table border="0" cellspacing="2" cellpadding="0" width="60%">
                        <xsl:for-each select="/group/memberOf/group">
                          <!--
                          <xsl:sort data-type="text" select="name" order="ascending" lang="no" case-order="lower-first"/>
                          -->
                          <tr>
                            <td width="16">
                              <xsl:choose>
                                <xsl:when test="(@type = '6' or @type = '7')">
                                  <img src="images/icon_usergroups.gif"/>
                                </xsl:when>
                                <xsl:otherwise>
                                  <img src="images/icon_groups.gif"/>
                                </xsl:otherwise>
                              </xsl:choose>
                            </td>
                            <td>
                              <xsl:text>&nbsp;</xsl:text>
                              <xsl:value-of select="name"/>
                            </td>
                            <td width="150">
                              <xsl:if test="@userStoreKey">
                                <xsl:text>%fldUserstore%: </xsl:text>
                                <xsl:value-of select="/group/userstores/userstore[@key = current()/@userStoreKey]/@name"/>
                              </xsl:if>
                            </td>
                          </tr>
                        </xsl:for-each>
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
              <td class="form_form_buttonrow_seperator">
                <img src="images/1x1.gif"/>
              </td>
            </tr>
            <tr>
              <td>
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdSave%'"/>
                  <xsl:with-param name="name" select="'lagre'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:text>&nbsp;</xsl:text>

                <xsl:variable name="buttonCaption">
                  <xsl:choose>
                    <xsl:when test="$canUpdateGroup = 'true'">%cmdCancel%</xsl:when>
                    <xsl:otherwise>%cmdClose%</xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="$buttonCaption"/>
                  <xsl:with-param name="name" select="'avbryt'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>javascript:history.back();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </td>
            </tr>
          </table>
        </form>

      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
