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

  <xsl:template name="properties">
    <xsl:variable name="categorypublish" select="not(/contents/userright) or (/contents/userright/@publish = 'true')"/>

    <xsl:variable name="text-auto-generated">
      <xsl:text>(Auto generated)</xsl:text>
    </xsl:variable>
    
    <xsl:variable name="is-assigned" select="/contents/content/@is-assigned = 'true'"/>

    <xsl:variable name="assignee-key">
      <xsl:choose>
        <xsl:when test="$is-assigned">
          <xsl:value-of select="/contents/content/assignee/@key"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$new or $editlockedversionmode">
            <xsl:value-of select="$currentuser_key"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="assignee-display-name-and-qname">
      <xsl:choose>
        <xsl:when test="$is-assigned">
          <xsl:value-of select="concat(/contents/content/assignee/display-name, ' (', /contents/content/assignee/@qualified-name, ')')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$new or $editlockedversionmode">
            <xsl:value-of select="concat($currentuser_fullname, ' (', $currentuser_qualifiedname, ')')"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="assigner-key">
      <xsl:choose>
        <xsl:when test="$is-assigned">
          <xsl:value-of select="/contents/content/assigner/@key"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$new or $editlockedversionmode">
            <xsl:value-of select="$currentuser_key"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="assigner-display-name-and-qname">
      <xsl:choose>
        <xsl:when test="$is-assigned">
          <xsl:value-of select="concat(/contents/content/assigner/display-name, ' (', /contents/content/assigner/@qualified-name, ')')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$new or $editlockedversionmode">
            <xsl:value-of select="concat($currentuser_fullname, ' (', $currentuser_qualifiedname, ')')"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="due-date" select="/contents/content/assignment-due-date"/>
    <xsl:variable name="assignment-description" select="/contents/content/assignment-description"/>

    <xsl:variable name="current-user-is-assignee" select="$currentuser_key = $assignee-key"/>
    <xsl:variable name="current-user-is-not-assignee" select="not($current-user-is-assignee)"/>
    <xsl:variable name="draft-exists" select="/contents/content/@has-draft = 'true'"/>

    <xsl:variable name="disable-assign-to-me-button" select="not($draft-exists) or $current-user-is-assignee"/>

    <div class="tab-page" id="tab-page-properties">
      <span class="tab">%blockProperties%</span>

      <script type="text/javascript" language="JavaScript">
        tabPane1.addTabPage( document.getElementById( "tab-page-properties" ) );
      </script>

      <fieldset>
      <legend>&nbsp;%blockName%&nbsp;</legend>

        <xsl:variable name="name-lock-tooltip-text">
          <xsl:choose>
            <xsl:when test="$categorypublish">
              <xsl:text>%tooltipUnlockToEditManually%</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>%tooltipCanNotModifyContentName%</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="name-help-element">
          <help>%hlpContentName%</help>
        </xsl:variable>

        <xsl:variable name="selectnode2">
          <xsl:choose>
            <xsl:when test="string-length(contents/content/name) &gt; 0">
              <xsl:value-of select="contents/content/name"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$text-auto-generated"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <table width="100%" colspace="0" colpadding="2" border="0"
               name="menuitem_nametable" id="menuitem_nametable">
          <tbody id="content_key_parambody">
            <tr>
              <xsl:call-template name="textfield">
                <xsl:with-param name="name" select="'_name'"/>
                <xsl:with-param name="label" select="'%fldName%:'"/>
                <xsl:with-param name="helpelement" select="$name-help-element"/>
                <xsl:with-param name="selectnode" select="$selectnode2"/>
                <xsl:with-param name="disabled" select="$selectnode2 = $text-auto-generated"/>
                <xsl:with-param name="lock" select="true()"/>
                <xsl:with-param name="lock-enabled" select="$categorypublish"/>
                <xsl:with-param name="lock-tooltip" select="$name-lock-tooltip-text"/>
                <xsl:with-param name="lock-click-callback">
                  <xsl:text>content_name_lockClickCallback('</xsl:text>
                  <xsl:value-of select="$text-auto-generated"/>
                  <xsl:text>')</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="readonly" select="true()"/>
                <xsl:with-param name="size" select="'46'"/>
                <xsl:with-param name="maxlength" select="'256'"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="onkeyup">
                  javascript: updateBreadCrumbHeader('titlename', this);
                </xsl:with-param>
                <xsl:with-param name="extra-css-class">
                  <xsl:if test="$selectnode2 = $text-auto-generated">
                    <xsl:text>grey-text</xsl:text>
                  </xsl:if>
                </xsl:with-param>
              </xsl:call-template>
            </tr>
          </tbody>
        </table>

        <script type="text/javascript">
          validatedFields[ idx ] = new Array( "%fldName%", "_name", contentValidateName );
          ++idx;
        </script>
      </fieldset>

      <fieldset>
        <legend>&nbsp;%blockProperties%&nbsp;</legend>
        <xsl:variable name="readonly"
                      select="not($create = 1 or $current_uid = /contents/content/owner/name or ( not(/contents/userright) or /contents/userright/@publish = 'true' ))"/>
        <table width="100%" border="0" cellspacing="2" cellpadding="2">
          <tr>
            <td>
              %fldCreated%:
            </td>
            <td>
              <xsl:call-template name="formatdatetime">
                <xsl:with-param name="date" select="/contents/content/@created"/>
              </xsl:call-template>
            </td>
          </tr>
          <tr>
            <td class="form_labelcolumn" valign="baseline">
              %fldOwner%:
            </td>
            <td>
              <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td nowrap="true" valign="baseline">
                    <xsl:variable name="owner">
                      <xsl:choose>
                        <xsl:when test="$create = 1">
                          <xsl:value-of select="$currentuser_fullname"/>
                          <xsl:text>&nbsp;(</xsl:text>
                          <xsl:value-of select="$currentuser_qualifiedname"/>
                          <xsl:text>)</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="/contents/content/owner/display-name"/>
                          <xsl:text>&nbsp;(</xsl:text>
                          <xsl:value-of select="/contents/content/owner/@qualified-name"/>
                          <xsl:text>)</xsl:text>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>

                    <span id="label_pubdata_ownername">
                      <xsl:if test="/contents/content/owner/@deleted = 'true'">
                        <xsl:attribute name="style">text-decoration: line-through;</xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="$owner"/>
                    </span>
                  </td>
                </tr>
              </table>
              <input type="hidden" id="_pubdata_owner" name="_pubdata_owner" value="{/contents/content/owner/@key}"/>
              <input type="hidden" id="__pubdata_ownername" name="__pubdata_ownername" value="{/contents/content/owner/display-name}"/>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <xsl:variable name="disableTakeOwnership">
                <xsl:choose>
                  <xsl:when test="$create = '1'">true</xsl:when>
                  <xsl:when test="$currentuser_key = /contents/content/owner/@key">true</xsl:when>
                  <xsl:when
                      test="/contents/userright/@publish = 'true' or /contents/userright/@administrate = 'true' or not(/contents/userright)">
                    false
                  </xsl:when>
                  <xsl:otherwise>true</xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <input type="hidden" id="__tmp_user_name" name="__tmp_user_name" value="{$currentuser_fullname}" disabled="disabled"/>

              <!-- Add an extra backslash to qualifiedName to escape the backslash (used later in a JavaScript string) -->
              <xsl:variable name="currentuser_qualifiedname_userstore" select="substring-before($currentuser_qualifiedname, '\')"/>
              <xsl:variable name="currentuser_qualifiedname_username" select="substring-after($currentuser_qualifiedname, '\')"/>
              <xsl:variable name="currentuser_qualifiedname_for_javascript">
                <xsl:choose>
                  <xsl:when test="string($currentuser_qualifiedname) = 'admin'">
                    <xsl:value-of select="$currentuser_qualifiedname"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="concat($currentuser_qualifiedname_userstore, '\\', $currentuser_qualifiedname_username)"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="button">
                <xsl:with-param name="name" select="'takeownership'"/>
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="caption" select="'%cmdTakeOwnership%'"/>
                <xsl:with-param name="disabled" select="$disableTakeOwnership"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:</xsl:text>
                  <xsl:text>document.getElementById('_pubdata_owner').value = '</xsl:text>
                  <xsl:value-of select="$currentuser_key"/>
                  <xsl:text>';</xsl:text>
                  <xsl:text>document.getElementById('__pubdata_ownername').value = document.getElementById('__tmp_user_name').value;</xsl:text>
                  <xsl:text>document.getElementById('label_pubdata_ownername').innerHTML = document.getElementById('__tmp_user_name').value + '&nbsp;(</xsl:text>
                  <xsl:value-of select="$currentuser_qualifiedname_for_javascript"/>
                  <xsl:text>)';</xsl:text>
                  <xsl:text>document.getElementById('label_pubdata_ownername').style.color = '#FF9933';</xsl:text>
                  <xsl:text>setTextButtonEnabled( document.getElementById('takeownership'), false );</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
            </td>
          </tr>
          <tr>
            <td>%fldContentType%:</td>
            <td>
              <xsl:value-of select="$modulename"/>
            </td>
          </tr>
          <xsl:choose>
            <xsl:when test="not($readonly)">
              <tr>
                <xsl:call-template name="dropdown_language">
                  <xsl:with-param name="name" select="'_pubdata_languagekey'"/>
                  <xsl:with-param name="label" select="'%fldLanguage%:'"/>
                  <xsl:with-param name="selectedkey" select="/contents/content/@languagekey"/>
                  <xsl:with-param name="selectnode" select="/contents/languages/language"/>
                  <xsl:with-param name="emptyrow" select="'%defaultLanguage%'"/>
                </xsl:call-template>
              </tr>
              <tr>
                <xsl:call-template name="textfield">
                  <xsl:with-param name="name" select="'_pubdata_priority'"/>
                  <xsl:with-param name="label" select="'%fldPriority%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/content/@priority"/>
                  <xsl:with-param name="size" select="'5'"/>
                  <xsl:with-param name="maxlength" select="'7'"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                </xsl:call-template>
              </tr>
            </xsl:when>
            <xsl:otherwise>
              <tr>
                <xsl:call-template name="readonlyvalue">
                  <xsl:with-param name="name" select="'_pubdata_languagename'"/>
                  <xsl:with-param name="label" select="'%fldLanguage%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/languages/language[@key = /contents/content/@languagekey]"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                </xsl:call-template>
                <input type="hidden" name="_pubdata_languagekey" value="{/contents/content/@languagekey}"/>
              </tr>
              <tr>
                <xsl:call-template name="readonlyvalue">
                  <xsl:with-param name="name" select="'_pubdata_priority'"/>
                  <xsl:with-param name="label" select="'%fldPriority%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/content/@priority"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                </xsl:call-template>
              </tr>
            </xsl:otherwise>
          </xsl:choose>
          <tr>
            <td>%fldKey%:</td>
            <td>
              <xsl:value-of select="/contents/content/@key"/>
            </td>
          </tr>
          <input type="hidden" name="_pubdata_created" value="{/contents/content/@created}"/>
        </table>
      </fieldset>

      <fieldset>
        <legend>&nbsp;%blockAssignment%&nbsp;</legend>

        <table width="100%" border="0" cellspacing="2" cellpadding="2">
          <tr>
            <xsl:call-template name="labelcolumn">
              <xsl:with-param name="label" select="'%fldAssignee%:'"/>
            </xsl:call-template>
            <td>
              <span id="view_assignee">
                <xsl:value-of select="$assignee-display-name-and-qname"/>
              </span>
              <input type="hidden" name="_assignee" id="_assignee" value="{$assignee-key}"/>
            </td>
          </tr>
          <tr>
            <td>
              &nbsp;
            </td>
            <td>
              <xsl:variable name="currentuser-qualifiedname-escaped-for-javascript">
                <xsl:choose>
                  <xsl:when test="string($currentuser_qualifiedname) = 'admin'">
                    <xsl:value-of select="$currentuser_qualifiedname"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="substring-before($currentuser_qualifiedname, '\')"/>
                    <xsl:text>\\</xsl:text>
                    <xsl:value-of select="substring-after($currentuser_qualifiedname, '\')"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="name" select="'_unassign_button'"/>
                <xsl:with-param name="hidden" select="string($current-user-is-not-assignee)"/>
                <xsl:with-param name="caption" select="'%cmdUnassign%'"/>
                <xsl:with-param name="disabled" select="false()"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:</xsl:text>
                  <xsl:text>content_form_unassign();</xsl:text>
                </xsl:with-param>
              </xsl:call-template>

              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="name" select="'_assign_to_me_button'"/>
                <xsl:with-param name="caption" select="'%cmdAssignToMe%'"/>
                <xsl:with-param name="hidden" select="string($current-user-is-assignee)"/>
                <xsl:with-param name="disabled" select="string($disable-assign-to-me-button)"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:</xsl:text>
                  <xsl:text>content_form_assignToMe(</xsl:text>
                  <xsl:text>'</xsl:text>
                  <xsl:value-of select="$currentuser_key"/>
                  <xsl:text>', </xsl:text>
                  <xsl:text>null,</xsl:text>
                  <xsl:text>'</xsl:text>
                  <xsl:value-of select="concat($currentuser_fullname, ' (', $currentuser-qualifiedname-escaped-for-javascript ,')')"/>
                  <xsl:text>',</xsl:text>
                  <xsl:text>null</xsl:text>
                  <xsl:text>);</xsl:text>
                </xsl:with-param>
              </xsl:call-template>

              <xsl:choose>
                <xsl:when test="$current-user-is-assignee">
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
              </xsl:choose>
              
            </td>
          </tr>
          <tr>
            <xsl:call-template name="labelcolumn">
              <xsl:with-param name="label" select="'%fldAssigner%:'"/>
            </xsl:call-template>
            <td>
              <span id="view_assigner">
                <xsl:value-of select="$assigner-display-name-and-qname"/>
              </span>
              <input type="hidden" name="_assigner" id="_assigner" value="{$assigner-key}"/>
            </td>
          </tr>
          <tr>
            <xsl:call-template name="textfielddatetime">
              <xsl:with-param name="name" select="'_assignment_duedate'"/>
              <xsl:with-param name="id" select="'_assignment_duedate'"/>
              <xsl:with-param name="label" select="'%fldDueDate%:'"/>
              <xsl:with-param name="selectnode" select="$due-date"/>
              <xsl:with-param name="disabled" select="not($is-assigned)"/>
              <xsl:with-param name="readonly" select="not($is-assigned)"/>
            </xsl:call-template>
          </tr>
          <tr>
            <xsl:call-template name="textarea">
              <xsl:with-param name="name" select="'_assignment_description'"/>
              <xsl:with-param name="id" select="'_assignment_description'"/>
              <xsl:with-param name="label" select="'%fldAssignmentDescr%:'"/>
              <xsl:with-param name="selectnode" select="$assignment-description"/>
              <xsl:with-param name="rows" select="'5'"/>
              <xsl:with-param name="cols" select="'45'"/>
              <xsl:with-param name="colspan" select="'1'"/>
              <xsl:with-param name="disabled" select="not($is-assigned)"/>
              <xsl:with-param name="readonly" select="not($is-assigned)"/>
            </xsl:call-template>
          </tr>
        </table>
      </fieldset>

      <fieldset>
        <legend>&nbsp;%blockEventLog%&nbsp;</legend>
        <img src="images/shim.gif" height="4" class="shim" border="0"/>
        <br/>
        <table width="99%" cellspacing="0" cellpadding="0">
          <tr>
            <td colspan="2">
              <xsl:if test="/contents/content/@key">
                <xsl:call-template name="button">
                  <xsl:with-param name="name" select="'vieweventlog'"/>
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdViewEventLog%'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>viewEventLog(0,</xsl:text>
                    <xsl:text>-1,</xsl:text>
                    <xsl:value-of select="/contents/content/@key"/>
                    <xsl:text>, null)</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:if>
            </td>
          </tr>
        </table>
      </fieldset>
    </div>
  </xsl:template>
</xsl:stylesheet>