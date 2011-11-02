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

  <xsl:template name="textfielddate">
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="use-date-prefix" select="true()"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan"/>
    <xsl:param name="required" select="false()"/>
    <xsl:param name="indexcallback"/>
    <xsl:param name="helpelement"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="onchange" select="''"/>
    <xsl:param name="formatdate" select="true()"/>
    <xsl:param name="readonly" select="false()"/>
    <xsl:param name="useIcon" select="false()"/>
    <xsl:param name="iconClass" select="''"/>
    <xsl:param name="iconText" select="''"/>

    <xsl:variable name="inputname">
      <xsl:if test="$use-date-prefix = true()">
        <xsl:text>date</xsl:text>
      </xsl:if>
      <xsl:value-of select="$name"/>
    </xsl:variable>

    <xsl:if test="$label != ''">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
        <xsl:with-param name="helpelement" select="$helpelement"/>
        <xsl:with-param name="useIcon" select="$useIcon"/>
        <xsl:with-param name="iconClass" select="$iconClass"/>
        <xsl:with-param name="iconText" select="$iconText"/>
      </xsl:call-template>
    </xsl:if>
    <td nowrap="nowrap">
      <xsl:attribute name="colspan">
        <xsl:value-of select="$colspan"/>
      </xsl:attribute>

      <table border="0" cellpadding="0" cellspacing="0" class="datetime-grid">
        <tr>
          <td class="date-field">

            <xsl:if test="$helpelement">
              <xsl:call-template name="displayhelp">
                <xsl:with-param name="fieldname" select="$name"/>
                <xsl:with-param name="helpelement" select="$helpelement"/>
              </xsl:call-template>
            </xsl:if>

            <input type="text" class="textfield">
              <xsl:attribute name="name"><xsl:value-of select="$inputname"/></xsl:attribute>
              <xsl:attribute name="value">
                <xsl:choose>
                  <xsl:when test="$formatdate = true()">
                    <xsl:call-template name="formatdate">
                      <xsl:with-param name="date" select="$selectnode"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$selectnode"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:attribute name="size">11</xsl:attribute>
              <xsl:attribute name="maxlength">10</xsl:attribute>
              <xsl:attribute name="onchange">
                <xsl:text>validateDate(this)</xsl:text>
                <xsl:if test="$onchange != ''">
                  <xsl:text>;</xsl:text>
                  <xsl:value-of select="$onchange"/>
                </xsl:if>
              </xsl:attribute>
              <xsl:if test="$disabled">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
              </xsl:if>
              <xsl:if test="$readonly">
                <xsl:attribute name="readonly">true</xsl:attribute>
              </xsl:if>

            </input>
          </td>
          <td class="calendar-icon">
            <xsl:choose>
              <xsl:when test="$readonly">
                <br/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="$disabled">
                    <img src="javascript/images/cal.gif" width="16" height="16" border="0" class="disabled-element" style="vertical-align: top"/>
                  </xsl:when>
                  <xsl:when test="$indexcallback!=''">
                    <a href="javascript:;" name="link{$name}" onclick="calendar.doo.display(document.getElementsByName('{$inputname}')[ getObjectIndex(this) ], 'dd.mm.yyyy', this);">
                      <img src="javascript/images/cal.gif" width="16" height="16" border="0" class="hand" style="vertical-align: top"/>
                    </a>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- Is the following depricated ??? -->
                    <a href="javascript:;" name="link{$name}" onclick="calendar.doo.display(document.getElementsByName('{$inputname}')[ getObjectIndex(this) ], 'dd.mm.yyyy', this);">
                      <img src="javascript/images/cal.gif" width="16" height="16" border="0" class="hand"/>
                    </a>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>

          </td>
        </tr>
      </table>
    </td>
  </xsl:template>
</xsl:stylesheet>

