<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="dropdown_runas">
    <xsl:param name="name"/>
    <xsl:param name="selectedKey"/>
    <xsl:param name="defaultRunAsUserName"/>
    <xsl:param name="inheritMessage"/>

    <select>
      <xsl:attribute name="name">
        <xsl:value-of select="$name"/>
      </xsl:attribute>

      <option value="INHERIT">
        <xsl:if test="$selectedKey = 'INHERIT'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="$inheritMessage"/>
      </option>
      <option value="DEFAULT_USER">
        <xsl:if test="$selectedKey = 'DEFAULT_USER'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        %fldDefaultUser% (<xsl:value-of select="$defaultRunAsUserName"/>)
      </option>
      <option value="PERSONALIZED">
        <xsl:if test="$selectedKey = 'PERSONALIZED'">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        %fldPersonalized%
      </option>
    </select>
  </xsl:template>

</xsl:stylesheet>
