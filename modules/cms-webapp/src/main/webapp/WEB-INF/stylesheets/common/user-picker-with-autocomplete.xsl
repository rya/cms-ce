<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
    <!--
      Requires:
        labelcolumn.xsl
        displayhelp.xsl
        button.xsl

        jquery-ui-1.8.1.custom.css
        jquery-ui-1.8.1.overrides.css
        user-picker-with-autocomplete.css

        engine.js
        AjaxService.js
        accessrights.js
        jquery-1.4.2.min.js
        jquery-ui-1.8.1.custom.min.js


      NOTE:
        In order for styling to be correct, the body element needs class="jquery-ui"
    -->

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="user-picker-with-autocomplete">
    <xsl:param name="name"/>
    <xsl:param name="label" select="''" />
    <xsl:param name="required" select="false()"/>
    <xsl:param name="id" select="$name"/>
    <xsl:param name="selected-user-key" select="''"/>
    <xsl:param name="selected-user-display-name" select="''"/>
    <xsl:param name="selected-user-qualified-name" select="''"/>
    <xsl:param name="size" select="40"/>
    <xsl:param name="help-element" select="''"/>
    <xsl:param name="ajax-service-function-to-execute" select="'findUsers'" />
    <xsl:param name="use-user-group-key" select="false()" />
    <xsl:param name="colspan" select="''"/>
    <xsl:param name="on-add-callback" select="'function() {}'"/>
    <xsl:param name="on-remove-callback" select="'function() {}'"/>

    <xsl:variable name="javascript-instance-name" select="concat('userPickerAutoComplete_', $id)"/>

    <xsl:if test="$label != ''">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
        <xsl:with-param name="helpelement" select="$help-element"/>
        <xsl:with-param name="valign" select="'middle'"/>
      </xsl:call-template>
    </xsl:if>

    <td nowrap="nowrap" valign="middle">
      <xsl:if test="$colspan != ''">
        <xsl:attribute name="colspan">
          <xsl:value-of select="$colspan"/>
        </xsl:attribute>
      </xsl:if>

      <input type="hidden" name="{$name}" value="{$selected-user-key}" id="{$id}"/>
      <input type="hidden" name="-ui-{$id}-compare-name"  value="" id="-ui-{$id}-compare-name"/>

      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <div>

              <xsl:if test="$help-element">
                <xsl:call-template name="displayhelp">
                  <xsl:with-param name="fieldname" select="$name"/>
                  <xsl:with-param name="helpelement" select="$help-element"/>
                </xsl:call-template>
              </xsl:if>

              <div style="position: relative">
                <div class="user-picker-autocomplete-feedback" id="-ui-autocomplete-feedback-{$id}">
                  %errAssigneeAutocompleteNoUsersFound%
                </div>

                <div style="float: left">
                  <input type="text" name="-ui-{$id}-autocomplete" id="-ui-{$id}-autocomplete" size="{$size}">
                    <xsl:attribute name="value">
                      <xsl:choose>
                        <xsl:when test="string-length( $selected-user-display-name ) &gt; 0">
                          <xsl:value-of select="$selected-user-display-name"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:text>%txtSearchUsers%</xsl:text>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:attribute>
                    <xsl:attribute name="class">
                      <xsl:text>textfield</xsl:text>
                      <xsl:if test="string-length( $selected-user-display-name ) = 0">
                        <xsl:text> placeholder-text</xsl:text>
                      </xsl:if>
                    </xsl:attribute>
                  </input>

                  <xsl:call-template name="button">
                    <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
                    <xsl:with-param name="name" select="concat('-', $id, '-userpickerbutton')"/>
                    <xsl:with-param name="onclick">
                      <xsl:value-of select="concat($javascript-instance-name, '.showUserPopup();')"/>
                    </xsl:with-param>
                    <xsl:with-param name="tooltip" select="'%cmdChoose%'"/>
                  </xsl:call-template>
                  <xsl:call-template name="button">
                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                    <xsl:with-param name="name" select="concat('_', $id, '_removebutton')"/>
                    <xsl:with-param name="onclick">
                      <xsl:value-of select="concat($javascript-instance-name, '.removeUser();')"/>
                    </xsl:with-param>
                    <xsl:with-param name="tooltip" select="'%cmdRemove%'"/>
                  </xsl:call-template>
                </div>
                <div style="float: left; padding-top: 4px">
                  <span id="-ui-{$id}-selected-user-qname" class="user-picker-selected-user-qname">
                    <xsl:if test="string-length( $selected-user-qualified-name ) &gt; 0">
                      <span>
                        <xsl:comment>Populated by JS on add user</xsl:comment>
                        (<xsl:value-of select="$selected-user-qualified-name"/>)
                      </span>
                    </xsl:if>
                  </span>
                </div>
              </div>
            </div>
          </td>
        </tr>
      </table>

      <script type="text/javascript">
        var <xsl:value-of select="$javascript-instance-name"/> = new UserPickerAutoComplete( '<xsl:value-of select="$id"/>', {
          'onAddUser' : <xsl:value-of select="$on-add-callback"/>
          ,'onRemoveUser' : <xsl:value-of select="$on-remove-callback"/>
          ,'ajaxServiceFunctionToExecute' : '<xsl:value-of select="$ajax-service-function-to-execute"/>'
          ,'useUserGroupKey' : <xsl:value-of select="$use-user-group-key"/>
        });
      </script>
    </td>
  </xsl:template>

</xsl:stylesheet>
