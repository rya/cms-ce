<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:exslt-common="http://exslt.org/common"
        xmlns:saxon="http://saxon.sf.net/"
        xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="filefield">
    <xsl:param name="label"/>
    <xsl:param name="size"/>
    <xsl:param name="name"/>
    <xsl:param name="id" select="$name"/>
    <xsl:param name="imagekey"/>
    <xsl:param name="colspan"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="onpropertychange"/>
    <xsl:param name="onchange"/>
    <xsl:param name="required" select="'false'"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="lefttdwidth" select="'none'"/>
    <xsl:param name="helpelement"/>

    <xsl:if test="$label != ''">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="width" select="$lefttdwidth"/>
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
        <xsl:with-param name="helpelement" select="$helpelement"/>
        <xsl:with-param name="valign" select="'middle'"/>
      </xsl:call-template>
    </xsl:if>
    <td>
      <xsl:attribute name="colspan">
        <xsl:value-of select="$colspan"/>
      </xsl:attribute>

      <xsl:variable name="errors">
        <xsl:choose>
          <xsl:when test="/*/errors">
            <xsl:copy-of select="/*/errors"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="/*/*/errors"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:if test="exslt-common:node-set($errors)/errors/error[@name=$name]">
        <xsl:call-template name="displayerror">
          <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name=$name]/@code"/>
        </xsl:call-template>
      </xsl:if>

      <xsl:if test="$helpelement">
        <xsl:call-template name="displayhelp">
          <xsl:with-param name="fieldname" select="$name"/>
          <xsl:with-param name="helpelement" select="$helpelement"/>
        </xsl:call-template>
      </xsl:if>
      

      <input type="file">
        <xsl:attribute name="id">
          <xsl:value-of select="$id"/>
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
        <xsl:attribute name="size">
          <xsl:value-of select="$size"/>
        </xsl:attribute>
        <xsl:attribute name="maxlength">
          <xsl:value-of select="$maxlength"/>
        </xsl:attribute>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </xsl:if>
        <xsl:if test="$onpropertychange">
          <xsl:attribute name="onpropertychange">
            <xsl:value-of select="$onpropertychange"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$onchange">
          <xsl:attribute name="onchange">
            <xsl:value-of select="$onchange"/>
          </xsl:attribute>
        </xsl:if>
      </input>
    </td>
  </xsl:template>

</xsl:stylesheet>
