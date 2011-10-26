<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>
  
  <xsl:template name="formatdatetime">
    <xsl:param name="date"/>
    <xsl:param name="includeseconds" select="false()"/>

    <xsl:if test="$date != ''">
      <xsl:variable name="dateTime">
        <xsl:value-of select="substring($date, 9, 2)"/>.<xsl:value-of select="substring($date, 6, 2)"/>.<xsl:value-of select="substring($date, 1, 4)"/>
        <xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="$includeseconds">
            <xsl:value-of select="substring($date, 12, 8)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="substring($date, 12, 5)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:value-of select="normalize-space($dateTime)"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="formatdate">
    <xsl:param name="date"/>

    <xsl:if test="$date != ''">
      <xsl:value-of select="substring($date, 9, 2)"/>.<xsl:value-of select="substring($date, 6, 2)"/>.<xsl:value-of select="substring($date, 1, 4)"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="formattime">
    <xsl:param name="date"/>
    <xsl:param name="includeseconds" select="false()"/>

    <xsl:if test="$date != ''">
      <xsl:choose>
        <xsl:when test="$includeseconds">
          <xsl:value-of select="substring($date, 12, 8)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring($date, 12, 5)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template name="formatmonth">
    <xsl:param name="date"/>
    
    <xsl:variable name="month">
      <xsl:value-of select="substring($date, 6, 2)"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$month = '01'">
        <xsl:text>%monthJanuary%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '02'">
        <xsl:text>%monthFebruary%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '03'">
        <xsl:text>%monthMarch%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '04'">
        <xsl:text>%monthApril%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '05'">
        <xsl:text>%monthMay%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '06'">
        <xsl:text>%monthJune%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '07'">
        <xsl:text>%monthJuly%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '08'">
        <xsl:text>%monthAugust%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '09'">
        <xsl:text>%monthSeptember%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '10'">
        <xsl:text>%monthOctober%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '11'">
        <xsl:text>%monthNovember%</xsl:text>
      </xsl:when>
      <xsl:when test="$month = '12'">
        <xsl:text>%monthDecember%</xsl:text>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
