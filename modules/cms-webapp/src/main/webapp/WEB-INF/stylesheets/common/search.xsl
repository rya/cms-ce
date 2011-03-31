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

  <xsl:template name="searchbuttons">
    <xsl:param name="reportbutton" select="false()"/>
    <xsl:param name="extraparameters" select="''"/>

    <xsl:choose>
      <xsl:when test="$searchtype = 'simple'">
        <xsl:call-template name="button">
          <xsl:with-param name="type" select="'button'"/>
          <xsl:with-param name="caption" select="'%cmdBack%'"/>
          <xsl:with-param name="name" select="'newsearch'"/>
          <xsl:with-param name="onclick" select="'history.back()'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="button">
          <xsl:with-param name="type" select="'link'"/>
          <xsl:with-param name="caption" select="'%cmdNewSearch%'"/>
          <xsl:with-param name="name" select="'newsearch'"/>
          <xsl:with-param name="href">
            <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
            <xsl:text>&amp;op=searchform&amp;contenttypekey=</xsl:text><xsl:value-of select="$contenttypekey"/>
            <xsl:text>&amp;cat=</xsl:text><xsl:value-of select="$cat"/>
            <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/>
            <xsl:value-of select="$extraparameters"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$reportbutton">
      <xsl:text> </xsl:text>
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="caption" select="'%cmdCreateReport%'"/>
        <xsl:with-param name="name" select="'report'"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
          <xsl:text>&amp;op=report&amp;subop=form</xsl:text>
          <xsl:text>&amp;selectedunitkey=</xsl:text>
          <xsl:value-of select="$selectedunitkey"/>
          <xsl:text>&amp;cat=</xsl:text>
          <xsl:value-of select="$cat"/>
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:choose>
            <xsl:when test="$searchtype = 'simple'">
              <xsl:text>&amp;searchtext=</xsl:text>
              <xsl:value-of select="$searchtext"/>
              <xsl:text>&amp;scope=</xsl:text>
              <xsl:value-of select="$scope"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>&amp;asearchtext=</xsl:text>
              <xsl:value-of select="$asearchtext"/>
              <xsl:text>&amp;ascope=</xsl:text>
              <xsl:value-of select="$ascope"/>
              <xsl:text>&amp;subcategories=</xsl:text>
              <xsl:value-of select="$subcategories"/>
              <xsl:text>&amp;status=</xsl:text>
              <xsl:value-of select="$status"/>
              <xsl:text>&amp;owner=</xsl:text>
              <xsl:value-of select="$owner"/>
              <xsl:text>&amp;lastmodified=</xsl:text>
              <xsl:value-of select="$lastmodified"/>
              <xsl:text>&amp;poperator=</xsl:text>
                <xsl:value-of select="$poperator"/>
                <xsl:text>&amp;property=</xsl:text>
                <xsl:value-of select="$priority"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:if>
    <br/><br/>
  </xsl:template>

</xsl:stylesheet>
