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

  <xsl:template name="assignee-status">
    <xsl:param name="content"/>
    <xsl:param name="selected-version-is-unsaved-draft"/>
    <xsl:param name="editlockedversionmode"/>
    
    <xsl:variable name="logged-in-user-key" select="$currentuser_key"/>
    <xsl:variable name="logged-in-user-name" select="concat($currentuser_fullname, ' (', $currentuser_qualifiedname, ')')"/>
    <xsl:variable name="new-content" select="not($content/@state)"/>
    <xsl:variable name="content-is-assigned" select="$content/@is-assigned = 'true'"/>
    <xsl:variable name="selected-assignee" select="$content/assignee"/>
    <xsl:variable name="photo-square-size" select="26"/>
    <xsl:variable name="photo-filters" select="concat('scalesquare(',$photo-square-size, ');rounded(2)')"/>

    <xsl:variable name="current-assignee-name">
      <xsl:choose>
        <xsl:when test="$new-content">
          <xsl:value-of select="$logged-in-user-name"/>
        </xsl:when>
        <xsl:when test="$content-is-assigned">
          <xsl:value-of select="concat($selected-assignee/display-name, ' (', $selected-assignee/@qualified-name, ')')"/>
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="current-assignee-photo">
      <xsl:choose>
        <xsl:when test="$new-content">
          <xsl:choose>
            <xsl:when test="$currentuser_has_photo = 'true'">
              <xsl:value-of select="concat('_image/user/', $logged-in-user-key, '?_filter=',$photo-filters)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>images/dummy-user-small.png</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="not($new-content) and $selected-assignee/photo/@exists = 'true'">
          <xsl:value-of select="concat('_image/user/', $selected-assignee/@key, '?_filter=',$photo-filters)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>images/dummy-user-small.png</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="new-assignee-name">
      <xsl:value-of select="$logged-in-user-name"/>
    </xsl:variable>

    <xsl:variable name="new-assignee-photo">
      <xsl:choose>
        <xsl:when test="$currentuser_has_photo = 'true'">
          <xsl:value-of select="concat('_image/user/', $logged-in-user-key, '?_filter=',$photo-filters)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>images/dummy-user-small.png</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="email">
      <xsl:choose>
        <xsl:when test="$content-is-assigned">
          <xsl:value-of select="$selected-assignee/email"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$selected-version-is-unsaved-draft or $editlockedversionmode">
            <xsl:value-of select="$currentuser_email"/>
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="hide-new-assignee" select="$new-content or not($editlockedversionmode)"/>

    <div class="assignee-status-box">

      <!--
      DEBUG:<br/>
      $selected-version-is-unsaved-draft: <xsl:value-of select="$selected-version-is-unsaved-draft"/><br/>
      $new-content: <xsl:value-of select="$new-content"/><br/>
      $editlockedversionmode : <xsl:value-of select="$editlockedversionmode"/>
      -->

      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td valign="top" id="assignee-status-current-assignee">
            <div class="assignment assignee-status-user">
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="assignee-photo" style="width:{$photo-square-size}px">
                    <img src="{$current-assignee-photo}" alt="{$current-assignee-name}" title="{$current-assignee-name}"
                         width="{$photo-square-size}" height="{$photo-square-size}"/>
                  </td>
                  <td class="assignee-name-email">
                    <xsl:choose>
                      <xsl:when test="$new-content or $content-is-assigned">
                        <h3>%txtAssignedTo%</h3>
                        <span>
                          <xsl:value-of select="$current-assignee-name"/>
                        </span>
                      </xsl:when>
                      <xsl:otherwise>
                        <h3>%txtUnassigned%</h3>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                </tr>
              </table>
            </div>
          </td>
          <td valign="top" id="assignee-status-unassigned" style="display:none">
            <div class="assignment assignee-status-user">
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="assignee-photo" style="width:{$photo-square-size}px">
                    <img src="images/dummy-user-small.png" alt="%txtNobody%" title="%txtNobody%"
                         width="{$photo-square-size}" height="{$photo-square-size}"/>
                  </td>
                  <td class="assignee-name-email">
                    <h3>%txtUnassigned%</h3>
                  </td>
                </tr>
              </table>
            </div>
          </td>
          <td id="assignee-status-arrow">
            <xsl:attribute name="style">
              <xsl:text>width:18px; text-align:center;</xsl:text>
              <xsl:if test="$hide-new-assignee">
                <xsl:text>display: none;</xsl:text>
              </xsl:if>
            </xsl:attribute>
            <img src="images/icon_right.gif" alt=""/>
          </td>
          <td valign="top" id="assignee-status-new-assignee">
            <xsl:if test="$hide-new-assignee">
              <xsl:attribute name="style">display: none</xsl:attribute>
            </xsl:if>
            
            <div class="assignment assignee-status-user">
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="assignee-photo" style="width:{$photo-square-size}px">
                    <img src="{$new-assignee-photo}" id="new-assignee-photo" alt="{$new-assignee-name}" title="{$new-assignee-name}"
                         width="{$photo-square-size}" height="{$photo-square-size}"/>
                  </td>
                  <td class="assignee-name-email">
                    <h3>%txtNewAssignee%</h3>
                    <span id="new-assignee-name">
                      <xsl:value-of select="$new-assignee-name"/>
                    </span>
                  </td>
                </tr>
              </table>
            </div>
          </td>
          <td id="assignee-status-arrow-2">
            <xsl:attribute name="style">
              <xsl:text>width:18px; text-align:center; display: none;</xsl:text>
            </xsl:attribute>
            <img src="images/icon_right.gif" alt=""/>
          </td>
          <td valign="top" id="assignee-status-unassigned-2" style="display:none">
            <div class="assignment assignee-status-user">
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="assignee-photo" style="width:{$photo-square-size}px">
                    <img src="images/dummy-user-small.png" alt="%txtNobody%" title="%txtNobody%"
                         width="{$photo-square-size}" height="{$photo-square-size}"/>
                  </td>
                  <td class="assignee-name-email">
                    <h3>%txtUnassigned%</h3>
                  </td>
                </tr>
              </table>
            </div>
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>