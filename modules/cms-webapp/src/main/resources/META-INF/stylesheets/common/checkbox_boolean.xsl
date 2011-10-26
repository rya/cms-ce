<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="checkbox_boolean">
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="onpropertychange"/>
    <xsl:param name="onclick"/>
    <xsl:param name="order" select="'labelfirst'"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="readonly" select="false()"/>
    <xsl:param name="useIcon" select="false()"/>
    <xsl:param name="iconClass" select="''"/>
    <xsl:param name="iconText" select="''"/>
    <xsl:param name="text-after-checkbox" select="''"/>

    <xsl:if test="$order = 'labelfirst'">
      <td nowrap="nowrap">
        
        <div style="float:left">
          <xsl:value-of select="$label"/>
        </div>

        <xsl:if test="$useIcon != ''">
          <div style="float:right">
            <span class="{$iconClass}">
              <xsl:if test="$iconText !=''">
                <xsl:attribute name="title">
                  <xsl:value-of select="$iconText"/>
                </xsl:attribute>
              </xsl:if>
            </span>
          </div>
        </xsl:if>

      </td>
    </xsl:if>

    <td nowrap="nowrap">
      <xsl:choose>
        <xsl:when test="$readonly">
          <input type="text" name="{$name}" value="{$selectnode}" readonly="true"/> 
        </xsl:when>
        <xsl:otherwise>
          <input type="checkbox">
            <xsl:if test="$selectnode = 'true'">
              <xsl:attribute name="checked"><xsl:value-of select="'checked'"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="$onpropertychange != ''">
              <xsl:attribute name="onpropertychange">
                <xsl:value-of select="$onpropertychange" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="$onclick != ''">
              <xsl:attribute name="onclick">
                <xsl:value-of select="$onclick" />
              </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="value"><xsl:value-of select="'true'"/></xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="$name"/></xsl:attribute>
            <xsl:if test="$disabled">
              <xsl:attribute name="disabled"><xsl:text>disabled</xsl:text></xsl:attribute>
            </xsl:if>
          </input>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:if test="$text-after-checkbox != ''">
        <label>
          <xsl:attribute name="for">
            <xsl:value-of select="$name"/>
          </xsl:attribute>
          <xsl:value-of select="$text-after-checkbox"/>
        </label>
      </xsl:if>

    </td>

    <xsl:if test="$order != 'labelfirst'">
      <td nowrap="nowrap">
        <xsl:value-of select="$label"/>
      </td>
    </xsl:if>

  </xsl:template>
</xsl:stylesheet>
