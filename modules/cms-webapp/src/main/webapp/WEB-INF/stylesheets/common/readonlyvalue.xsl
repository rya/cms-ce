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

  <xsl:template name="readonlyvalue">
    <xsl:param name="label" select="''"/>
    <xsl:param name="name" select="''"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan" select="''"/>
    <xsl:param name="strong" select="false()"/>
    <xsl:param name="italic" select="false()"/>
    <xsl:param name="buttonname" select="''"/>
    <xsl:param name="buttonurl" select="''"/>
    <xsl:param name="buttontarget" select="''"/>

    <xsl:if test="$label != ''">
      <td class="form_labelcolumn" valign="baseline">
        <xsl:value-of select="$label"/>
      </td>
    </xsl:if>

    <td>
      <xsl:if test="$colspan != ''">
        <xsl:attribute name="colspan">
          <xsl:value-of select="$colspan"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="$strong">
          <strong>
            <xsl:value-of select="$selectnode"/>
          </strong>
        </xsl:when>
        <xsl:when test="$italic">
          <i>
            <xsl:value-of select="$selectnode"/>
          </i>
        </xsl:when>
        <xsl:otherwise>
          <span><xsl:value-of select="$selectnode"/></span>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:if test="$name != ''">
        <input type="hidden">
          <xsl:attribute name="name">
            <xsl:value-of select="$name"/>
          </xsl:attribute>
          <xsl:attribute name="value">
            <xsl:value-of select="$selectnode"/>
          </xsl:attribute>
        </input>
      </xsl:if>

      <xsl:if test="$buttonname != '' and $buttonurl != ''">
        <xsl:text>&nbsp;&nbsp;</xsl:text>
        <xsl:call-template name="button">
          <xsl:with-param name="type" select="'link'"/>
          <xsl:with-param name="caption" select="$buttonname"/>
          <xsl:with-param name="name" select="'urlbtn'"/>
          <xsl:with-param name="href" select="$buttonurl"/>
          <xsl:with-param name="target" select="$buttontarget"/>
        </xsl:call-template>
      </xsl:if>
    </td>
  </xsl:template>

</xsl:stylesheet>
