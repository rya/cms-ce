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

  <xsl:template name="textfielddatetime">
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="id"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan"/>
    <xsl:param name="required" select="false()"/>
    <xsl:param name="indexcallback"/>
    <xsl:param name="includeseconds" select="false()"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="readonly" select="false()"/>
    <xsl:param name="onbluroverridefunction" select="''"/>

    <xsl:if test="string-length($label) &gt; 0">
      <td class="form_labelcolumn" nowrap="nowrap">
          <xsl:value-of select="$label"/>
          <xsl:if test="$required">
              <span class="requiredfield">*</span>
          </xsl:if>
      </td>
    </xsl:if>
    <td nowrap="nowrap">
      <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>

      <table border="0" cellpadding="0" cellspacing="0" class="datetime-grid">
        <tr>
          <td class="date-field">

            <!--
              For validate: Firfox has serious issues when the formElem.focus() method is executed in an onblur handler.
              In some specific validation routines we set focus on this element instead.
             -->
            <a href="javascript:;" id="{concat('date', $name, '_focus_hook')}" style="display:block;position:absolute;left:-1000px">.</a>

            <input type="text" class="textfield">
              <xsl:attribute name="name">date<xsl:value-of select="$name"/></xsl:attribute>
              <xsl:if test="$id !=''">
                <xsl:attribute name="id">date<xsl:value-of select="$id"/></xsl:attribute>
              </xsl:if>
              <xsl:attribute name="value">
                <xsl:call-template name="formatdate">
                  <xsl:with-param name="date" select="$selectnode"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="size">11</xsl:attribute>
              <xsl:attribute name="maxlength">10</xsl:attribute>
              <xsl:attribute name="onblur">
                <xsl:choose>
                  <xsl:when test="$onbluroverridefunction != ''">
                    <xsl:value-of select="$onbluroverridefunction"/>
                  </xsl:when>
                  <xsl:otherwise>
                <xsl:text>validateDate(this);</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
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
             <!-- indexcallback is not in use anymore -->
             <xsl:when test="$indexcallback != ''">
               <input type="button" value="" onclick="calendar.doo.display(document.getElementsByName('date{$name}')[ getObjectIndex(this) ], 'dd.mm.yyyy', this);" id="link{$name}" name="link{$name}">
                 <xsl:if test="$readonly or $disabled">
                   <xsl:attribute name="disabled">
                     <xsl:text>true</xsl:text>
                   </xsl:attribute>
                 </xsl:if>
               </input>
             </xsl:when>
             <xsl:otherwise>
               <input type="button" value="" onclick="calendar.doo.display(document.getElementsByName('date{$name}')[ getObjectIndex(this) ], 'dd.mm.yyyy', this);" class="calendar-icon" id="link{$name}" name="link{$name}">
                 <xsl:if test="$readonly or $disabled">
                   <xsl:attribute name="disabled">
                     <xsl:text>true</xsl:text>
                   </xsl:attribute>
                 </xsl:if>
               </input>
             </xsl:otherwise>
            </xsl:choose>
          </td>
          <td>
            DD.MM.YYYY
          </td>
          <td>
            <!--
              For validate: Firfox has serious issues when the formElem.focus() method is executed in an onblur handler.
              In some specific validation routines we set focus on this element instead.
             -->
            <a href="javascript:;" id="{concat('time', $name, '_focus_hook')}" style="display:block;position:absolute;left:-1000px">.</a>

            <input type="text" class="textfield">
              <xsl:attribute name="name">time<xsl:value-of select="$name"/></xsl:attribute>
              <xsl:if test="$id !=''">
                <xsl:attribute name="id">time<xsl:value-of select="$id"/></xsl:attribute>
              </xsl:if>
              <xsl:attribute name="value">
                <xsl:call-template name="formattime">
                  <xsl:with-param name="date" select="$selectnode"/>
                  <xsl:with-param name="includeseconds" select="$includeseconds"/>
                </xsl:call-template>
              </xsl:attribute>
              <xsl:attribute name="size">6</xsl:attribute>
              <xsl:attribute name="maxlength">
                <xsl:choose>
                  <xsl:when test="$includeseconds">
                    <xsl:text>8</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>5</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:attribute name="onblur">
                <xsl:choose>
                  <xsl:when test="$onbluroverridefunction != ''">
                    <xsl:value-of select="$onbluroverridefunction"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                  <xsl:when test="$includeseconds">
                    <xsl:text>validateTimeSeconds(this);</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>validateTime(this);</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:if test="$disabled">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
              </xsl:if>
              <xsl:if test="$readonly">
                <xsl:attribute name="readonly">true</xsl:attribute>
              </xsl:if>
            </input>
          </td>
          <td>
            <xsl:choose>
              <xsl:when test="$includeseconds">
                <xsl:text> HH:MM:SS</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text> HH:MM</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </td>
        </tr>
      </table>
   </td>
  </xsl:template>
</xsl:stylesheet>

