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

  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/serialize.xsl"/>
  <xsl:include href="common/codearea.xsl"/>

  <xsl:param name="key"/>
  <xsl:param name="fromsystem"/>
  <xsl:param name="userstorename" select="/wizarddata/userstores/userstore/@name"/>

  <xsl:variable name="new" select="not(/wizarddata/userstores/userstore)"/>
  <xsl:variable name="wizard-state-stepstate" select="/wizarddata/wizardstate/stepstate"/>

  <xsl:variable name="userstore">
    <xsl:choose>
      <xsl:when test="/wizarddata/wizardstate/stepstate[@id = '0']/userstore">
        <xsl:copy-of select="/wizarddata/wizardstate/stepstate[@id = '0']/userstore"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="/wizarddata/userstores/userstore"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="userStoreConnector">
    <xsl:choose>
      <xsl:when test="/wizarddata/wizardstate/stepstate[@id = '0']/userstore">
        <xsl:value-of select="/wizarddata/wizardstate/stepstate[@id = '0']/userstore/@connector"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="/wizarddata/userstores/userstore/connector/@name"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="availableConnectors" select="/wizarddata/userstore-connector-configs/config"/>
  <xsl:variable name="userStoreConnectorExist" select="boolean($availableConnectors[@name = $userStoreConnector])"/>

  <xsl:template name="userstore_form_title">
    <xsl:choose>
      <xsl:when test="/wizarddata/userstores/userstore">
        <xsl:text>%headEdit%:</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>%headCreate%:</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text> </xsl:text>
    <span id="titlename">
      <xsl:value-of select="exslt-common:node-set($userstore)/userstore/@name"/>
    </span>
  </xsl:template>

  <xsl:template name="userstoreheader">
    <xsl:call-template name="displayuserstorepath">
      <xsl:with-param name="userstorekey" select="$key"/>
      <xsl:with-param name="userstorename" select="$userstorename"/>
      <xsl:with-param name="edit" select="true()"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="step0">

    <xsl:variable name="remote" select="exslt-common:node-set($userstore)/userstore/@remote"/>

    <xsl:variable name="error">
      <part>
        <xsl:value-of select="/wizarddata/wizardstate/errors/error"/>
      </part>
    </xsl:variable>

    <xsl:if test="/wizarddata/wizardstate/errors/error != ''">
      <xsl:call-template name="displayerror">
        <xsl:with-param name="error" select="exslt-common:node-set($error)"/>
      </xsl:call-template>
    </xsl:if>

    <link rel="stylesheet" type="text/css" href="css/codearea.css"/>

    <script type="text/javascript">
      var g_number_of_available_connectors = <xsl:value-of select="count($availableConnectors)"/>;
      var g_is_remote = <xsl:value-of select="$remote = 'true'"/>;
    </script>
    <script type="text/javascript" src="javascript/userstore_form.js">//</script>
    <script type="text/javascript" src="codemirror/js/codemirror.js">//</script>
    <script type="text/javascript" src="javascript/codearea.js">//</script>

    <div class="tab-pane" id="tab-pane-1">
      <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
      </script>

      <div class="tab-page" id="tab-page-general">
        <span class="tab">%blockGeneral%</span>

        <script type="text/javascript" language="JavaScript">
          tabPane1.addTabPage( document.getElementById( "tab-page-general" ) );
        </script>

        <fieldset>
          <legend>%blockGeneral%</legend>

          <table width="100%" border="0" cellspacing="2" cellpadding="2">
            <tr>
              <xsl:call-template name="textfield">
                <xsl:with-param name="label" select="'%fldName%:'"/>
                <xsl:with-param name="name" select="'name'"/>
                <xsl:with-param name="selectnode" select="exslt-common:node-set($userstore)/userstore/@name"/>
                <xsl:with-param name="required" select="'true'"/>
                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
              </xsl:call-template>
            </tr>
            <tr>
              <td>%fldDefaultUserStore%:</td>
              <td>
                <xsl:choose>
                  <xsl:when test="not(/wizarddata/defaultuserstore/@key) or /wizarddata/userstores/userstore/@default = 'true'">
                    <input type="checkbox" name="defaultuserstore_disabled" checked="checked" disabled="true"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <input type="checkbox" name="defaultuserstore" id="defaultuserstore" value="true">
                      <xsl:if test="exslt-common:node-set($userstore)/userstore/@default = 'true'">
                        <xsl:attribute name="checked">checked</xsl:attribute>
                      </xsl:if>
                    </input>
                  </xsl:otherwise>
                </xsl:choose>
              </td>
            </tr>
            <tr>
              <td>%fldType%:</td>
              <td>
                <select name="userStoreType" onchange="UserStoreForm.onChangeType(this);">
                  <option value="remote">
                    <xsl:if test="$remote = 'true'">
                      <xsl:attribute name="selected">
                        <xsl:text>selected</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:text>
                      %optRemoteUserStore%
                    </xsl:text>
                  </option>
                  <option value="local">
                    <xsl:if test="$remote = 'false' or not($remote)">
                      <xsl:attribute name="selected">
                        <xsl:text>selected</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:text>
                      %optLocalUserStore%
                    </xsl:text>
                  </option>
                </select>
              </td>
            </tr>
            <tr id="form-connector-row">
              <xsl:if test="$new or $remote = 'false'">
                <xsl:attribute name="style">
                  <xsl:text>display: none;</xsl:text>
                </xsl:attribute>
              </xsl:if>

              <td>
                %lblRemoteUserStoreConnector%:
              </td>
              <td>

                <xsl:variable name="storedConnectorError">
                  <part>
                    <xsl:value-of select="/wizarddata/userstores/userstore/connector/config/errors/error"/>
                  </part>
                </xsl:variable>

                <div id="userstore-error-message">
                  <xsl:if test="/wizarddata/userstores/userstore/connector/config/errors/error != ''">
                    <xsl:call-template name="displayerror">
                      <xsl:with-param name="error" select="exslt-common:node-set($storedConnectorError)"/>
                    </xsl:call-template>
                  </xsl:if>
                </div>

                <xsl:choose>
                  <xsl:when test="$new and count($availableConnectors) = 0">
                    <div>
                      %msgNoConfiguredUserstoreConnectors%
                    </div>
                    <input type="hidden" name="remoteUserStoreConnector" value=""/>
                  </xsl:when>
                  <xsl:when test="count($availableConnectors) = 0">
                    <div>
                      %msgConnectorIsNotConfigured%
                      "<xsl:value-of select="$userStoreConnector"/>": %msgNoConfiguredUserstoreConnectors%
                    </div>
                    <input type="hidden" name="remoteUserStoreConnector" value="{$userStoreConnector}"/>
                  </xsl:when>
                  <xsl:otherwise>

                    <select name="remoteUserStoreConnector" onchange="UserStoreForm.onChangeConnector(this)">
                      <xsl:if test="not($new) and $userStoreConnectorExist = false()">
                        <!-- Printing option for missing userstore connector. @class=-connector-not-found used by onChangeConnector -->
                        <option value="{$userStoreConnector}" class="-connector-not-found">
                          <xsl:value-of select="$userStoreConnector"/><xsl:text> ( %errorConnectorNotFound% )</xsl:text>
                        </option>
                      </xsl:if>
                      <!-- Printing available connectors -->
                      <xsl:for-each select="$availableConnectors">
                        <xsl:sort select="@name"/>
                        <option value="{@name}">

                          <xsl:choose>
                            <xsl:when test="$wizard-state-stepstate[@errorstate = 'true']">
                              <xsl:if test="@name = $wizard-state-stepstate/userstore/@connector">
                                <xsl:attribute name="selected">
                                  <xsl:text>selected</xsl:text>
                                </xsl:attribute>
                              </xsl:if>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:if test="@name = $userStoreConnector">
                                <xsl:attribute name="selected">
                                  <xsl:text>selected</xsl:text>
                                </xsl:attribute>
                              </xsl:if>
                            </xsl:otherwise>
                          </xsl:choose>
                          <xsl:value-of select="@name"/>
                        </option>
                      </xsl:for-each>
                    </select>

                  </xsl:otherwise>
                </xsl:choose>
              </td>
            </tr>
            <tr>

              <xsl:variable name="selectnode">
                <xsl:if test="/wizarddata/wizardstate/stepstate/@errorstate">
                  <xsl:value-of select="exslt-common:node-set($userstore)/userstore/configRaw"/>
                </xsl:if>
                <xsl:if test="not(/wizarddata/wizardstate/stepstate/@errorstate)">
                  <xsl:call-template name="serialize">
                    <xsl:with-param name="xpath" select="exslt-common:node-set($userstore)/userstore/config"/>
                    <xsl:with-param name="include-self" select="true()"/>
                  </xsl:call-template>
                </xsl:if>
              </xsl:variable>

              <xsl:call-template name="codearea">
                <xsl:with-param name="name" select="'config'"/>
                <xsl:with-param name="label" select="'%fldConfig%:'"/>
                <xsl:with-param name="width" select="'100%'"/>
                <xsl:with-param name="height" select="'380px'"/>
                <xsl:with-param name="line-numbers" select="true()"/>
                <xsl:with-param name="selectnode" select="$selectnode"/>
                <xsl:with-param name="buttons" select="'find, replace, indentall, indentselection, gotoline'"/>
              </xsl:call-template>
            </tr>
          </table>
        </fieldset>
      </div>
    </div>

    <script type="text/javascript" language="JavaScript">
      setupAllTabs();
    </script>
  </xsl:template>

</xsl:stylesheet>
